package com.craft.silicon.centemobile.view.fragment.auth;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.craft.silicon.centemobile.data.model.converter.ResponseTypeConverter;
import com.craft.silicon.centemobile.data.model.user.ActivationData;
import com.craft.silicon.centemobile.data.receiver.SMSData;
import com.craft.silicon.centemobile.data.receiver.SMSReceiver;
import com.craft.silicon.centemobile.data.source.constants.StatusEnum;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseDetails;
import com.craft.silicon.centemobile.databinding.FragmentOtpBinding;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.MainActivity;
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment;
import com.craft.silicon.centemobile.view.dialog.DialogData;
import com.craft.silicon.centemobile.view.dialog.LoadingFragment;
import com.craft.silicon.centemobile.view.model.AuthViewModel;
import com.google.gson.Gson;

import java.util.Objects;

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
public class OtpFragment extends Fragment implements AppCallbacks, View.OnClickListener, SMSData {

    private FragmentOtpBinding binding;
    private AuthViewModel authViewModel;
    private final CompositeDisposable subscribe = new CompositeDisposable();

    private final SMSReceiver smsReceiver = new SMSReceiver();
    private static final String ARG_MOBILE = "mobile";
    private String mobile;

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
    public static OtpFragment newInstance(String mobile) {
        OtpFragment fragment = new OtpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOBILE, mobile);
        fragment.setArguments(args);
        return new OtpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mobile = getArguments().getString(ARG_MOBILE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOtpBinding.inflate(inflater, container, false);
        setViewModel();
        setBinding();
        setOnClick();
        setBroadcastListener();
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
        if (TextUtils.isEmpty(binding.verificationCodeEditText.getText())) {
            new ShowToast(requireContext(), getString(R.string.opt_required));
            return false;
        } else return true;
    }

    @Override
    public void onClick(View view) {
        if (binding.materialButton.equals(view)) {
            if (validateFields()) verifyOTP();
        } else if (binding.resendOTP.equals(view)) {
            resendOTP();
        }
    }

    private void resendOTP() {

    }

    private void verifyOTP() {
        setTimer();
        LoadingFragment.show(getChildFragmentManager());
        subscribe.add(authViewModel.verifyOTP(String.valueOf(binding.verificationCodeEditText.getText()),
                        requireActivity(),
                        mobile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    LoadingFragment.dismiss(getChildFragmentManager());
                    Log.e("OTP", new Gson().toJson(data));
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
                            authViewModel.saveActivationData(new ActivationData(responseDetails.getCustomerID(), mobile));
                            new ShowToast(requireContext(), responseDetails.getMessage());
                            new Handler(Looper.getMainLooper()).postDelayed(() -> ((MainActivity) requireActivity())
                                    .provideNavigationGraph()
                                    .navigate(authViewModel.navigationDataSource.navigateAuth()), 300);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertDialogFragment.newInstance(new DialogData(
                                R.string.error,
                                getString(R.string.something_),
                                R.drawable.warning_app
                        ), getChildFragmentManager());
                    }
                }, Throwable::printStackTrace));
        subscribe.add(authViewModel.loading
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showProgress, Throwable::printStackTrace));


    }

    private void showProgress(boolean data) {
        //if (data) LoadingFragment.show(getChildFragmentManager());
    }

    private void setTimer() {
        binding.counter.setVisibility(View.VISIBLE);
        long maxCounter = 30000;
        long diff = 1000;
        new CountDownTimer(maxCounter, diff) {

            public void onTick(long millisUntilFinished) {
                long diff = maxCounter - millisUntilFinished;
                binding.counter.setText(String.valueOf(diff / 1000));
            }

            public void onFinish() {
                binding.resendOTP.setVisibility(View.VISIBLE);
                binding.counter.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    public void setBroadcastListener() {
        smsReceiver.setOnSMS(this);
        requireActivity().registerReceiver(smsReceiver, new IntentFilter("SMSReceived"));
    }

    @Override
    public void onSMS(@NonNull String sms) {
        binding.verificationCodeEditText.setText(sms);
    }

    @Override
    public void onResume() {
        super.onResume();
        setBroadcastListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        setBroadcastListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(smsReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}