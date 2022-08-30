package com.craft.silicon.centemobile.view.fragment.auth;

import static com.craft.silicon.centemobile.view.binding.BindingAdapterKt.hideSoftKeyboard;
import static com.craft.silicon.centemobile.view.binding.BindingAdapterKt.isOnline;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.data.model.converter.LoginDataTypeConverter;
import com.craft.silicon.centemobile.data.model.user.ActivationData;
import com.craft.silicon.centemobile.data.model.user.LoginUserData;
import com.craft.silicon.centemobile.data.source.constants.StatusEnum;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.sync.SyncData;
import com.craft.silicon.centemobile.databinding.FragmentAuthBinding;
import com.craft.silicon.centemobile.util.AppLogger;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.MainActivity;
import com.craft.silicon.centemobile.view.binding.BindingAdapterKt;
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment;
import com.craft.silicon.centemobile.view.dialog.DialogData;
import com.craft.silicon.centemobile.view.fragment.auth.bio.util.BiometricAuthListener;
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData;
import com.craft.silicon.centemobile.view.model.AuthViewModel;
import com.craft.silicon.centemobile.view.model.WorkStatus;
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
public class AuthFragment extends Fragment implements AppCallbacks, View.OnClickListener,
        BiometricAuthListener {

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
        autoBio();
        bioLogin();
        setFeedbackTimer();
        return binding.getRoot().getRootView();
    }

    private void setFeedbackTimer() {
        int feedback = authViewModel.storage.getFeedbackTimer().getValue();
        feedback = feedback + 1;
        authViewModel.storage.setFeedbackTimer(feedback);
    }

    private void autoBio() {
        int animationDuration = requireContext()
                .getResources().getInteger(R.integer.animation_duration);
        new Handler(Looper.getMainLooper()).postDelayed(this::setFingerPrint, animationDuration);
    }

    private void setFingerPrint() {
        boolean state = authViewModel.storage.getBio().getValue();
        if (state) {
            binding.bioButton.setVisibility(View.VISIBLE);
            if (((MainActivity) requireActivity()).isBiometric()) {
                ((MainActivity) requireActivity()).authenticateTo(this::authUser);
            }
        }
    }

    private void setData() {
        binding.setData(authViewModel.storage.getActivationData().getValue());
    }

    private void bioLogin() {
        binding.bioButton.setOnClickListener(view -> setFingerPrint());
    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void setOnClick() {
        binding.materialButton.setOnClickListener(this);
        binding.forgotPin.setOnClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    @Override
    public boolean validateFields() {
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.editPin.getText()).toString())) {
            new ShowToast(requireContext(), getString(R.string.pin_required), true);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.materialButton)) {
            if (validateFields()) {
                authUser(Objects.requireNonNull(binding.editPin.getText()).toString());

            }
        } else if (view.equals(binding.forgotPin)) {
            BindingAdapterKt.navigate(this,
                    authViewModel.navigationDataSource.navigateResetPinATM());
        }
    }

    private void authUser(String pin) {
        hideSoftKeyboard(requireActivity(), binding.getRoot());
        if (isOnline(requireActivity())) {
            subscribe.add(authViewModel.loginAccount(pin,
                            requireActivity())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> setOnSuccess(v, pin), Throwable::printStackTrace));
            subscribe.add(authViewModel.loading.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setLoading, Throwable::printStackTrace));
        } else new ShowToast(requireContext(), getString(R.string.no_connection), true);

    }

    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        workerViewModel = new ViewModelProvider(this).get(WorkerViewModel.class);
    }

    private void setOnSuccess(DynamicResponse data, String pin) {
        try {
            new AppLogger().appLog("LOGIN:Response", BaseClass.decryptLatest(data.getResponse(),
                    authViewModel.storage.getDeviceData().getValue().getDevice(),
                    true,
                    authViewModel.storage.getDeviceData().getValue().getRun()
            ));

            if (data.getResponse() == null) {
                setLoading(false);
                AlertDialogFragment.newInstance(new DialogData(
                        R.string.error,
                        getString(R.string.fatal_error),
                        R.drawable.warning_app
                ), getChildFragmentManager());
            } else {
                if (data.getResponse().equals("ok")) {
                    setLoading(false);
                    AlertDialogFragment.newInstance(new DialogData(
                            R.string.error,
                            getString(R.string.fatal_error),
                            R.drawable.warning_app
                    ), getChildFragmentManager());
                } else {
                    LoginUserData responseDetails = new LoginDataTypeConverter()
                            .to(BaseClass.decryptLatest(data.getResponse(),
                                    authViewModel.storage.getDeviceData().getValue().getDevice(),
                                    true,
                                    authViewModel.storage.getDeviceData().getValue().getRun()
                            ));
                    if (responseDetails != null) {
                        if (Objects.equals(responseDetails.getStatus(),
                                StatusEnum.FAILED.getType())) {
                            showError(Objects.requireNonNull(responseDetails.getMessage()));
                            setLoading(false);
                        } else if (Objects.equals(responseDetails.getStatus(),
                                StatusEnum.PIN_CHANGE.getType())) {
                            BindingAdapterKt.navigate(this, authViewModel
                                    .navigationDataSource.navigateToChangePin());
                        }
                        if (Objects.equals(responseDetails.getStatus(),
                                StatusEnum.TOKEN.getType())) {
                            workerViewModel.routeData(getViewLifecycleOwner(),
                                    new WorkStatus() {
                                        @Override
                                        public void progress(int p) {

                                        }

                                        @Override
                                        public void error(@Nullable String p) {

                                        }

                                        @Override
                                        public void onOCRData(@NonNull OCRData data, boolean b) {

                                        }

                                        @Override
                                        public void workDone(boolean b) {
                                            if (b) authUser(pin);
                                        }
                                    });
                        } else if (Objects.equals(responseDetails.getStatus(),
                                StatusEnum.SUCCESS.getType())) {
                            new ShowToast(requireContext(), getString(R.string.welcome_back));
                            AppLogger.Companion.getInstance().appLog("AUTH",
                                    new Gson().toJson(responseDetails));
                            saveUserData(responseDetails);
                        } else if (Objects.equals(responseDetails.getStatus(),
                                StatusEnum.PHONE_REG.getType())) {
                            setLoading(false);
                            BindingAdapterKt.navigate(this,
                                    authViewModel.navigationDataSource.navigateToDisclaimer());
                        } else {
                            setLoading(false);
                        }
                    } else {
                        setLoading(false);
                        showError(getString(R.string.parsing_error));
                    }

                }
            }
        } catch (Exception e) {
            setLoading(false);
            showError(getString(R.string.parsing_error));
            e.printStackTrace();
        }

    }


    private void navigate() {
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                {
                    authViewModel.storage.setLoginTime(System.currentTimeMillis());
                    BindingAdapterKt.navigate(this,
                            authViewModel.navigationDataSource.navigateToHome());
                }

                , 1500);
    }

    private void setLoading(boolean b) {
        if (b) binding.motionContainer.setTransition(R.id.loadingState, R.id.userState);
        else binding.motionContainer.setTransition(R.id.userState, R.id.loadingState);
    }

    private void saveUserData(LoginUserData res) {
        updateActivationData(res);
        if (res.getVersion() != null)
            updateWidgets(res.getVersion());
        if (res.getAccounts() != null)
            authViewModel.storage.setAccounts(res.getAccounts());
        if (res.getBeneficiary() != null)
            authViewModel.storage.setBeneficiary(res.getBeneficiary());

        if (res.getModules() != null)
            authViewModel.saveFrequentModule(res.getModules());
        if (res.getServiceAlerts() != null)
            authViewModel.storage.setAlerts(res.getServiceAlerts());
        if (res.getHideModule() != null)
            authViewModel.storage.setHiddenModule(res.getHideModule());

    }


    private void updateWidgets(String version) {
        LiveData<SyncData> liveData = BindingAdapterKt.syncLive(authViewModel.storage.getSync());
        liveData.observe(getViewLifecycleOwner(), live -> {
            if (!authViewModel.storage.getVersion().getValue().equals(version)) {
                if (live != null) {
                    binding.loadingFrame.loading.getRoot().setVisibility(View.VISIBLE);
                    binding.loadingFrame.loading.setData(live);
                    new AppLogger().appLog("PROGRESS", new Gson().toJson(live));
                }
                if (live == null) {
                    if (!authViewModel.storage.getVersion().getValue().equals("1"))
                        workerViewModel.onWidgetData(getViewLifecycleOwner(), null);
                } else {
                    if (live.getWork() == 8) {
                        navigate();
                        authViewModel.storage.setVersion(version);
                    }
                }
            } else navigate();
        });
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
            if (!TextUtils.isEmpty(userData.getMessage()))
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
    public void onDialog() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onBiometricAuthenticationSuccess(@NonNull BiometricPrompt.AuthenticationResult result) {

    }

    @Override
    public void onBiometricAuthenticationError(int errorCode, @NonNull String errorMessage) {

    }


}