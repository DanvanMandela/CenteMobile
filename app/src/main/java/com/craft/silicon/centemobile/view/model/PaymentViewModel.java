package com.craft.silicon.centemobile.view.model;

import android.content.Context;

import androidx.annotation.NonNull;
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

import java.io.Closeable;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

@HiltViewModel
public class PaymentViewModel extends ViewModel implements PaymentDataSource {

    private final StorageDataSource dataSource;
    private final PaymentRepository repository;

    @Inject
    public PaymentViewModel(StorageDataSource dataSource, PaymentRepository repository) {
        this.dataSource = dataSource;
        this.repository = repository;
    }

    private final BehaviorSubject<Boolean> loadingUi = BehaviorSubject.createDefault(false);
    public Observable<Boolean> loading = loadingUi.hide();


    @Override
    public Single<DynamicResponse> recentList(String moduleID, String merchantID, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String customerID = dataSource.getActivationData().getValue().getId();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.DB_CALL.getType(),
                    customerID,
                    true);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("HEADER", "GETRECENTLIST");
            jsonObject.put("DynamicForm", jsonObject1);

            String newRequest = jsonObject.toString();
            String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(dataSource.getDeviceData().getValue().getOther())).getPath();

            return repository.paymentRequest(
                            dataSource.getDeviceData().getValue().getToken(),
                            new PayloadData(
                                    uniqueID,
                                    BaseClass.encryptString(newRequest, device, iv)
                            ), path)
                    .doOnSubscribe(disposable -> loadingUi.onNext(true))
                    .doOnSuccess(disposable -> loadingUi.onNext(false))
                    .doOnError(disposable -> loadingUi.onNext(false));

        } catch (JSONException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
