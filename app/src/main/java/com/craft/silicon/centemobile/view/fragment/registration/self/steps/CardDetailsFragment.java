package com.craft.silicon.centemobile.view.fragment.registration.self.steps;

import static com.craft.silicon.centemobile.util.TextHelper.CARD_NUMBER_DIVIDER;
import static com.craft.silicon.centemobile.util.TextHelper.CARD_NUMBER_DIVIDER_MODULO;
import static com.craft.silicon.centemobile.util.TextHelper.CARD_NUMBER_DIVIDER_POSITION;
import static com.craft.silicon.centemobile.util.TextHelper.CARD_NUMBER_TOTAL_DIGITS;
import static com.craft.silicon.centemobile.util.TextHelper.CARD_NUMBER_TOTAL_SYMBOLS;
import static com.craft.silicon.centemobile.util.TextHelper.concatString;
import static com.craft.silicon.centemobile.util.TextHelper.getDigitArray;
import static com.craft.silicon.centemobile.util.TextHelper.isInputCorrect;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.craft.silicon.centemobile.R;
import com.craft.silicon.centemobile.data.model.converter.GlobalResponseTypeConverter;
import com.craft.silicon.centemobile.data.model.dynamic.GlobalResponse;
import com.craft.silicon.centemobile.data.source.constants.StatusEnum;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.databinding.FragmentCardDetailsBinding;
import com.craft.silicon.centemobile.util.AppLogger;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.util.ShowToast;
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks;
import com.craft.silicon.centemobile.view.activity.MainActivity;
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment;
import com.craft.silicon.centemobile.view.dialog.DialogData;
import com.craft.silicon.centemobile.view.dialog.LoadingFragment;
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment;
import com.craft.silicon.centemobile.view.model.BaseViewModel;
import com.craft.silicon.centemobile.view.model.WidgetViewModel;
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
 * Use the {@link CardDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class CardDetailsFragment extends Fragment implements AppCallbacks, View.OnClickListener, OTP {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private FragmentCardDetailsBinding binding;
    private BaseViewModel baseViewModel;
    private WorkerViewModel workerViewModel;
    private WidgetViewModel widgetViewModel;
    private static final String ARG_MOBILE = "mobile";

    private final CompositeDisposable disposable = new CompositeDisposable();

    // TODO: Rename and change types of parameters

    private MutableLiveData<String> mobileNumber = new MutableLiveData<>();


    public CardDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mobile Parameter 1.
     * @return A new instance of fragment CardDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardDetailsFragment newInstance(String mobile) {
        CardDetailsFragment fragment = new CardDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MOBILE, mobile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mobileNumber.setValue(getArguments().getString(ARG_MOBILE));
        }


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCardDetailsBinding.inflate(inflater, container, false);
        setBinding();
        setOnClick();
        setViewModel();
        setTextWatchers();
        return binding.getRoot().getRootView();
    }

    private void setTextWatchers() {

        binding.editATM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!isInputCorrect(editable,
                        CARD_NUMBER_TOTAL_SYMBOLS,
                        CARD_NUMBER_DIVIDER_MODULO,
                        CARD_NUMBER_DIVIDER)) {
                    editable.replace(0, editable.length(),
                            concatString(getDigitArray(editable,
                                            CARD_NUMBER_TOTAL_DIGITS),
                                    CARD_NUMBER_DIVIDER_POSITION, CARD_NUMBER_DIVIDER));

                }
            }
        });
    }


    private void checkCustomer() {
        setLoading(true);
        JSONObject jsonObject = new JSONObject();
        JSONObject encrypted = new JSONObject();
        try {
            jsonObject.put("MOBILENUMBER", mobileNumber.getValue());
            jsonObject.put("BANKACCOUNTID", mobileNumber.getValue());
            disposable.add(baseViewModel.customerExist(jsonObject, encrypted, requireContext())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setOnCustomerSuccess, Throwable::printStackTrace));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setOnCustomerSuccess(DynamicResponse response) {
        try {
            new AppLogger().appLog("SELF:REG:Response",
                    BaseClass.decryptLatest(response.getResponse(),
                            baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                            true,
                            baseViewModel.dataSource.getDeviceData().getValue().getRun()
                    ));

            if (response.getResponse() != null) {
                if (response.getResponse().equals("ok")) {
                    setLoading(false);
                    showError(getString(R.string.something_));
                } else {
                    GlobalResponse resData = new GlobalResponseTypeConverter().to(
                            BaseClass.decryptLatest(
                                    response.getResponse(),
                                    baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                                    true,
                                    baseViewModel.dataSource.getDeviceData().getValue().getRun()
                            )
                    );
                    assert resData != null;
                    if (Objects.equals(resData.getStatus(), StatusEnum.SUCCESS.getType())) {
                        setLoading(false);
                        SuccessDialogFragment.showDialog(
                                new DialogData(
                                        R.string.info_,
                                        resData.getMessage(),
                                        R.drawable.warning_app
                                ), this.getChildFragmentManager(), this
                        );
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.FAILED.getType())) {
                        generateOTP();
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.TOKEN.getType())) {
                        workerViewModel.routeData(getViewLifecycleOwner(), b -> {
                            setLoading(false);
                            if (b) checkCustomer();
                            else showError(getString(R.string.something_));
                        });
                    }
                }

            } else {
                setLoading(false);
                new AppLogger().appLog("CHECK:CUSTOMER", getString(R.string.something_));
            }
        } catch (Exception e) {
            showError(getString(R.string.something_));
            e.printStackTrace();
        }
    }

    private void generateOTP() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MOBILENUMBER", mobileNumber.getValue());
            disposable.add(baseViewModel.createOTP(jsonObject, requireContext())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setOnOTPSuccess, Throwable::printStackTrace));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setOnOTPSuccess(DynamicResponse response) {
        setLoading(false);
        try {
            new AppLogger().appLog("SELF:OTP:Response",
                    BaseClass.decryptLatest(response.getResponse(),
                            baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                            true,
                            baseViewModel.dataSource.getDeviceData().getValue().getRun()
                    ));

            if (response.getResponse() != null) {
                if (response.getResponse().equals("ok")) {
                    setLoading(false);
                    showError(getString(R.string.something_));
                } else {
                    GlobalResponse resData = new GlobalResponseTypeConverter().to(
                            BaseClass.decryptLatest(
                                    response.getResponse(),
                                    baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                                    true,
                                    baseViewModel.dataSource.getDeviceData().getValue().getRun()
                            )
                    );
                    assert resData != null;
                    if (Objects.equals(resData.getStatus(), StatusEnum.SUCCESS.getType())) {
                        OTPDialogFragment.showDialog(this, this.getChildFragmentManager());
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.FAILED.getType())) {
                        showError(resData.getMessage());
                        setLoading(false);
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.TOKEN.getType())) {
                        workerViewModel.routeData(getViewLifecycleOwner(), b -> {
                            setLoading(false);
                            if (b) generateOTP();
                            else showError(getString(R.string.something_));
                        });
                    }
                }

            } else {
                setLoading(false);
                new AppLogger().appLog("CHECK:CUSTOMER", getString(R.string.something_));
            }
        } catch (Exception e) {
            showError(getString(R.string.something_));
            e.printStackTrace();
        }
    }


    private void setLoading(boolean b) {
        if (b) LoadingFragment.show(getChildFragmentManager());
        else LoadingFragment.dismiss(getChildFragmentManager());
    }

    private void showError(String string) {
        AlertDialogFragment.newInstance(new DialogData(
                R.string.error,
                string,
                R.drawable.warning_app
        ), getChildFragmentManager());
    }

    @Override
    public boolean validateFields() {
        if (TextUtils.isEmpty(binding.editAccountNumber.getText())) {
            new ShowToast(requireContext(),
                    getString(R.string.enter_account_number), true);
            return false;
        } else if (TextUtils.isEmpty(binding.editATM.getText())) {
            new ShowToast(requireContext(),
                    getString(R.string.enter_atm_number), true);
            return false;
        } else if (TextUtils.isEmpty(binding.editATMPin.getText())) {
            new ShowToast(requireContext(),
                    getString(R.string.enter_atm_pin), true);
            return false;
        } else {
            if (binding.editAccountNumber.length() < 8) {
                new ShowToast(requireContext(),
                        getString(R.string.account_number_invalid), true);
                return false;
            } else if (binding.editATM.length() < 12) {
                new ShowToast(requireContext(),
                        getString(R.string.atm_number_invalid), true);
                return false;
            } else if (binding.editATMPin.length() < 4) {
                new ShowToast(requireContext(),
                        getString(R.string.atm_pin_invalid), true);
                return false;
            } else return true;
        }
    }

    @Override
    public void setOnClick() {
        binding.materialButton.setOnClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    @Override
    public void setViewModel() {
        baseViewModel = new ViewModelProvider(this).get(BaseViewModel.class);
        workerViewModel = new ViewModelProvider(this).get(WorkerViewModel.class);
        widgetViewModel = new ViewModelProvider(this).get(WidgetViewModel.class);
    }

    @Override
    public void setBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.materialButton)) {
            if (validateFields()) checkCustomer();
        }
    }

    @Override
    public void otp(@NonNull String string) {
        new AppLogger().appLog("SELF:OTP", string);
        validateOTP(string);
    }

    private void validateOTP(String string) {
        setLoading(true);
        JSONObject jsonObject = new JSONObject();
        JSONObject encrypted = new JSONObject();
        try {
            jsonObject.put("MOBILENUMBER", mobileNumber.getValue());
            jsonObject.put("OTPKEY", string);

            disposable.add(baseViewModel.validateOTP(jsonObject, encrypted, requireContext())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(v -> setOnValidOTPSuccess(v, string),
                            Throwable::printStackTrace));

        } catch (JSONException e) {
            new ShowToast(requireContext(), e.getLocalizedMessage(), true);
            setLoading(false);
            e.printStackTrace();
        }
    }

    private void setOnValidOTPSuccess(DynamicResponse response, String otp) {
        try {
            new AppLogger().appLog("SELF:OTP:Response",
                    BaseClass.decryptLatest(response.getResponse(),
                            baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                            true,
                            baseViewModel.dataSource.getDeviceData().getValue().getRun()
                    ));

            if (response.getResponse() != null) {
                if (response.getResponse().equals("ok")) {
                    setLoading(false);
                    showError(getString(R.string.something_));
                } else {
                    GlobalResponse resData = new GlobalResponseTypeConverter().to(
                            BaseClass.decryptLatest(
                                    response.getResponse(),
                                    baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                                    true,
                                    baseViewModel.dataSource.getDeviceData().getValue().getRun()
                            )
                    );
                    assert resData != null;
                    if (Objects.equals(resData.getStatus(), StatusEnum.SUCCESS.getType())) {
                        validateCard();
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.FAILED.getType())) {
                        setLoading(false);
                        showError(resData.getMessage());
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.TOKEN.getType())) {
                        workerViewModel.routeData(getViewLifecycleOwner(), b -> {
                            setLoading(false);
                            if (b) validateOTP(otp);
                            else showError(getString(R.string.something_));
                        });
                    }
                }

            } else {
                new AppLogger().appLog("VALID:OTP", getString(R.string.something_));
            }
        } catch (Exception e) {
            showError(getString(R.string.something_));
            e.printStackTrace();
        }
    }

    private void validateCard() {
        JSONObject jsonObject = new JSONObject();
        JSONObject encrypted = new JSONObject();
        try {
            jsonObject.put("BANKACCOUNTID", Objects
                    .requireNonNull(binding.editAccountNumber.getText()).toString());
            jsonObject.put("PHONENUMBER", mobileNumber.getValue());
            encrypted.put("CARDNUMBER", BaseClass
                    .encrypt(Objects.requireNonNull(binding.editATM.getText())
                            .toString().replace("-", "")));
            encrypted.put("CARDPIN", BaseClass
                    .encrypt(Objects.requireNonNull(binding.editATMPin.getText()).toString()));
            disposable.add(baseViewModel.validateCard(jsonObject, encrypted, requireContext())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setOnValidateCardSuccess, Throwable::printStackTrace));

        } catch (JSONException e) {
            new ShowToast(requireContext(), e.getLocalizedMessage(), true);
            setLoading(false);
            e.printStackTrace();
        }
    }

    private void setOnValidateCardSuccess(DynamicResponse response) {
        try {
            new AppLogger().appLog("SELF:OTP:Response",
                    BaseClass.decryptLatest(response.getResponse(),
                            baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                            true,
                            baseViewModel.dataSource.getDeviceData().getValue().getRun()
                    ));

            if (response.getResponse() != null) {
                if (response.getResponse().equals("ok")) {
                    setLoading(false);
                    showError(getString(R.string.something_));
                } else {
                    GlobalResponse resData = new GlobalResponseTypeConverter().to(
                            BaseClass.decryptLatest(
                                    response.getResponse(),
                                    baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                                    true,
                                    baseViewModel.dataSource.getDeviceData().getValue().getRun()
                            )
                    );
                    assert resData != null;
                    if (Objects.equals(resData.getStatus(), StatusEnum.SUCCESS.getType())) {
                        saveUserData();
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.FAILED.getType())) {
                        showError(resData.getMessage());
                        setLoading(false);
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.TOKEN.getType())) {
                        workerViewModel.routeData(getViewLifecycleOwner(), b -> {
                            setLoading(false);
                            if (b) validateCard();
                            else showError(getString(R.string.something_));
                        });
                    }
                }

            } else {
                setLoading(false);
                new AppLogger().appLog("VALID:CARD", getString(R.string.something_));
            }
        } catch (Exception e) {
            showError(getString(R.string.something_));
            e.printStackTrace();
        }
    }

    private void saveUserData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("BANKACCOUNTID", Objects
                    .requireNonNull(binding.editAccountNumber.getText()).toString());
            jsonObject.put("PHONENUMBER", mobileNumber.getValue());
            disposable.add(baseViewModel.saveUserDataSelf(jsonObject, requireContext())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setSaveUserSuccess, Throwable::printStackTrace));

        } catch (JSONException e) {
            new ShowToast(requireContext(), e.getLocalizedMessage(), true);
            setLoading(false);
            e.printStackTrace();
        }
    }

    private void setSaveUserSuccess(DynamicResponse response) {
        setLoading(false);
        try {
            new AppLogger().appLog("SELF:SAVE:Response",
                    BaseClass.decryptLatest(response.getResponse(),
                            baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                            true,
                            baseViewModel.dataSource.getDeviceData().getValue().getRun()
                    ));

            if (response.getResponse() != null) {
                if (response.getResponse().equals("ok")) {
                    setLoading(false);
                    showError(getString(R.string.something_));
                } else {
                    GlobalResponse resData = new GlobalResponseTypeConverter().to(
                            BaseClass.decryptLatest(
                                    response.getResponse(),
                                    baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                                    true,
                                    baseViewModel.dataSource.getDeviceData().getValue().getRun()
                            )
                    );
                    assert resData != null;
                    if (Objects.equals(resData.getStatus(), StatusEnum.SUCCESS.getType())) {
                        SuccessDialogFragment.showDialog(new DialogData(
                                R.string.success,
                                resData.getMessage(),
                                R.drawable.success
                        ), this.getChildFragmentManager(), this);
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.FAILED.getType())) {
                        showError(resData.getMessage());
                        setLoading(false);
                    } else if (Objects.equals(resData.getStatus(), StatusEnum.TOKEN.getType())) {
                        workerViewModel.routeData(getViewLifecycleOwner(), b -> {
                            setLoading(false);
                            if (b) saveUserData();
                            else showError(getString(R.string.something_));
                        });
                    }
                }

            } else {
                setLoading(false);
                new AppLogger().appLog("SAVE:CUSTOMER", getString(R.string.something_));
            }
        } catch (Exception e) {
            showError(getString(R.string.something_));
            e.printStackTrace();
        }
    }

    @Override
    public void navigateUp() {
        ((MainActivity) requireActivity())
                .provideNavigationGraph()
                .navigate(widgetViewModel.navigation().navigateLanding());
    }
}