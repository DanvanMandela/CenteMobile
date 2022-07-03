package com.craft.silicon.centemobile.view.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum;
import com.craft.silicon.centemobile.data.repository.validation.ValidationDataSource;
import com.craft.silicon.centemobile.data.repository.validation.ValidationRepository;
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
public class ValidationViewModel extends ViewModel implements ValidationDataSource {
    private final ValidationRepository repository;
    public final StorageDataSource dataSource;

    private final BehaviorSubject<Boolean> loadingUi = BehaviorSubject.createDefault(false);
    public Observable<Boolean> loading = loadingUi.hide();


    @Inject
    public ValidationViewModel(ValidationRepository repository, StorageDataSource dataSource) {
        this.repository = repository;
        this.dataSource = dataSource;
    }


    @Override
    public Single<DynamicResponse> validation(String moduleID, String merchantID, JSONObject data, Context context) {
        try {
            String iv = dataSource.getDeviceData().getValue().getRun();
            String device = dataSource.getDeviceData().getValue().getDevice();
            String customerID = dataSource.getActivationData().getValue().getId();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    context,
                    uniqueID,
                    ActionTypeEnum.VALIDATE.getType(),
                    customerID,
                    true);
            jsonObject.put("MerchantID", merchantID);
            jsonObject.put("ModuleID", moduleID);
            jsonObject.put("Validate", data);

            String newRequest = jsonObject.toString();
            Log.e("Validation", newRequest);
            String path = new SpiltURL(dataSource.getDeviceData().getValue() == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(dataSource.getDeviceData().getValue().getValidate())).getPath();

            return repository.validationRequest(
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
