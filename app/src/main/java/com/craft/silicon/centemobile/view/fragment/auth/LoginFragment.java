package com.craft.silicon.centemobile.view.fragment.auth;

import static com.craft.silicon.centemobile.view.binding.BindingAdapterKt.hideSoftKeyboard;
import static com.craft.silicon.centemobile.view.binding.BindingAdapterKt.isOnline;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.data.model.converter.ResponseTypeConverter;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.constants.StatusEnum;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseDetails;
import com.craft.silicon.centemobile.databinding.FragmentLoginBinding;
import com.craft.silicon.centemobile.util.AppLogger;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.MainActivity;
import com.craft.silicon.centemobile.view.binding.BindingAdapterKt;
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment;
import com.craft.silicon.centemobile.view.dialog.DialogData;
import com.craft.silicon.centemobile.view.dialog.LoadingFragment;
import com.craft.silicon.centemobile.view.ep.data.ActivateData;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;
import com.craft.silicon.centemobile.view.model.AuthViewModel;
import com.craft.silicon.centemobile.view.model.WorkStatus;
import com.craft.silicon.centemobile.view.model.WorkerViewModel;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
public class LoginFragment extends Fragment implements AppCallbacks {
    private FragmentLoginBinding binding;
    private AuthViewModel authViewModel;
    private WorkerViewModel workerViewModel;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        setBinding();
        setViewModel();

        setOnClick();
        return binding.getRoot().getRootView();
    }

    @Override
    public void setOnClick() {
        binding.materialButton.setOnClickListener(v ->
                {
                    if (validateFields()) authAccount();
                }
        );

        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        binding.forgotPin.setOnClickListener(view -> BindingAdapterKt.navigate(this,
                authViewModel.navigationDataSource.navigateToPreResetPin()));
    }

    @Override
    public boolean validateFields() {
        boolean isValid = true;
        if (TextUtils.isEmpty(binding.editMobile.getText())) {
            new ShowToast(requireContext(), getString(R.string.mobile_required), true);
            isValid = false;
        } else if (TextUtils.isEmpty(binding.editPin.getText())) {
            new ShowToast(requireContext(), getString(R.string.enter_pin), true);
        }
        return isValid;
    }

    private void authAccount() {
        hideSoftKeyboard(requireActivity(), binding.getRoot());
        if (isOnline(requireActivity())) {
            setLoading(true);
            CompositeDisposable subscribe = new CompositeDisposable();
            subscribe.add(authViewModel.activateAccount(Constants.
                                    setMobile(binding.countryCodeHolder.getSelectedCountryCode(),
                                            Objects.requireNonNull(binding.editMobile.getText()).toString()),
                            Objects.requireNonNull(binding.editPin.getText()).toString(),
                            requireActivity())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setOnSuccess, Throwable::printStackTrace));
        } else new ShowToast(requireContext(), getString(R.string.no_connection), true);
    }

    private void setOnSuccess(DynamicResponse data) {
        ((MainActivity) requireActivity()).initSMSBroadCast();
        try {
            new AppLogger().appLog("ACTIVATION:Response",
                    BaseClass.decryptLatest(data.getResponse(),
                            authViewModel.storage.getDeviceData().getValue().getDevice(),
                            true,
                            authViewModel.storage.getDeviceData().getValue().getRun()
                    ));

            if (data.getResponse() == null) {
                setLoading(false);
                AlertDialogFragment.newInstance(new DialogData(
                        R.string.error,
                        getString(R.string.something_),
                        R.drawable.warning_app
                ), getChildFragmentManager());
            } else {
                if (data.getResponse().equals("ok")) {
                    setLoading(false);
                    AlertDialogFragment.newInstance(new DialogData(
                            R.string.error,
                            getString(R.string.something_),
                            R.drawable.warning_app
                    ), getChildFragmentManager());
                } else {
                    ResponseDetails responseDetails = new ResponseTypeConverter()
                            .to(BaseClass.decryptLatest(data.getResponse(),
                                    authViewModel.storage
                                            .getDeviceData().getValue().getDevice(),
                                    true,
                                    authViewModel.storage.getDeviceData().getValue().getRun()
                            ));
                    assert responseDetails != null;
                    if (responseDetails.getStatus().equals(StatusEnum.FAILED.getType())) {
                        setLoading(false);
                        AlertDialogFragment.newInstance(new DialogData(
                                R.string.error,
                                Objects.requireNonNull(responseDetails.getMessage()),
                                R.drawable.warning_app
                        ), getChildFragmentManager());
                    } else if (Objects.equals(responseDetails.getStatus(),
                            StatusEnum.TOKEN.getType())) {
                        workerViewModel.routeData(getViewLifecycleOwner(), new WorkStatus() {
                            @Override
                            public void workDone(boolean b) {
                                setLoading(false);
                                if (b) authAccount();
                            }

                            @Override
                            public void onOCRData(@NonNull OCRData data, boolean b) {

                            }

                            @Override
                            public void error(@Nullable String p) {

                            }

                            @Override
                            public void progress(int p) {

                            }
                        });
                    } else if (responseDetails.getStatus().equals(StatusEnum.SUCCESS.getType())) {
                        setLoading(false);
                        new ShowToast(requireContext(), responseDetails.getMessage());
                        String mobile = Constants
                                .setMobile(binding.countryCodeHolder
                                                .getSelectedCountryCode(),
                                        Objects.requireNonNull(binding
                                                        .editMobile.getText())
                                                .toString());
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                BindingAdapterKt.navigate(this,
                                        authViewModel.navigationDataSource.navigateToOTP(
                                                new ActivateData(
                                                        mobile,
                                                        Objects.requireNonNull(binding
                                                                .editPin.getText()).toString()
                                                )
                                        )), 1500);


                    }
                }
            }

        } catch (Exception e) {
            setLoading(false);
            e.printStackTrace();
            AlertDialogFragment.newInstance(new DialogData(
                    R.string.error,
                    getString(R.string.fatal_error),
                    R.drawable.warning_app
            ), getChildFragmentManager());
        }
    }

    private void setLoading(boolean b) {
        if (b) LoadingFragment.show(getChildFragmentManager());
        else LoadingFragment.dismiss(getChildFragmentManager());
    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        workerViewModel = new ViewModelProvider(this).get(WorkerViewModel.class);
    }

}