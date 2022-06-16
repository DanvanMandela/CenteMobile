package com.craft.silicon.centemobile.view.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.craft.silicon.centemobile.data.model.SpiltURL;
import com.craft.silicon.centemobile.data.repository.auth.AuthDataSource;
import com.craft.silicon.centemobile.data.repository.auth.AuthRepository;
import com.craft.silicon.centemobile.data.source.constants.Constants;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.RequestData;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseData;
import com.craft.silicon.centemobile.util.BaseClass;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import kotlinx.coroutines.flow.MutableStateFlow;

@HiltViewModel
public class AuthViewModel extends ViewModel implements AuthDataSource {

    private final AuthRepository authRepository;
    private final StorageDataSource storage;
    public LiveData<String> optLiveData = new MutableLiveData<>("");


    private final BehaviorSubject<Boolean> loadingUi = BehaviorSubject.createDefault(false);
    public Observable<Boolean> loading = loadingUi.hide();


    @Inject
    public AuthViewModel(AuthRepository authRepository, StorageDataSource storageDataSource) {
        this.authRepository = authRepository;
        this.storage = storageDataSource;

    }


    @Override
    public Single<ResponseData> loginAccount(String mobile, String pin) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("FORMID", "ACTIVATIONREQ");
            jsonObject.put("UNIQUEID", "wtgsgfsfsfsfsffsspo");
            jsonObject.put("CUSTOMERID", "25600116");
            jsonObject.put("BANKID", "16");
            jsonObject.put("VersionNumber", "28");
            jsonObject.put("MobileNumber", mobile);
            jsonObject.put("IMEI", BaseClass.encrypt("45687555"));
            jsonObject.put("IMSI", BaseClass.encrypt("45687555"));
            jsonObject.put("TRXSOURCE", "APP");
            jsonObject.put("APPNAME", "CENTE");
            jsonObject.put("CODEBASE", "ANDROID");
            jsonObject.put(
                    "LATLON",
                    "0.0" + "," + "0.0"
            );

            JSONObject jsonObject1 = new JSONObject();
            jsonObject.put("Activation", jsonObject1);
            JSONObject encryptedFieldsJsonObject = new JSONObject();
            encryptedFieldsJsonObject.put("PIN", BaseClass.encrypt(pin));
            jsonObject.put("EncryptedFields", encryptedFieldsJsonObject);

            String newRequest = jsonObject.toString();

            Log.e("DATA", newRequest);

            jsonObject.put("rashi", BaseClass.hashLatest(newRequest));
            jsonObject.put("MobileNumber", "2500116");
            jsonObject.put("CodeBase", "ANDROID");
            jsonObject.put("lat", "0.0");
            jsonObject.put("long", "0.0");
            jsonObject.put("UniqueId", "eqrwfdgdgdhdhd");
            jsonObject.put("Appname", "CENTE");
            String path = new SpiltURL(storage.getDeviceData().getValue() == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(storage.getDeviceData().getValue().getAuth())).getPath();

            return authRepository.authRequest(new RequestData(
                            "87654321",
                            mobile,
                            "12345678",
                            "ANDROID",
                            "0.0",
                            "0.0",
                            BaseClass.hashLatest(newRequest), "CENTE"
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
    public Single<ResponseData> verifyOTP(String otp) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("FORMID", "ACTIVATIONREQ");
            jsonObject.put("UNIQUEID", "wtgsgfsfsfsfsffsspo");
            jsonObject.put("CUSTOMERID", "16");
            jsonObject.put("VersionNumber", "28");
            jsonObject.put("MobileNumber", "mobile");
            jsonObject.put("IMEI", BaseClass.encrypt("45687555"));
            jsonObject.put("IMSI", BaseClass.encrypt("45687555"));
            jsonObject.put("TRXSOURCE", "APP");
            jsonObject.put("APPNAME", "CENTE");
            jsonObject.put("CODEBASE", "ANDROID");
            jsonObject.put(
                    "LATLON",
                    "0.0" + "," + "0.0"
            );

            JSONObject jsonObject1 = new JSONObject();
            jsonObject.put("Activation", jsonObject1);
            JSONObject encryptedFieldsJsonObject = new JSONObject();
            encryptedFieldsJsonObject.put("OTP", BaseClass.encrypt(otp));
            jsonObject.put("EncryptedFields", encryptedFieldsJsonObject);

            String newRequest = jsonObject.toString();

            Log.e("DATA", newRequest);

            jsonObject.put("rashi", BaseClass.hashLatest(newRequest));
            jsonObject.put("MobileNumber", "2500116");
            jsonObject.put("CodeBase", "ANDROID");
            jsonObject.put("lat", "0.0");
            jsonObject.put("long", "0.0");
            jsonObject.put("UniqueId", "eqrwfdgdgdhdhd");
            jsonObject.put("Appname", "CENTE");
            String path = new SpiltURL(storage.getDeviceData().getValue() == null ? Constants.BaseUrl.UAT : Objects.requireNonNull(storage.getDeviceData().getValue().getAuth())).getPath();

            return authRepository.authRequest(new RequestData(
                            "87654321",
                            "mobile",
                            "12345678",
                            "ANDROID",
                            "0.0",
                            "0.0",
                            BaseClass.hashLatest(newRequest), "CENTE"
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
