package com.craft.silicon.centemobile.view.model;

import android.app.Activity;

import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum;
import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.craft.silicon.centemobile.data.model.user.ActivationData;
import com.craft.silicon.centemobile.data.model.user.FrequentModules;
import com.craft.silicon.centemobile.data.repository.auth.AuthDataSource;
import com.craft.silicon.centemobile.data.repository.auth.AuthRepository;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.util.BaseClass;
import com.craft.silicon.centemobile.view.navigation.NavigationDataSource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

@HiltViewModel
public class AuthViewModel extends ViewModel implements AuthDataSource {

    private final AuthRepository authRepository;
    public final StorageDataSource storage;
    public final NavigationDataSource navigationDataSource;


    private final BehaviorSubject<Boolean> loadingUi = BehaviorSubject.createDefault(false);
    public Observable<Boolean> loading = loadingUi.hide();


    @Inject
    public AuthViewModel(AuthRepository authRepository, StorageDataSource storageDataSource, NavigationDataSource navigationDataSource) {
        this.authRepository = authRepository;
        this.storage = storageDataSource;
        this.navigationDataSource = navigationDataSource;
    }


    @Override
    public Single<DynamicResponse> activateAccount(String mobile, String pin, Activity activity) {
        try {
            String iv = storage.getDeviceData().getValue().getRun();
            String device = storage.getDeviceData().getValue().getDevice();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();


            Constants.commonJSON(jsonObject,
                    activity,
                    uniqueID,
                    ActionTypeEnum.ACTIVATION_REQ.getType(),
                    "",
                    false);

            jsonObject.put("MobileNumber", mobile);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject.put("Activation", jsonObject1);
            JSONObject encryptedFieldsJsonObject = new JSONObject();
            encryptedFieldsJsonObject.put("PIN", BaseClass.newEncrypt(pin));
            jsonObject.put("EncryptedFields", encryptedFieldsJsonObject);

            String newRequest = jsonObject.toString();
            String path = new SpiltURL(storage.getDeviceData().getValue() == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(storage.getDeviceData().getValue().getAuth())).getPath();

            return authRepository.authRequest(new PayloadData(
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

    @Override
    public Single<DynamicResponse> loginAccount(String pin, Activity activity) {
        try {
            String iv = storage.getDeviceData().getValue().getRun();
            String device = storage.getDeviceData().getValue().getDevice();
            String mobile = storage.getActivationData().getValue().getMobile();
            String customerID = storage.getActivationData().getValue().getId();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();

            Constants.commonJSON(jsonObject,
                    activity,
                    uniqueID,
                    ActionTypeEnum.LOGIN.getType(),
                    customerID,
                    true);

            jsonObject.put("MobileNumber", mobile);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("LoginType", "PIN");
            jsonObject.put("Login", jsonObject1);
            JSONObject encryptedFieldsJsonObject = new JSONObject();
            encryptedFieldsJsonObject.put("PIN", BaseClass.newEncrypt(pin));
            jsonObject.put("EncryptedFields", encryptedFieldsJsonObject);

            String newRequest = jsonObject.toString();

            String path = new SpiltURL(storage.getDeviceData().getValue() == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(storage.getDeviceData().getValue().getAuth())).getPath();

            return authRepository.authRequest(new PayloadData(
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

    @Override
    public Single<DynamicResponse> verifyOTP(String otp, Activity activity, String mobile) {
        try {
            String iv = storage.getDeviceData().getValue().getRun();
            String device = storage.getDeviceData().getValue().getDevice();
            String uniqueID = Constants.getUniqueID();
            JSONObject jsonObject = new JSONObject();
            Constants.commonJSON(jsonObject,
                    activity,
                    uniqueID,
                    ActionTypeEnum.ACTIVATE.getType(),
                    "",
                    false);

            jsonObject.put("MobileNumber", mobile);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject.put("Activation", jsonObject1);
            JSONObject encryptedFieldsJsonObject = new JSONObject();
            encryptedFieldsJsonObject.put("Key", BaseClass.newEncrypt(otp));
            jsonObject.put("EncryptedFields", encryptedFieldsJsonObject);

            String newRequest = jsonObject.toString();
            String path = new SpiltURL(storage.getDeviceData().getValue() == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(storage.getDeviceData().getValue().getAuth())).getPath();

            return authRepository.authRequest(new PayloadData(
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

    @Override
    public void saveFrequentModule(List<FrequentModules> modules) {
        authRepository.saveFrequentModule(modules);
    }

    @Override
    public Observable<List<FrequentModules>> getFrequentModules() {
        return authRepository.getFrequentModules()
                .doOnSubscribe(disposable -> loadingUi.onNext(true))
                .doOnError(throwable -> loadingUi.onNext(false));
    }

    @Override
    public Observable<List<Accounts>> getAccount() {
        return authRepository.getAccount();
    }

    @Override
    public void saveActivationData(ActivationData activationData) {
        storage.setActivationData(activationData);
        storage.setActivated(true);
    }
}
