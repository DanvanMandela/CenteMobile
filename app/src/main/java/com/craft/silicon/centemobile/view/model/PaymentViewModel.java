package com.craft.silicon.centemobile.view.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum;
import com.craft.silicon.centemobile.data.repository.payment.PaymentDataSource;
import com.craft.silicon.centemobile.data.repository.payment.PaymentRepository;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
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
public class PaymentViewModel extends ViewModel implements PaymentDataSource {

    public final StorageDataSource dataSource;
    private final PaymentRepository repository;

    @Inject
    public PaymentViewModel(StorageDataSource dataSource, PaymentRepository repository) {
        this.dataSource = dataSource;
        this.repository = repository;
    }

    private final BehaviorSubject<Boolean> loadingUi = BehaviorSubject.createDefault(false);
    public Observable<Boolean> loading = loadingUi.hide();


    @Override
    public Single<DynamicResponse> pay(JSONObject data, JSONObject encrypted, Context context, String moduleID) {
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
                    true);

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
                            uniqueID,
                            BaseClass.encryptString(newRequest, device, iv)
                    ), path);

        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
