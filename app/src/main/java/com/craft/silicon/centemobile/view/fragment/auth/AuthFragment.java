package com.craft.silicon.centemobile.view.fragment.auth;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.data.model.converter.LoginDataTypeConverter;
import com.craft.silicon.centemobile.data.model.user.ActivationData;
import com.craft.silicon.centemobile.data.model.user.LoginUserData;
import com.craft.silicon.centemobile.data.source.constants.StatusEnum;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.databinding.FragmentAuthBinding;
import com.craft.silicon.centemobile.util.AppLogger;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.MainActivity;
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment;
import com.craft.silicon.centemobile.view.dialog.DialogData;
import com.craft.silicon.centemobile.view.dialog.LoadingFragment;
import com.craft.silicon.centemobile.view.model.AuthViewModel;
import com.craft.silicon.centemobile.view.model.WorkerViewModel;
import com.google.gson.Gson;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AuthFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class AuthFragment extends Fragment implements AppCallbacks, View.OnClickListener {

    private FragmentAuthBinding binding;
    private AuthViewModel authViewModel;
    private WorkerViewModel workerViewModel;
    private final CompositeDisposable subscribe = new CompositeDisposable();


    public AuthFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AuthFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AuthFragment newInstance() {

        return new AuthFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAuthBinding.inflate(inflater, container, false);
        setBinding();
        setOnClick();
        setViewModel();
        setData();
        return binding.getRoot().getRootView();
    }

    private void setData() {
        binding.setData(authViewModel.storage.getActivationData().getValue());
    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @Override
    public void setOnClick() {
        binding.materialButton.setOnClickListener(this);
        binding.forgotPin.setOnClickListener(this);
    }

    @Override
    public boolean validateFields() {
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.editPin.getText()).toString())) {
            new ShowToast(requireContext(), getString(R.string.pin_required), true);
            return false;
        } else return true;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.materialButton)) {
            if (validateFields()) authUser();
        } else if (view.equals(binding.forgotPin)) {
            ((MainActivity) requireActivity())
                    .provideNavigationGraph()
                    .navigate(authViewModel.navigationDataSource.navigateResetPinATM());
        }
    }

    private void authUser() {
        LoadingFragment.show(getChildFragmentManager());
        subscribe.add(authViewModel.loginAccount(Objects.requireNonNull(binding.editPin.getText()).toString(),
                        requireActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setOnSuccess, Throwable::printStackTrace));
    }

    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        workerViewModel = new ViewModelProvider(this).get(WorkerViewModel.class);
    }

    private void setOnSuccess(DynamicResponse data) {
        LoadingFragment.dismiss(getChildFragmentManager());
        try {
            new AppLogger().appLog("LOGIN:Response", BaseClass.decryptLatest(data.getResponse(),
                    authViewModel.storage.getDeviceData().getValue().getDevice(),
                    true,
                    authViewModel.storage.getDeviceData().getValue().getRun()
            ));


            if (data.getResponse() != null && data.getResponse().equals("ok")) {
                AlertDialogFragment.newInstance(new DialogData(
                        R.string.error,
                        getString(R.string.something_),
                        R.drawable.warning_app
                ), getChildFragmentManager());
            } else {
                LoginUserData responseDetails = new LoginDataTypeConverter()
                        .to(BaseClass.decryptLatest(data.getResponse(),
                                authViewModel.storage.getDeviceData().getValue().getDevice(),
                                true,
                                authViewModel.storage.getDeviceData().getValue().getRun()
                        ));


                assert responseDetails != null;
                if (Objects.equals(responseDetails.getStatus(), StatusEnum.FAILED.getType())) {
                    showError(Objects.requireNonNull(responseDetails.getMessage()));
                } else {
                    new ShowToast(requireContext(), getString(R.string.welcome_back));
                    AppLogger.Companion.getInstance().appLog("AUTH",
                            new Gson().toJson(responseDetails));
                    saveUserData(responseDetails);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> ((MainActivity) requireActivity())
                            .provideNavigationGraph()
                            .navigate(authViewModel.navigationDataSource.navigateToHome()), 300);
                }
            }
        } catch (Exception e) {
            showError(getString(R.string.something_));
            e.printStackTrace();
        }

    }

    private void saveUserData(LoginUserData responseDetails) {
        updateActivationData(responseDetails);
        if (responseDetails.getVersion() != null)
            updateWidgets(responseDetails.getVersion());
        if (responseDetails.getAccounts() != null)
            authViewModel.storage.setAccounts(responseDetails.getAccounts());
        if (responseDetails.getBeneficiary() != null)
            authViewModel.storage.setBeneficiary(responseDetails.getBeneficiary());

        if (responseDetails.getModules() != null)
            authViewModel.saveFrequentModule(responseDetails.getModules());
        if (responseDetails.getServiceAlerts() != null)
            authViewModel.storage.setAlerts(responseDetails.getServiceAlerts());

    }

    private void updateWidgets(String version) {
        if (!TextUtils.isEmpty(version)) {
            if (!authViewModel.storage.getVersion().getValue().equals(version))
                workerViewModel.onWidgetData();
            authViewModel.storage.setVersion(version);
        } else
            authViewModel.storage.setVersion("1");
    }

    private void updateActivationData(LoginUserData data) {
        ActivationData userData = authViewModel.storage.getActivationData().getValue();
        userData.setFirstName(data.getFirstName());
        userData.setLastName(data.getLastName());
        userData.setEmail(data.getEmailId());
        userData.setImageURL(data.getImageURL());
        userData.setIDNumber(data.getIDNumber());
        userData.setLoginDate(data.getLoginDate());
        if (userData.getMessage() != null)
            userData.setMessage(data.getMessage());
        authViewModel.storage.setActivationData(userData);
    }


    private void showError(String string) {
        AlertDialogFragment.newInstance(new DialogData(
                R.string.error,
                string,
                R.drawable.warning_app
        ), getChildFragmentManager());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }
}