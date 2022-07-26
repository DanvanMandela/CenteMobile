package com.craft.silicon.centemobile.view.model;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.model.action.ActionControls;
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum;
import com.craft.silicon.centemobile.data.model.module.Modules;
import com.craft.silicon.centemobile.data.model.user.ActivationData;
import com.craft.silicon.centemobile.data.repository.account.AccountRepository;
import com.craft.silicon.centemobile.data.repository.auth.AuthRepository;
import com.craft.silicon.centemobile.data.repository.calls.AppDataSource;
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepository;
import com.craft.silicon.centemobile.data.repository.payment.PaymentRepository;
import com.craft.silicon.centemobile.data.repository.validation.ValidationRepository;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.util.AppLogger;
import com.craft.silicon.centemobile.util.BaseClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

@HiltViewModel
public class BaseViewModel extends ViewModel implements AppDataSource {
    private final ValidationRepository repository;
    private final WidgetRepository widgetRepository;
    private final PaymentRepository paymentRepository;
    private final AuthRepository authRepository;
    private final AccountRepository accountRepository;

    public final StorageDataSource dataSource;

    private final BehaviorSubject<Boolean> loadingUi = BehaviorSubject.createDefault(false);
    public Observable<Boolean> loading = loadingUi.hide();

    @Inject
    public BaseViewModel(ValidationRepository repository,
                         WidgetRepository widgetRepository,
                         PaymentRepository paymentRepository,
                         AuthRepository authRepository,
                         AccountRepository accountRepository,
                         StorageDataSource dataSource) {
        this.repository = repository;
        this.widgetRepository = widgetRepository;
        this.paymentRepository = paymentRepository;
        this.authRepository = authRepository;
        this.accountRepository = accountRepository;
        this.dataSource = dataSource;
    }


    @Override
    public Single<DynamicResponse> dynamicCall(ActionControls action,
                                               JSONObject data,
                                               JSONObject encrypted,
                                               Modules modules, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            ActivationData customerID = dataSource.getActivationData().getValue();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    action.getActionType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);


            //String merchantID=if(TextUtils.isEmpty())

            if (BaseClass.nonCaps(action.getActionType())
                    .equals(BaseClass.nonCaps(ActionTypeEnum.DB_CALL.getType()))) {
                jsonObject.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                data.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                jsonObject.put("ModuleID", modules.getModuleID());
                data.put("HEADER", action.getActionID());
                jsonObject.put("DynamicForm", data);

                String dbRequest = jsonObject.toString();
                AppLogger.Companion.getInstance().appLog("DBCall", dbRequest);
                return dbCall(new PayloadData(
                        dataSource.getUniqueID().getValue(),
                        BaseClass.encryptString(dbRequest, device, iv)
                ));
            } else if (BaseClass.nonCaps(action.getActionType())
                    .equals(BaseClass.nonCaps(ActionTypeEnum.VALIDATE.getType()))) {
                jsonObject.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                data.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                jsonObject.put("ModuleID", modules.getModuleID());
                jsonObject.put("Validate", data);
                String validateRequest = jsonObject.toString();
                AppLogger.Companion.getInstance().appLog("Validation", validateRequest);

                if (action.getWebHeader() != null) {
                    if (action.getWebHeader().equalsIgnoreCase("auth")) {
                        return authCall(new PayloadData(
                                dataSource.getUniqueID().getValue(),
                                BaseClass.encryptString(validateRequest, device, iv)
                        ));
                    } else if (action.getWebHeader().equalsIgnoreCase("validate")) {
                        return validateCall(new PayloadData(
                                dataSource.getUniqueID().getValue(),
                                BaseClass.encryptString(validateRequest, device, iv)
                        ));
                    } else return null;
                } else return validateCall(new PayloadData(
                        dataSource.getUniqueID().getValue(),
                        BaseClass.encryptString(validateRequest, device, iv)
                ));
            } else if (BaseClass.nonCaps(action.getActionType())
                    .equals(BaseClass.nonCaps(ActionTypeEnum.PAY_BILL.getType()))) {
                jsonObject.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                data.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                jsonObject.put("ModuleID", modules.getModuleID());
                jsonObject.put("PayBill", data);
                jsonObject.put("EncryptedFields", encrypted);
                String payBillRequest = jsonObject.toString();
                AppLogger.Companion.getInstance().appLog("PayBill", payBillRequest);

                if (action.getWebHeader() != null) {
                    if (action.getWebHeader().equalsIgnoreCase("account")) {
                        AppLogger.Companion.getInstance().appLog("PayBill:account",
                                payBillRequest);
                        return accountCall(new PayloadData(
                                dataSource.getUniqueID().getValue(),
                                BaseClass.encryptString(payBillRequest, device, iv)
                        ));
                    } else if (action.getWebHeader().equalsIgnoreCase("purchase")) {
                        AppLogger.Companion.getInstance().appLog("PayBill:purchase",
                                payBillRequest);

                        return payBillCall(new PayloadData(
                                dataSource.getUniqueID().getValue(),
                                BaseClass.encryptString(payBillRequest, device, iv)
                        ));
                    } else return null;
                } else return payBillCall(new PayloadData(
                        dataSource.getUniqueID().getValue(),
                        BaseClass.encryptString(payBillRequest, device, iv)
                ));
            } else return null;

        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> validateOTP(JSONObject data, JSONObject encrypted,
                                               Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String uniqueID = Constants.getUniqueID();
            ActivationData customerID = dataSource.getActivationData().getValue();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.DB_CALL.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);


            data.put("HEADER", "OTPVERIFY");
            jsonObject.put("DynamicForm", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("SELF:OTP:VLD", newRequest);

            return dbCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> saveUserDataSelf(JSONObject data, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String uniqueID = Constants.getUniqueID();

            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.DB_CALL.getType(),
                    "",
                    true,
                    dataSource);

            jsonObject.put("INFOFIELD1", "RegistrationRequest");
            data.put("HEADER", "SELFREG");
            jsonObject.put("DynamicForm", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("SELF:REG:SAVE", newRequest);

            return dbCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> registrationOnGO(JSONObject data, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            ActivationData customerID = dataSource.getActivationData().getValue();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.VALIDATE.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);

            data.put("MerchantID", "SELFRAO");
            data.put("BANKACCOUNTID", Constants.Data.BANK_ID);
            jsonObject.put("Validate", data);
            String validateRequest = jsonObject.toString();
            AppLogger.Companion.getInstance().appLog("RAO:GO:", validateRequest);

            return validateCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(validateRequest, device, iv)
            ));

        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> pinForgotATM(JSONObject data, JSONObject encrypted,
                                                Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String mobile = dataSource.getActivationData().getValue().getMobile();
            ActivationData customerID = dataSource.getActivationData().getValue();
            String uniqueID = Constants.getUniqueID();

            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.PAY_BILL.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);

            data.put("PHONENUMBER", mobile);
            data.put("MerchantID", "PINRESET");
            jsonObject.put("EncryptedFields", encrypted);
            jsonObject.put("PayBill", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("PIN:Forgot", newRequest);

            return payBillCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> validateCard(JSONObject data,
                                                JSONObject encrypted, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String uniqueID = Constants.getUniqueID();

            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.PAY_BILL.getType(),
                    "",
                    true,
                    dataSource);


            data.put("MerchantID", "VALIDATECARD");
            jsonObject.put("EncryptedFields", encrypted);
            jsonObject.put("PayBill", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("CARD:VALID", newRequest);

            return payBillCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> createOTP(JSONObject data, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String uniqueID = Constants.getUniqueID();
            ActivationData customerID = dataSource.getActivationData().getValue();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.DB_CALL.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);

            data.put("HEADER", "CREATEOTP");
            jsonObject.put("DynamicForm", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("SELF:CRT:OTP", newRequest);

            return dbCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> ocr(JSONObject data, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            ActivationData customerID = dataSource.getActivationData().getValue();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.VALIDATE.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);


            jsonObject.put("MerchantID", "OCR");
            data.put("MerchantID", "OCR");
            jsonObject.put("ModuleID", "CENTEXPRESS");
            jsonObject.put("Validate", data);
            String validateRequest = jsonObject.toString();
            AppLogger.Companion.getInstance().appLog("Validation:OCR:", validateRequest);

            return validateCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(validateRequest, device, iv)
            ));

        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> customerExist(JSONObject data,
                                                 JSONObject encrypted,
                                                 Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.DB_CALL.getType(),
                    "",
                    true,
                    dataSource);


            data.put("HEADER", "CHECKCUSTOMERACCOUNTEXISTS");
            jsonObject.put("DynamicForm", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("SELF:REG:CHECK:CUSTOMER", newRequest);

            return dbCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> customerNumberExist(JSONObject data, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();
            ActivationData customerID = dataSource.getActivationData().getValue();
            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.DB_CALL.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);


            data.put("HEADER", "CHECKCUSTOMEREXISTS");
            jsonObject.put("DynamicForm", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("GO:REG:CHECK:CUSTOMER", newRequest);

            return dbCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> dbCall(PayloadData data) {
        String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ?
                Constants.BaseUrl.UAT : Objects.requireNonNull(dataSource.getDeviceData()
                .getValue().getOther())).getPath();
        return widgetRepository.requestWidget(data, path)
                .doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnSuccess(disposable -> loadingUi.onNext(false))
                .doOnError(disposable -> loadingUi.onNext(false));
    }

    @Override
    public Single<DynamicResponse> payBillCall(PayloadData data) {
        String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ?
                Constants.BaseUrl.UAT : Objects.requireNonNull(dataSource.getDeviceData()
                .getValue().getPurchase())).getPath();

        return paymentRepository.paymentRequest(
                        dataSource.getDeviceData().getValue().getToken(),
                        data, path).doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnSuccess(disposable -> loadingUi.onNext(false))
                .doOnError(disposable -> loadingUi.onNext(false));
    }

    @Override
    public Single<DynamicResponse> validateCall(PayloadData data) {
        String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ?
                Constants.BaseUrl.UAT : Objects.requireNonNull(dataSource.getDeviceData()
                .getValue().getValidate())).getPath();

        return repository.validationRequest(
                        dataSource.getDeviceData().getValue().getToken(),
                        data, path).doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnSuccess(disposable -> loadingUi.onNext(false))
                .doOnError(disposable -> loadingUi.onNext(false));
    }


    @Override
    public Single<DynamicResponse> accountCall(PayloadData data) {
        String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ?
                Constants.BaseUrl.UAT : Objects.requireNonNull(dataSource.getDeviceData()
                .getValue().getAccount())).getPath();

        return accountRepository.accountRequestT(
                        dataSource.getDeviceData().getValue().getToken(),
                        data, path).doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnSuccess(disposable -> loadingUi.onNext(false))
                .doOnError(disposable -> loadingUi.onNext(false));
    }

    @Override
    public Single<DynamicResponse> authCall(PayloadData data) {
        String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ?
                Constants.BaseUrl.UAT : Objects.requireNonNull(dataSource.getDeviceData()
                .getValue().getAuth())).getPath();

        return authRepository.authRequest(
                        data, path).doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnSuccess(disposable -> loadingUi.onNext(false))
                .doOnError(disposable -> loadingUi.onNext(false));
    }
}
