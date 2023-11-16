package com.elmacentemobile.view.fragment.auth;

import static com.elmacentemobile.view.binding.BindingAdapterKt.activationData;
import static com.elmacentemobile.view.binding.BindingAdapterKt.hideSoftKeyboard;
import static com.elmacentemobile.view.binding.BindingAdapterKt.isOnline;
import static com.elmacentemobile.view.binding.BindingAdapterKt.pinLive;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.elmacentemobile.R;
import com.elmacentemobile.data.model.LanguageVersion;
import com.elmacentemobile.data.model.control.FormControl;
import com.elmacentemobile.data.model.control.PasswordEnum;
import com.elmacentemobile.data.model.converter.DynamicAPIResponseConverter;
import com.elmacentemobile.data.model.converter.LoginDataTypeConverter;
import com.elmacentemobile.data.model.dynamic.DynamicAPIResponse;
import com.elmacentemobile.data.model.module.ModuleCategory;
import com.elmacentemobile.data.model.module.Modules;
import com.elmacentemobile.data.model.user.ActivationData;
import com.elmacentemobile.data.model.user.DisplayHash;
import com.elmacentemobile.data.model.user.LoginUserData;
import com.elmacentemobile.data.model.user.PendingTransaction;
import com.elmacentemobile.data.model.user.PendingTrxActionControls;
import com.elmacentemobile.data.model.user.PendingTrxFormControls;
import com.elmacentemobile.data.source.constants.StatusEnum;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.sync.SyncData;
import com.elmacentemobile.databinding.FragmentAuthBinding;
import com.elmacentemobile.util.AppLogger;
import com.elmacentemobile.util.BaseClass;
import com.elmacentemobile.util.ShowToast;
import com.elmacentemobile.util.callbacks.AppCallbacks;
import com.elmacentemobile.view.activity.MainActivity;
import com.elmacentemobile.view.activity.level.FalconHeavyActivity;
import com.elmacentemobile.view.binding.BindingAdapterKt;
import com.elmacentemobile.view.composable.keyboard.CustomKeyData;
import com.elmacentemobile.view.composable.keyboard.CustomKeyboard;
import com.elmacentemobile.view.dialog.AlertDialogFragment;
import com.elmacentemobile.view.dialog.DialogData;
import com.elmacentemobile.view.ep.data.BusData;
import com.elmacentemobile.view.ep.data.BusDataTypeConverter;
import com.elmacentemobile.view.ep.data.GroupForm;
import com.elmacentemobile.view.ep.data.GroupModule;
import com.elmacentemobile.view.fragment.auth.bio.util.BiometricAuthListener;
import com.elmacentemobile.view.fragment.go.steps.OCRData;
import com.elmacentemobile.view.fragment.home.HomeFragment;
import com.elmacentemobile.view.model.AuthViewModel;
import com.elmacentemobile.view.model.WidgetViewModel;
import com.elmacentemobile.view.model.WorkStatus;
import com.elmacentemobile.view.model.WorkerViewModel;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

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
    private WidgetViewModel widgetViewModel;
    private final CompositeDisposable subscribe = new CompositeDisposable();

    private Stack<String> pinStack;


    private MutableLiveData<DynamicAPIResponse> serverResponse = new MutableLiveData<>();


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
        setPinType();
        showKeyBoard();
        return binding.getRoot().getRootView();
    }


    private void showKeyBoard() {
        binding.editPin.setOnClickListener((v ->
                CustomKeyboard.Companion.instance(getChildFragmentManager(),
                        this)));
        authViewModel.loginPin.observe(getViewLifecycleOwner(), strings -> {
            pinStack = strings;
            StringBuilder builder = new StringBuilder();
            for (String s : strings) {
                builder.append(s);
            }
            binding.editPin.setText(builder);
        });
    }

    @Override
    public void onType(CustomKeyData data) {
        switch (data.getType()) {
            case Push: {
                pinStack.push(data.getStr());
                authViewModel.loginPin.setValue(pinStack);
                break;
            }
            case Pop: {
                if (!pinStack.isEmpty()) {
                    pinStack.pop();
                    authViewModel.loginPin.setValue(pinStack);
                }
                break;
            }
        }
    }

    private void setPinType() {
        pinLive(authViewModel.storage.getPasswordType()).observe(getViewLifecycleOwner(), passType -> {
            if (passType != null && !TextUtils.isEmpty(passType)) {
                AppLogger.Companion.getInstance().appLog("PIN:TYPE", passType);
                new AppLogger().appLog(AuthFragment.class.getSimpleName(), passType);
                if (passType.equals(PasswordEnum.TEXT_PASSWORD.getType())) {
                    binding.editPin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else if (passType.equals(PasswordEnum.WEB_PASSWORD.getType())) {
                    binding.editPin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
                } else if (passType.equals(PasswordEnum.NUMERIC_PASSWORD.getType())) {
                    binding.editPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                }
            }
        });
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
        widgetViewModel.deletePendingTransactions();
        activationData(authViewModel.storage.getActivationData()).observe(getViewLifecycleOwner(),
                activationData -> binding.setData(activationData));

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
        binding.privacyPolicy.setOnClickListener(v ->
                openUrl("https://www.centenarybank.co.ug/privacy-policy"));
    }

    @Override
    public void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
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
                authUser(Objects
                        .requireNonNull(binding.editPin.getText()).toString());
            }
        } else if (view.equals(binding.forgotPin)) {
            BindingAdapterKt.navigate(this,
                    authViewModel.navigationDataSource.navigateResetPinATM());
        }
    }

    private void authUser(String pin) {
        hideSoftKeyboard(requireActivity(), binding.getRoot());
        if (isOnline(requireActivity())) {
            setLoading(true);
            workerViewModel.routeData(getViewLifecycleOwner(), new WorkStatus() {
                @Override
                public void workDone(boolean b) {
                    if (b) {
                        subscribe.add(authViewModel.loginAccount(pin,
                                        requireActivity())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(v -> setOnSuccess(v, pin), Throwable::printStackTrace));
                        subscribe.add(authViewModel.loading.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(v -> setLoading(v), Throwable::printStackTrace));
                    }
                }

                @Override
                public void progress(int p) {

                }

                @Override
                public void error(@Nullable String p) {

                }

                @Override
                public void onOCRData(@NonNull OCRData data, boolean b) {

                }
            });
        } else new ShowToast(requireContext(), getString(R.string.no_connection), true);

    }

    @Override
    public void setViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        workerViewModel = new ViewModelProvider(this).get(WorkerViewModel.class);
        widgetViewModel = new ViewModelProvider(this).get(WidgetViewModel.class);
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

//                            BindingAdapterKt.navigate(this, authViewModel
//                                    .navigationDataSource.navigateToChangePin());
                            DynamicAPIResponse dynamicAPIResponse = new DynamicAPIResponseConverter().to(
                                    BaseClass.decryptLatest(data.getResponse(),
                                            authViewModel.storage.getDeviceData()
                                                    .getValue().getDevice(),
                                            true,
                                            authViewModel.storage.getDeviceData()
                                                    .getValue().getRun()));
                            serverResponse.setValue(dynamicAPIResponse);

                            new AppLogger().appLog("RESPONSE", new Gson().toJson(dynamicAPIResponse.getFormField()));
                            binding.editPin.setText("");
                            setLoading(false);
                            getModule(responseDetails.getFormID(),
                                    responseDetails.getNext());
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
                                StatusEnum.DYNAMIC_FORM.getType())) {
                            setLoading(false);
                            getModule(responseDetails.getFormID(),
                                    responseDetails.getNext());
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            authViewModel.storage.setInactivity(false);
            authViewModel.storage.setLoginTime(System.currentTimeMillis());
            BindingAdapterKt.navigate(this,
                    authViewModel.navigationDataSource.navigateToHome());
        }, 3500);
    }

    private void setLoading(boolean b) {
        if (b) binding.motionContainer.setTransition(R.id.loadingState, R.id.userState);
        else binding.motionContainer.setTransition(R.id.userState, R.id.loadingState);
    }

    private void saveUserData(LoginUserData res) {
        updateActivationData(res);
        if (res.getVersion() != null)
            updateWidgets(new LanguageVersion(res.getCustomerType(), res.getVersion()));
        else updateWidgets(new LanguageVersion("ENG", "R"));


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
        else authViewModel.storage.removeHiddenModule();

        if (res.getDisableModule() != null)
            authViewModel.storage.setDisableModule(res.getDisableModule());
        else authViewModel.storage.removeDisableModule();

        if (res.getPendingTrxDisplay() != null) {
            setPending(res);
        }


    }

    @SuppressLint("NewApi")
    private void setPending(LoginUserData res) {
        try {
            for (HashMap<String, String> map : Objects.requireNonNull(res.getPendingTrxDisplay())) {
                for (HashMap<String, String> pay : Objects.requireNonNull(res.getPendingTrxPayload())) {
                    if (Objects.requireNonNull(pay.get("PendingUniqueID"))
                            .equals(map.get("PendingUniqueID"))) {
                        List<PendingTrxFormControls> forms =
                                Objects.requireNonNull(res.getPendingTrxFormControls()).stream()
                                        .filter(a -> Objects.equals(a.getModuleID(), pay.get("ModuleID")))
                                        .collect(Collectors.toList());

                        List<PendingTrxActionControls> action =
                                Objects.requireNonNull(res.getPendingTrxActionControls()).stream()
                                        .filter(a -> Objects.equals(a.getModuleID(), pay.get("ModuleID")))
                                        .collect(Collectors.toList());

                        PendingTransaction pendingTransaction = new PendingTransaction(
                                new DisplayHash(map), new DisplayHash(pay), forms, action);
                        widgetViewModel.savePendingTransaction(pendingTransaction);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void updateWidgets(LanguageVersion version) {
        AppLogger.Companion.getInstance().appLog("CURRENT:VERSION",
                "" + authViewModel.storage.getVersion().getValue());
        AppLogger.Companion.getInstance().appLog("VERSION", "" + version.getVersion());
        AppLogger.Companion.getInstance().appLog("LANGUAGE", "" + version.getLanguage());
        AppLogger.Companion.getInstance().appLog("CURRENT:LANGUAGE",
                "" + authViewModel.storage.getAccountType().getValue());
        LiveData<SyncData> liveData = BindingAdapterKt.syncLive(authViewModel.storage.getSync());
        liveData.observe(getViewLifecycleOwner(), live -> {
            if (!authViewModel.storage.getVersion().getValue().equals(version.getVersion())) {
                if (live != null) {
                    binding.loadingFrame.loading.getRoot().setVisibility(View.VISIBLE);
                    binding.loadingFrame.loading.setData(live);
                    new AppLogger().appLog("PROGRESS", new Gson().toJson(live));
                    if (live.getWork() >= 8) {
                        navigate();
                        authViewModel.storage
                                .setVersion(version.getVersion());
                        authViewModel.storage
                                .setSync(new SyncData(null, 0, true));
                    } else if (live.getWork() <= 1) {
                        if (!authViewModel.storage.getVersion().getValue().equals("R"))
                            workerViewModel.onWidgetData(getViewLifecycleOwner(), null);
                        else navigate();
                    }
                }
            } else if (version.getLanguage() != null &&
                    !Objects.equals(version.getLanguage(),
                            authViewModel.storage.getAccountType().getValue())) {
                authViewModel.storage.setVersion("L");
                authViewModel.storage.accountType(version.getLanguage());
                updateWidgets(version);
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
        if (data.getMessage() != null && !TextUtils.isEmpty(data.getMessage())) {
            new AppLogger().appLog("MESSAGE:LOCAL", "" +
                    authViewModel.storage.getActivationData().getValue().getMessage());
            new AppLogger().appLog("MESSAGE:REMOTE", data.getMessage());
            userData.setMessage(data.getMessage());
        }
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

    private void navigateTo(Modules modules) {
        if (Objects.equals(modules.getModuleCategory(), ModuleCategory.BLOCK.getType())) {
            subscribe.add(widgetViewModel.getModules(modules.getModuleID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(m -> setDynamicModules(m, modules), Throwable::printStackTrace));
        } else getFormControl(modules);
    }

    private void getFormControl(Modules modules) {
        subscribe.add(widgetViewModel.getFormControl(modules.getModuleID(), "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(f -> setFormNavigation(f, modules), Throwable::printStackTrace));
    }

    @Override
    public void setFormNavigation(List<FormControl> forms, Modules modules) {
        AppLogger.Companion.getInstance().appLog(HomeFragment.class.getSimpleName(),
                new Gson().toJson(modules));
        EventBus.getDefault().removeStickyEvent(BusData.class);
        EventBus.getDefault().postSticky(new BusData(new GroupForm(modules,
                null,
                forms,
                false),
                serverResponse.getValue(),
                null));
        Intent i = new Intent(getActivity(), FalconHeavyActivity.class);
        startActivity(i);
    }


    private void setDynamicModules(List<Modules> m, Modules modules) {
        EventBus.getDefault().removeStickyEvent(BusData.class);
        EventBus.getDefault().postSticky(new BusData(new GroupModule(modules, m),
                null,
                null));
        Intent i = new Intent(getActivity(), FalconHeavyActivity.class);
        startActivity(i);
    }

    private void getModule(String module, String next) {
        AppLogger.Companion.getInstance().appLog("FORM", module);
        AppLogger.Companion.getInstance().appLog("NEXT:", next);
        subscribe.add(widgetViewModel.getModule(module)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                    if (v != null) setNextForm(v, next);
                    else new AppLogger().appLog(AuthFragment.class.getSimpleName(), "No Module");
                }, Throwable::printStackTrace));
    }

    private void setNextForm(Modules v, String next) {
        AppLogger.Companion.getInstance().appLog("MODULE", new Gson().toJson(v));
        subscribe.add(widgetViewModel.getFormControl(v.getModuleID(), next)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> setFormNavigation(list, v), Throwable::printStackTrace));
    }

}