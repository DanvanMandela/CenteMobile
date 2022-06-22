package com.craft.silicon.centemobile.view.fragment.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.data.model.converter.ResponseTypeConverter;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.constants.StatusEnum;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseDetails;
import com.craft.silicon.centemobile.databinding.FragmentLoginBinding;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.MainActivity;
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment;
import com.craft.silicon.centemobile.view.dialog.DialogData;
import com.craft.silicon.centemobile.view.dialog.LoadingFragment;
import com.craft.silicon.centemobile.view.model.AuthViewModel;

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

        binding.materialButton.setOnClickListener(v -> authAccount());
        return binding.getRoot().getRootView();
    }

    private void authAccount() {
        LoadingFragment.show(getChildFragmentManager());
        CompositeDisposable subscribe = new CompositeDisposable();
        subscribe.add(authViewModel.loading
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showProgress, Throwable::printStackTrace));
        subscribe.add(authViewModel.activateAccount(Constants.setMobile(binding.countryCodeHolder.getSelectedCountryCode(),
                                Objects.requireNonNull(binding.editMobile.getText()).toString()),
                        Objects.requireNonNull(binding.editPin.getText()).toString(),
                        requireActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    LoadingFragment.dismiss(getChildFragmentManager());
                    if (data.getResponse() != null && data.getResponse().equals("ok")) {
                        AlertDialogFragment.newInstance(new DialogData(
                                R.string.error,
                                getString(R.string.something_),
                                R.drawable.warning_app
                        ), getChildFragmentManager());
                    } else {
                        try {
                            ResponseDetails responseDetails = new ResponseTypeConverter().to(BaseClass.decryptLatest(data.getResponse(),
                                    authViewModel.storage.getDeviceData().getValue().getDevice(),
                                    true,
                                    ""
                            ));
                            assert responseDetails != null;
                            if (responseDetails.getStatus().equals(StatusEnum.FAILED.getType())) {
                                AlertDialogFragment.newInstance(new DialogData(
                                        R.string.error,
                                        Objects.requireNonNull(responseDetails.getMessage()),
                                        R.drawable.warning_app
                                ), getChildFragmentManager());
                            } else if (responseDetails.getStatus().equals(StatusEnum.SUCCESS.getType())) {
                                new ShowToast(requireContext(), responseDetails.getMessage());
                                ((MainActivity) requireActivity())
                                        .provideNavigationGraph()
                                        .navigate(authViewModel.navigationDataSource.navigateToOTP(Constants.setMobile(binding.countryCodeHolder.getSelectedCountryCode(),
                                                Objects.requireNonNull(binding.editMobile.getText()).toString())));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertDialogFragment.newInstance(new DialogData(
                                    R.string.error,
                                    getString(R.string.something_),
                                    R.drawable.warning_app
                            ), getChildFragmentManager());
                        }
                    }
                }, Throwable::printStackTrace));

    }

    private void showProgress(boolean data) {
        // if (data) LoadingFragment.show(getChildFragmentManager());

    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

}