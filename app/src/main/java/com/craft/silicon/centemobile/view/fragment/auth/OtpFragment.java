package com.craft.silicon.centemobile.view.fragment.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.databinding.FragmentOtpBinding;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.dialog.LoadingFragment;
import com.craft.silicon.centemobile.view.model.AuthViewModel;

import java.util.concurrent.atomic.AtomicBoolean;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
public class OtpFragment extends Fragment implements AppCallbacks, View.OnClickListener {

    private FragmentOtpBinding binding;
    private AuthViewModel authViewModel;
    private CompositeDisposable subscribe = new CompositeDisposable();


    public OtpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OtpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtpFragment newInstance() {
        return new OtpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        setViewModel();
        setBinding();
        setOnClick();
        return binding.getRoot().getRootView();
    }

    @Override
    public void setOnClick() {
        binding.materialButton.setOnClickListener(this);
    }

    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setModel(authViewModel);
    }

    @Override
    public boolean validateFields() {
        return !TextUtils.isEmpty(binding.verificationCodeEditText.getText());
    }

    @Override
    public void onClick(View view) {
        if (binding.materialButton.equals(view)) {
            verifyOTP();
        }
    }

    private void verifyOTP() {
        if (validateFields()) {
            subscribe.add(authViewModel.verifyOTP(String.valueOf(binding.verificationCodeEditText.getText()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(data -> {
                    }, Throwable::printStackTrace));
            subscribe.add(authViewModel.loading
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::showProgress, Throwable::printStackTrace));

        } else new ShowToast(requireContext(), getString(R.string.otp_required));
    }

    private void showProgress(boolean data) {
        if (data) LoadingFragment.show(getChildFragmentManager());

    }
}