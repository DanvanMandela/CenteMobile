package com.elmacentemobile.view.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.elmacentemobile.data.model.SpiltURL;
import com.elmacentemobile.data.model.action.ActionControls;
import com.elmacentemobile.data.model.action.ActionTypeEnum;
import com.elmacentemobile.data.model.module.Modules;
import com.elmacentemobile.data.repository.dynamic.widgets.WidgetRepository;
import com.elmacentemobile.data.repository.payment.PaymentDataSource;
import com.elmacentemobile.data.repository.payment.PaymentRepository;
import com.elmacentemobile.data.repository.validation.ValidationRepository;
import com.elmacentemobile.data.source.constants.Constants;
import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;
import com.elmacentemobile.util.AppLogger;
import com.elmacentemobile.util.BaseClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

@HiltViewModel
public class PaymentViewModel extends ViewModel implements PaymentDataSource {

    public final StorageDataSource dataSource;
    private final PaymentRepository repository;
    private final WidgetRepository widgetRepository;
    private final ValidationRepository validateRepo;

    @Inject
    public PaymentViewModel(StorageDataSource dataSource,
                            PaymentRepository repository,
                            WidgetRepository widgetRepository,
                            ValidationRepository validateRepo) {
        this.dataSource = dataSource;
        this.repository = repository;
        this.widgetRepository = widgetRepository;
        this.validateRepo = validateRepo;
    }

    private final BehaviorSubject<Boolean> loadingUi = BehaviorSubject.createDefault(false);
    public Observable<Boolean> loading = loadingUi.hide();


    @Override
    public Single<DynamicResponse> pay(JSONObject data,
                                       JSONObject encrypted,
                                       Context context, String moduleID) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String customerID = dataSource.getActivationData().getValue().getId();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.PAY_BILL.getType(),
                    customerID,
                    true,
                    dataSource);

            data.put("SessionID", uniqueID);
            jsonObject.put("ModuleID", moduleID);
            jsonObject.put("PayBill", data);
            jsonObject.put("EncryptedFields", encrypted);
            String newRequest = jsonObject.toString();

            String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ?
                    Constants.BaseUrl.URL : Objects.requireNonNull(dataSource.getDeviceData()
                    .getValue().getPurchase())).getPath();

            Log.e("PAY", newRequest);

            return repository.paymentRequest(
                    dataSource.getDeviceData().getValue().getToken(),
                    new PayloadData(
                            dataSource.getUniqueID().getValue(),
                            BaseClass.encryptString(newRequest, device, iv)
                    ), path);

        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public Single<DynamicResponse> pay(ActionControls action,
                                       JSONObject data,
                                       JSONObject encrypted,
                                       Modules modules,
                                       Context context) {

        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String customerID = dataSource.getActivationData().getValue().getId();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    action.getActionType(),
                    customerID,
                    true, dataSource);

            if (BaseClass.nonCaps(action.getActionType())
                    .equals(BaseClass.nonCaps(ActionTypeEnum.DB_CALL.getType()))) {

                data.put("SessionID", uniqueID);
                jsonObject.put("MerchantID", !TextUtils.isEmpty(modules.getMerchantID()) ?
                        modules.getMerchantID() : action.getMerchantID());
                jsonObject.put("ModuleID", modules.getModuleID());
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("HEADER", action.getActionID());
                jsonObject.put("DynamicForm", jsonObject1);

                String dbRequest = jsonObject.toString();
                AppLogger.Companion.getInstance().appLog("DBCall", dbRequest);
                return dbCall(new PayloadData(
                        dataSource.getUniqueID().getValue(),
                        BaseClass.encryptString(dbRequest, device, iv)
                ));
            } else if (BaseClass.nonCaps(action.getActionType())
                    .equals(BaseClass.nonCaps(ActionTypeEnum.VALIDATE.getType()))) {
                jsonObject.put("MerchantID", !TextUtils.isEmpty(modules.getMerchantID()) ?
                        modules.getMerchantID() : action.getMerchantID());
                jsonObject.put("ModuleID", modules.getModuleID());
                jsonObject.put("Validate", data);
                String validateRequest = jsonObject.toString();
                AppLogger.Companion.getInstance().appLog("Validation", validateRequest);
                return validateCall(new PayloadData(
                        dataSource.getUniqueID().getValue(),
                        BaseClass.encryptString(validateRequest, device, iv)
                ));
            } else if (BaseClass.nonCaps(action.getActionType())
                    .equals(BaseClass.nonCaps(ActionTypeEnum.PAY_BILL.getType()))) {
                jsonObject.put("ModuleID", modules.getModuleID());
                jsonObject.put("PayBill", data);
                jsonObject.put("EncryptedFields", encrypted);
                String payBillRequest = jsonObject.toString();
                AppLogger.Companion.getInstance().appLog("PayBill", payBillRequest);
                return payBillCall(new PayloadData(
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

        return repository.paymentRequest(
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

        return validateRepo.validationRequest(
                        dataSource.getDeviceData().getValue().getToken(),
                        data, path).doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnSuccess(disposable -> loadingUi.onNext(false))
                .doOnError(disposable -> loadingUi.onNext(false));
    }
}
