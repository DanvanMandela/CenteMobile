package com.craft.silicon.centemobile.view.fragment.auth;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.craft.silicon.centemobile.data.model.converter.ResponseTypeConverter;
import com.craft.silicon.centemobile.data.model.user.ActivationData;
import com.craft.silicon.centemobile.data.receiver.SMSData;
import com.craft.silicon.centemobile.data.receiver.SMSReceiver;
import com.craft.silicon.centemobile.data.source.constants.StatusEnum;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseDetails;
import com.craft.silicon.centemobile.databinding.FragmentOtpBinding;
import com.craft.silicon.centemobile.util.AppLogger;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.MainActivity;
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment;
import com.craft.silicon.centemobile.view.dialog.DialogData;
import com.craft.silicon.centemobile.view.dialog.LoadingFragment;
import com.craft.silicon.centemobile.view.fragment.go.steps.OTP;
import com.craft.silicon.centemobile.view.fragment.go.steps.OTPCountDownTimer;
import com.craft.silicon.centemobile.view.model.AuthViewModel;
import com.craft.silicon.centemobile.view.model.BaseViewModel;
import com.craft.silicon.centemobile.view.model.WorkStatus;
import com.craft.silicon.centemobile.view.model.WorkerViewModel;

import org.json.JSONException;
import org.json.JSONObject;

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
public class OtpFragment extends Fragment implements AppCallbacks, View.OnClickListener, SMSData, OTP {

    private FragmentOtpBinding binding;
    private AuthViewModel authViewModel;
    private WorkerViewModel workerViewModel;
    private final CompositeDisposable subscribe = new CompositeDisposable();
    private CountDownTimer countDownTimer;
    private final SMSReceiver smsReceiver = new SMSReceiver();
    private static final String ARG_MOBILE = "mobile";
    private String mobile;
    private BaseViewModel baseViewModel;

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
        setTimer();
        return binding.getRoot().getRootView();
    }

    @Override
    public void setOnClick() {
        binding.materialButton.setOnClickListener(this);
        binding.resendButton.setOnClickListener(this);
    }

    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        workerViewModel = new ViewModelProvider(this).get(WorkerViewModel.class);
        baseViewModel = new ViewModelProvider(this).get(BaseViewModel.class);
    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setModel(authViewModel);
    }

    @Override
    public boolean validateFields() {
        if (TextUtils.isEmpty(binding.verificationCodeEditText.getText())) {
            new ShowToast(requireContext(), getString(R.string.opt_required), true);
            return false;
        } else return true;
    }

    @Override
    public void onClick(View view) {
        if (binding.materialButton.equals(view)) {
            if (validateFields()) verifyOTP();
        } else if (binding.resendButton.equals(view)) {
            resendOTP();
        }
    }

    private void resendOTP() {
        JSONObject jsonObject = new JSONObject();
        try {
            setLoading(true);
            jsonObject.put("MOBILENUMBER", mobile);
            jsonObject.put("SERVICENAME", "ACTIVATION");
            subscribe.add(baseViewModel.createOTP(jsonObject, requireContext())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> {
                        new AppLogger().appLog("OTP:Response",
                                BaseClass.decryptLatest(v.getResponse(),
                                        authViewModel.storage.getDeviceData()
                                                .getValue().getDevice(),
                                        true,
                                        authViewModel.storage.getDeviceData()
                                                .getValue().getRun()
                                ));

                        if (v.getResponse() != null) {
                            if (!v.getResponse().equals(StatusEnum.ERROR.getType())) {
                                setLoading(false);
                                ResponseDetails responseDetails = new ResponseTypeConverter()
                                        .to(BaseClass.decryptLatest(v.getResponse(),
                                                authViewModel.storage
                                                        .getDeviceData().getValue().getDevice(),
                                                true,
                                                authViewModel.storage.getDeviceData().getValue().getRun()
                                        ));
                                if (responseDetails != null) {
                                    if (responseDetails.getStatus().equals(StatusEnum.SUCCESS
                                            .getType())) {
                                        setLoading(false);
                                        setTimer();
                                    } else if (Objects.equals(responseDetails.getStatus(),
                                            StatusEnum.TOKEN.getType())) {
                                        workerViewModel.routeData(getViewLifecycleOwner(),
                                                new WorkStatus() {
                                                    @Override
                                                    public void workDone(boolean b) {
                                                        setLoading(false);
                                                        if (b) resendOTP();
                                                    }

                                                    @Override
                                                    public void progress(int p) {

                                                    }
                                                });
                                    }
                                } else setLoading(false);
                            }
                            setLoading(false);
                        } else setLoading(false);
                    }, Throwable::printStackTrace));
        } catch (JSONException e) {
            e.printStackTrace();
            new ShowToast(requireContext(), getString(R.string.something_));
        }
    }

    private void verifyOTP() {
        setLoading(true);
        subscribe.add(authViewModel.verifyOTP(String.valueOf(binding.verificationCodeEditText.getText()),
                        requireActivity(),
                        mobile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setOnSuccess, Throwable::printStackTrace));

    }

    private void setOnSuccess(DynamicResponse data) {
        try {

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
                    new AppLogger().appLog("ACTIVATION:OTP:Response",
                            BaseClass.decryptLatest(data.getResponse(),
                                    authViewModel.storage.getDeviceData().getValue().getDevice(),
                                    true,
                                    authViewModel.storage.getDeviceData().getValue().getRun()
                            ));

                    ResponseDetails responseDetails = new ResponseTypeConverter()
                            .to(BaseClass.decryptLatest(data.getResponse(),
                                    authViewModel.storage.getDeviceData().getValue().getDevice(),
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
                    } else if (Objects.equals(responseDetails.getStatus(), StatusEnum.TOKEN.getType())) {
                        workerViewModel.routeData(getViewLifecycleOwner(), new WorkStatus() {
                            @Override
                            public void workDone(boolean b) {
                                setLoading(false);
                                if (b) verifyOTP();
                            }

                            @Override
                            public void progress(int p) {

                            }
                        });
                    } else if (responseDetails.getStatus().equals(StatusEnum.SUCCESS.getType())) {
                        setLoading(false);
                        authViewModel.saveActivationData(new ActivationData(responseDetails
                                .getCustomerID(), mobile));
                        new ShowToast(requireContext(), responseDetails.getMessage());
                        new Handler(Looper.getMainLooper()).postDelayed(() ->
                                ((MainActivity) requireActivity())
                                        .provideNavigationGraph()
                                        .navigate(authViewModel
                                                .navigationDataSource.navigateAuth()), 300);
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

    private void setLoading(boolean b) {
        if (b) LoadingFragment.show(getChildFragmentManager());
        else LoadingFragment.dismiss(getChildFragmentManager());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setTimer() {
        binding.resendLay.setVisibility(View.VISIBLE);
        long sData = authViewModel.storage.getOtpState().getValue();
        long startTime = (120 * 1000);
        long interval = 1000;
        if (sData != 0L) {
            countDownTimer = new OTPCountDownTimer(sData, interval, this);
        } else countDownTimer = new OTPCountDownTimer(startTime, interval, this);

        timerControl(true);
        done(false);
    }

    private void timerControl(boolean b) {
        if (b) {
            countDownTimer.start();
        } else {
            countDownTimer.cancel();
        }
    }


    @Override
    public void timer(@NonNull String str) {
        binding.otpTimer.setText(str);
    }

    @Override
    public void done(boolean b) {
        if (b) {
            timerControl(false);
            binding.resendButton.setVisibility(View.VISIBLE);
        } else binding.resendButton.setVisibility(View.GONE);
    }


    @Override
    public void otp(@NonNull String str) {

    }
}