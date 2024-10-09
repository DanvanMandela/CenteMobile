package com.elmacentemobile.view.model;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elmacentemobile.data.model.SpiltURL;
import com.elmacentemobile.data.model.action.ActionControls;
import com.elmacentemobile.data.model.action.ActionTypeEnum;
import com.elmacentemobile.data.model.module.Modules;
import com.elmacentemobile.data.model.user.Accounts;
import com.elmacentemobile.data.model.user.ActivationData;
import com.elmacentemobile.data.repository.account.AccountRepository;
import com.elmacentemobile.data.repository.auth.AuthRepository;
import com.elmacentemobile.data.repository.calls.AppDataSource;
import com.elmacentemobile.data.repository.card.CardRepository;
import com.elmacentemobile.data.repository.dynamic.widgets.WidgetRepository;
import com.elmacentemobile.data.repository.payment.PaymentRepository;
import com.elmacentemobile.data.repository.validation.ValidationRepository;
import com.elmacentemobile.data.service.InteractionDataSource;
import com.elmacentemobile.data.service.otp.SMSDatasource;
import com.elmacentemobile.data.source.constants.Constants;
import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;
import com.elmacentemobile.util.AppLogger;
import com.elmacentemobile.util.BaseClass;
import com.elmacentemobile.util.SimData;
import com.elmacentemobile.view.navigation.NavigationDataSource;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Stack;

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
    private final CardRepository cardRepository;
    public final SimData simData;

    public final InteractionDataSource interactionDataSource;

    public final StorageDataSource dataSource;
    public final NavigationDataSource navigationData;

    public MutableLiveData<Stack<String>> pin = new MutableLiveData<>(new Stack<>());

    @Inject
    public SMSDatasource smsDatasource;


    public MutableLiveData<String> mobile = new MutableLiveData<>();

    private final BehaviorSubject<Boolean> loadingUi = BehaviorSubject.createDefault(false);
    public Observable<Boolean> loading = loadingUi.hide();

    private  Context context;

    @Inject
    public BaseViewModel(ValidationRepository repository,
                         WidgetRepository widgetRepository,
                         PaymentRepository paymentRepository,
                         AuthRepository authRepository,
                         AccountRepository accountRepository,
                         CardRepository cardRepository,
                         SimData simData,
                         InteractionDataSource interactionDataSource,
                         StorageDataSource dataSource,
                         NavigationDataSource navigationData) {
        this.repository = repository;
        this.widgetRepository = widgetRepository;
        this.paymentRepository = paymentRepository;
        this.authRepository = authRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.simData = simData;
        this.interactionDataSource = interactionDataSource;
        this.dataSource = dataSource;
        this.navigationData = navigationData;
    }

    @Override
    public Single<DynamicResponse> addComment(JSONObject data, Context context) {
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
            data.put("HEADER", "FEEDBACK");
            data.put("COUNTRY", "UGANDATEST");
            data.put("BANKID", "UGANDATEST");
            data.put("APPNAME", "CENTEMOBILE");
            jsonObject.put("DynamicForm", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("FEEDBACK:", newRequest);

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
    public Single<DynamicResponse> checkMiniStatement(Accounts accounts, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            ActivationData customerID = dataSource.getActivationData().getValue();
            String uniqueID = Constants.getUniqueID();

            JSONObject jsonObject = new JSONObject();
            JSONObject json = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.PAY_BILL.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);

            json.put("BANKACCOUNTID", accounts.getId());
            json.put("MerchantID", "STATEMENT");
            jsonObject.put("MerchantID", "STATEMENT");
            jsonObject.put("ModuleID", "STATEMENT");
            jsonObject.put("PayBill", json);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("Mini:statement", newRequest);

            return accountCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> transactionCenter(Modules modules, Context context) {
        String iv = dataSource.getDeviceData().getValue().getRun();
        String device = dataSource.getDeviceData().getValue().getDevice();
        ActivationData customerID = dataSource.getActivationData().getValue();
        String uniqueID = Constants.getUniqueID();
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();

        AppLogger.Companion.getInstance().appLog("TC:UniqueID",
                dataSource.getUniqueID().getValue());

        Constants.commonJSON(jsonObject,
                context,
                uniqueID,
                ActionTypeEnum.DB_CALL.getType(),
                customerID != null ? customerID.getId() : "",
                true,
                dataSource);

        try {
            data.put("HEADER", "GETTRXLIST");
            jsonObject.put("ModuleID", "TRANSACTIONSCENTER");
            jsonObject.put("DynamicForm", data);

            String dbRequest = jsonObject.toString();
            AppLogger.Companion.getInstance().appLog("DBCall", dbRequest);

            PayloadData payloadData = new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(dbRequest, device, iv)
            );

            return dbCall(payloadData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Single<DynamicResponse> createNSSFOTP(JSONObject data, Context context) {
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

            data.put("MerchantID", "NSSFCARDVALIDATION");
            jsonObject.put("Validate", data);
            String newRequest = jsonObject.toString();
            new AppLogger().appLog("NSSF:validation", newRequest);

            return validateCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> changePin(JSONObject json, JSONObject encrypted, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            ActivationData customerID = dataSource.getActivationData().getValue();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            AppLogger.Companion.getInstance().appLog("GLOBAL:UniqueID",
                    dataSource.getUniqueID().getValue());

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.CHANGE_PIN.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);


            jsonObject.put("ModuleID", "PIN");
            jsonObject.put("MerchantID", "PIN");
            json.put("MerchantID", "PIN");
            json.put("ModuleID", "PIN");
            json.put("PINTYPE", "PIN");

            jsonObject.put("CHANGEPIN", json);
            jsonObject.put("EncryptedFields", encrypted);
            String changePinRequest = jsonObject.toString();
            AppLogger.Companion.getInstance().appLog("CHANGE:PIN", changePinRequest);

            PayloadData payloadData = new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(changePinRequest, device, iv)
            );
            return authCall(payloadData);

        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> dynamicCall(ActionControls action,
                                               JSONObject data,
                                               JSONObject encrypted,
                                               Modules modules, Activity context) {
        this.context=context;
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            ActivationData customerID = dataSource.getActivationData().getValue();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            AppLogger.Companion.getInstance().appLog("GLOBAL:UniqueID",
                    dataSource.getUniqueID().getValue());

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    action.getActionType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);


            if (BaseClass.nonCaps(action.getActionType())
                    .equals(BaseClass.nonCaps(ActionTypeEnum.DB_CALL.getType()))) {
                jsonObject.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                data.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                jsonObject.put("ModuleID", modules.getModuleID());
                data.put("HEADER", action.getActionID());
                jsonObject.put("DynamicForm", data);
                jsonObject.put("EncryptedFields", encrypted);
                String dbRequest = jsonObject.toString();
                AppLogger.Companion.getInstance().appLog("DBCall", dbRequest);
                AppLogger.Companion.getInstance().logTXT(new Gson().toJson(jsonObject), context);
                PayloadData payloadData = new PayloadData(
                        dataSource.getUniqueID().getValue(),
                        BaseClass.encryptString(dbRequest, device, iv)
                );

                return dbCall(payloadData);


            } else if (BaseClass.nonCaps(action.getActionType())
                    .equals(BaseClass.nonCaps(ActionTypeEnum.VALIDATE.getType()))) {
                jsonObject.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                data.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                jsonObject.put("ModuleID", modules.getModuleID());
                jsonObject.put("Validate", data);
                jsonObject.put("EncryptedFields", encrypted);
                String validateRequest = jsonObject.toString();
                AppLogger.Companion.getInstance().appLog("Validation", validateRequest);
                AppLogger.Companion.getInstance().logTXT(new Gson().toJson(validateRequest), context);
                PayloadData payloadData = new PayloadData(
                        dataSource.getUniqueID().getValue(),
                        BaseClass.encryptString(validateRequest, device, iv)
                );
                new AppLogger().logTXT(new Gson().toJson(validateRequest), context);

                if (action.getWebHeader() != null) {
                    if (action.getWebHeader().equalsIgnoreCase("auth")) {
                        return authCall(payloadData);
                    } else if (action.getWebHeader().equalsIgnoreCase("validate")) {
                        return validateCall(payloadData);
                    } else return null;
                } else return validateCall(payloadData);
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
                AppLogger.Companion.getInstance().logTXT(new Gson().toJson(jsonObject), context);
                PayloadData payloadData = new PayloadData(
                        dataSource.getUniqueID().getValue(),
                        BaseClass.encryptString(payBillRequest, device, iv)
                );
                if (action.getWebHeader() != null) {
                    if (action.getWebHeader().equalsIgnoreCase("account")) {
                        AppLogger.Companion.getInstance().appLog("PayBill:account",
                                payBillRequest);
                        return accountCall(payloadData);
                    } else if (action.getWebHeader().equalsIgnoreCase("purchase")) {
                        AppLogger.Companion.getInstance().appLog("PayBill:purchase",
                                payBillRequest);

                        return payBillCall(payloadData);
                    } else if (action.getWebHeader().equalsIgnoreCase("card")) {
                        AppLogger.Companion.getInstance().appLog("PayBill:card",
                                payBillRequest);

                        return cardCall(payloadData);
                    } else return null;
                } else return payBillCall(payloadData);

            } else if (BaseClass.nonCaps(action.getActionType())
                    .equals(BaseClass.nonCaps(ActionTypeEnum.CHANGE_PIN.getType()))) {
                jsonObject.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                data.put("MerchantID", !TextUtils.isEmpty(action.getMerchantID()) ?
                        action.getMerchantID() : modules.getMerchantID());
                jsonObject.put("ModuleID", modules.getModuleID());
                data.put("ModuleID", modules.getModuleID());
                // data.put("PINTYPE", "PIN");
                jsonObject.put("CHANGEPIN", data);
                jsonObject.put("EncryptedFields", encrypted);
                String changePinRequest = jsonObject.toString();
                AppLogger.Companion.getInstance().appLog("CHANGE:PIN", changePinRequest);
                AppLogger.Companion.getInstance().logTXT(new Gson().toJson(jsonObject), context);
                PayloadData payloadData = new PayloadData(
                        dataSource.getUniqueID().getValue(),
                        BaseClass.encryptString(changePinRequest, device, iv)
                );
                return authCall(payloadData);
            }
            return null;

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

            data.put("SessionID", uniqueID);
            data.put("MerchantID", "SELFRAOV2");
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
    public Single<DynamicResponse> checkProductExist(JSONObject data, Context context) {

        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            ActivationData customerID = dataSource.getActivationData().getValue();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.DB_CALL.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);

            data.put("SessionID", uniqueID);
            data.put("HEADER", ActionTypeEnum.VALIDATE_ACCOUNT_PRODUCT_TYPE.getType());
            jsonObject.put("DynamicForm", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("PRODUCT:EXIST:REQ", newRequest);

            return dbCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return AppDataSource.super.checkProductExist(data, context);
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
                    ActionTypeEnum.CARD.getType(),
                    customerID != null ? customerID.getId() : "",
                    true,
                    dataSource);

            data.put("MOBILENUMBER", mobile);
            data.put("SessionID", uniqueID);
            data.put("MerchantID", "PINRESET");
            jsonObject.put("EncryptedFields", encrypted);
            jsonObject.put("PayBill", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("PIN:Forgot", newRequest);

            return cardCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> pinResetPre(JSONObject data, JSONObject encrypted, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
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

            data.put("SessionID", uniqueID);
            data.put("MerchantID", "PINRESET");
            jsonObject.put("EncryptedFields", encrypted);
            jsonObject.put("PayBill", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("PIN:Forgot", newRequest);

            return cardCall(new PayloadData(
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


            data.put("SessionID", uniqueID);
            data.put("MerchantID", "SELFREG");
            jsonObject.put("MerchantID", "SELFREG");
            jsonObject.put("EncryptedFields", encrypted);
            jsonObject.put("PayBill", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("CARD:VALID", newRequest);

            return cardCall(new PayloadData(
                    dataSource.getUniqueID().getValue(),
                    BaseClass.encryptString(newRequest, device, iv)
            ));
        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> validateCardOnTheGo(JSONObject data,
                                                       JSONObject encrypted, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
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


            data.put("SessionID", uniqueID);
            data.put("MerchantID", "CENTEONTHEGO");
            jsonObject.put("MerchantID", "CENTEONTHEGO");
            jsonObject.put("EncryptedFields", encrypted);
            jsonObject.put("PayBill", data);
            String newRequest = jsonObject.toString();

            new AppLogger().appLog("CARD:VALID", newRequest);

            return cardCall(new PayloadData(
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

            data.put("SessionID", uniqueID);
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


            data.put("SessionID", uniqueID);
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


    public Single<DynamicResponse> nira(JSONObject data, Context context) {
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


            data.put("SessionID", uniqueID);
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


            data.put("SessionID", uniqueID);
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


            data.put("SessionID", uniqueID);
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
                Constants.BaseUrl.URL : Objects.requireNonNull(dataSource.getDeviceData()
                .getValue().getOther())).getPath();
        return widgetRepository.requestWidget(data, path)
                .doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnSuccess(disposable -> loadingUi.onNext(false))
                .doOnError(disposable -> loadingUi.onNext(false));
    }

    @Override
    public Single<DynamicResponse> payBillCall(PayloadData data) {
        String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ?
                Constants.BaseUrl.URL : Objects.requireNonNull(dataSource.getDeviceData()
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
                Constants.BaseUrl.URL : Objects.requireNonNull(dataSource.getDeviceData()
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
                Constants.BaseUrl.URL : Objects.requireNonNull(dataSource.getDeviceData()
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
                Constants.BaseUrl.URL : Objects.requireNonNull(dataSource.getDeviceData()
                .getValue().getAuth())).getPath();

        return authRepository.authRequest(
                        data, path).doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnSuccess(disposable -> loadingUi.onNext(false))
                .doOnError(disposable -> loadingUi.onNext(false));
    }

    @Override
    public Single<DynamicResponse> cardCall(PayloadData data) {
        String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ?
                Constants.BaseUrl.URL : Objects.requireNonNull(dataSource.getDeviceData()
                .getValue().getCard())).getPath();

        return cardRepository.cardRequest(
                        dataSource.getDeviceData().getValue().getToken(),
                        data, path).doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnSuccess(disposable -> loadingUi.onNext(false))
                .doOnError(disposable -> loadingUi.onNext(false));
    }


}
