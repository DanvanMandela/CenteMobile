package com.craft.silicon.centemobile.view.fragment.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.data.model.converter.LoginDataTypeConverter;
import com.craft.silicon.centemobile.data.model.user.LoginUserData;
import com.craft.silicon.centemobile.data.source.constants.StatusEnum;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.databinding.FragmentAuthBinding;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.MainActivity;
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment;
import com.craft.silicon.centemobile.view.dialog.DialogData;
import com.craft.silicon.centemobile.view.dialog.LoadingFragment;
import com.craft.silicon.centemobile.view.model.AuthViewModel;
import com.craft.silicon.centemobile.view.model.WorkerViewModel;

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
    private CompositeDisposable subscribe = new CompositeDisposable();


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
        return binding.getRoot().getRootView();
    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @Override
    public void setOnClick() {
        binding.materialButton.setOnClickListener(this);
    }

    @Override
    public boolean validateFields() {
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.editPin.getText()).toString())) {
            new ShowToast(requireContext(), getString(R.string.pin_required));
            return false;
        } else return true;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.materialButton)) {
            if (validateFields()) authUser();
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
        if (data.getResponse() != null && data.getResponse().equals("ok")) {
            AlertDialogFragment.newInstance(new DialogData(
                    R.string.error,
                    getString(R.string.something_),
                    R.drawable.warning_app
            ), getChildFragmentManager());
        } else {
            Log.e("WAHH",BaseClass.decryptLatest(data.getResponse(),
                    authViewModel.storage.getDeviceData().getValue().getDevice(),
                    true,
                    ""
            ));
            LoginUserData responseDetails = new LoginDataTypeConverter().to(BaseClass.decryptLatest(data.getResponse(),
                    authViewModel.storage.getDeviceData().getValue().getDevice(),
                    true,
                   ""
            ));
            assert responseDetails != null;
            if (Objects.equals(responseDetails.getStatus(), StatusEnum.FAILED.getType())) {
                AlertDialogFragment.newInstance(new DialogData(
                        R.string.error,
                        Objects.requireNonNull(responseDetails.getMessage()),
                        R.drawable.warning_app
                ), getChildFragmentManager());
            } else {
                new ShowToast(requireContext(), getString(R.string.welcome_back));
                workerViewModel.onLoginData(responseDetails);
                new Handler(Looper.getMainLooper()).postDelayed(() -> ((MainActivity) requireActivity())
                        .provideNavigationGraph()
                        .navigate(authViewModel.navigationDataSource.navigateToHome()), 200);
            }
        }
    }

}