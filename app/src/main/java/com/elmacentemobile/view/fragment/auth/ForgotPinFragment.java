package com.elmacentemobile.view.fragment.auth;

import static com.elmacentemobile.util.TextHelper.CARD_NUMBER_DIVIDER;
import static com.elmacentemobile.util.TextHelper.CARD_NUMBER_DIVIDER_MODULO;
import static com.elmacentemobile.util.TextHelper.CARD_NUMBER_DIVIDER_POSITION;
import static com.elmacentemobile.util.TextHelper.CARD_NUMBER_TOTAL_DIGITS;
import static com.elmacentemobile.util.TextHelper.CARD_NUMBER_TOTAL_SYMBOLS;
import static com.elmacentemobile.util.TextHelper.concatString;
import static com.elmacentemobile.util.TextHelper.getDigitArray;
import static com.elmacentemobile.util.TextHelper.isInputCorrect;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.elmacentemobile.R;
import com.elmacentemobile.data.model.converter.DynamicAPIResponseConverter;
import com.elmacentemobile.data.model.dynamic.DynamicAPIResponse;
import com.elmacentemobile.data.model.dynamic.Notifications;
import com.elmacentemobile.data.source.constants.StatusEnum;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;

import com.elmacentemobile.databinding.FragmentForgotPinBinding;
import com.elmacentemobile.util.AppLogger;
import com.elmacentemobile.util.BaseClass;
import com.elmacentemobile.util.ShowToast;
import com.elmacentemobile.util.callbacks.AppCallbacks;
import com.elmacentemobile.view.activity.MainActivity;
import com.elmacentemobile.view.binding.BindingAdapterKt;
import com.elmacentemobile.view.composable.keyboard.CustomKeyData;
import com.elmacentemobile.view.composable.keyboard.CustomKeyboard;
import com.elmacentemobile.view.dialog.AlertDialogFragment;
import com.elmacentemobile.view.dialog.DialogData;
import com.elmacentemobile.view.dialog.LoadingFragment;
import com.elmacentemobile.view.dialog.MainDialogData;
import com.elmacentemobile.view.dialog.SuccessDialogFragment;
import com.elmacentemobile.view.fragment.go.steps.OCRData;
import com.elmacentemobile.view.model.BaseViewModel;
import com.elmacentemobile.view.model.WorkStatus;
import com.elmacentemobile.view.model.WorkerViewModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class ForgotPinFragment extends Fragment implements AppCallbacks, View.OnClickListener {


    private FragmentForgotPinBinding binding;
    private BaseViewModel baseViewModel;
    private WorkerViewModel workerViewModel;
    private final CompositeDisposable subscribe = new CompositeDisposable();

    private Stack<String> pinStack;

    public ForgotPinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ForgotPinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPinFragment newInstance() {
        return new ForgotPinFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForgotPinBinding.inflate(inflater, container, false);
        setViewModel();
        setOnClick();
        setTextWatchers();
        showKeyBoard();
        return binding.getRoot().getRootView();
    }

    private void showKeyBoard() {
        binding.editATMPin.setOnClickListener((v ->
                CustomKeyboard.Companion.instance(getChildFragmentManager(),
                        this)));
        baseViewModel.pin.observe(getViewLifecycleOwner(), strings -> {
            pinStack = strings;
            StringBuilder builder = new StringBuilder();
            for (String s : strings) {
                builder.append(s);
            }
            binding.editATMPin.setText(builder);
        });
    }

    @Override
    public void onType(CustomKeyData data) {
        switch (data.getType()) {
            case Push: {
                pinStack.push(data.getStr());
                baseViewModel.pin.setValue(pinStack);
                break;
            }
            case Pop: {
                if (!pinStack.isEmpty()) {
                    pinStack.pop();
                    baseViewModel.pin.setValue(pinStack);
                }
                break;
            }
        }
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

    @Override
    public void setOnClick() {
        binding.materialButton.setOnClickListener(this);
        binding.toolbar.setNavigationOnClickListener(v -> navigateUp());
    }

    @Override
    public void navigateUp() {
        ((MainActivity) requireActivity())
                .provideNavigationGraph().navigateUp();
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
    public void setViewModel() {
        baseViewModel = new ViewModelProvider(this).get(BaseViewModel.class);
        workerViewModel = new ViewModelProvider(this).get(WorkerViewModel.class);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(binding.materialButton)) {
            if (BindingAdapterKt.isOnline(requireActivity())) {
                if (validateFields()) restPin();
            } else new ShowToast(requireContext(), getString(R.string.no_connection), true);
        }
    }

    private void restPin() {
        setLoading(true);
        JSONObject jsonObject = new JSONObject();
        JSONObject encrypted = new JSONObject();
        try {
            jsonObject.put("BANKACCOUNTID", Objects
                    .requireNonNull(binding.editAccountNumber.getText()).toString());
            encrypted.put("CARDNUMBER", BaseClass
                    .newEncrypt(Objects.requireNonNull(binding.editATM.getText())
                            .toString().replace("-", "")));
            encrypted.put("CARDPIN", BaseClass
                    .newEncrypt(Objects.requireNonNull(binding.editATMPin.getText()).toString()));
            subscribe.add(baseViewModel.pinForgotATM(jsonObject, encrypted, requireContext())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setOnSuccess, Throwable::printStackTrace));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setOnSuccess(DynamicResponse response) {
        setLoading(false);
        try {
            new AppLogger().appLog("PIN:Forgot:Response",
                    BaseClass.decryptLatest(response.getResponse(),
                            baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                            true,
                            baseViewModel.dataSource.getDeviceData().getValue().getRun()
                    ));

            if (response.getResponse() != null) {
                if (response.getResponse().equals("ok")) {
                    showError(getString(R.string.something_));
                } else {
                    DynamicAPIResponse resData = new DynamicAPIResponseConverter().to(
                            BaseClass.decryptLatest(
                                    response.getResponse(),
                                    baseViewModel.dataSource.getDeviceData().getValue().getDevice(),
                                    true,
                                    baseViewModel.dataSource.getDeviceData().getValue().getRun()
                            )
                    );

                    if (resData != null) {
                        if (Objects.equals(resData.getStatus(), StatusEnum.SUCCESS.getType())) {
                            SuccessDialogFragment.showDialog(new DialogData(
                                    R.string.success,
                                    resData.getMessage(),
                                    R.drawable.success
                            ), getChildFragmentManager(), this);
                        } else if (Objects.equals(resData.getStatus(), StatusEnum.FAILED.getType())) {
                            showError(resData.getMessage());
                        } else if (Objects.equals(resData.getStatus(), StatusEnum.TOKEN.getType())) {
                            workerViewModel.routeData(getViewLifecycleOwner(), new WorkStatus() {
                                @Override
                                public void workDone(boolean b) {
                                    setLoading(false);
                                    if (b) restPin();
                                    else showError(getString(R.string.something_));
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
                        }
                    } else showError(getString(R.string.something_));

                }

            } else {
                showError(getString(R.string.something_));
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

    private void showSuccess(MainDialogData mainDialogData) {
        SuccessDialogFragment.setData(this);
        BindingAdapterKt.navigate(this, baseViewModel
                .navigationData.navigateToSuccessDialog(mainDialogData));
    }


}