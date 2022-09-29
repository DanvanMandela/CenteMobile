package com.elmacentemobile.data.repository.auth;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.elmacentemobile.data.model.user.Accounts;
import com.elmacentemobile.data.model.user.ActivationData;
import com.elmacentemobile.data.model.user.Beneficiary;
import com.elmacentemobile.data.model.user.FrequentModules;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface AuthDataSource {

    default Single<DynamicResponse> authRequest(PayloadData data, String path) {
        return null;
    }

    default Single<DynamicResponse> activateAccount(String mobile, String pin, Activity activity) {
        return null;
    }

    default Single<DynamicResponse> loginAccount(String pin, Activity activity) {
        return null;
    }

    default Single<DynamicResponse> pinForgotATM(JSONObject jsonObject, Context context) {
        return null;
    }

    default Single<DynamicResponse> deviceRegister(JSONObject json, Activity activity) {
        return null;
    }


    default Single<DynamicResponse> verifyOTP(String otp, Activity activity, String mobile) {
        return null;
    }

    default Single<DynamicResponse> authRequestT(String token, PayloadData data, String path) {
        return null;
    }

    default void saveFrequentModule(List<FrequentModules> modules) {
    }

    default Observable<List<FrequentModules>> getFrequentModules() {
        return null;
    }

    default void saveAccountModule(List<Accounts> modules) {
    }

    default Observable<List<Accounts>> getAccount() {
        return null;
    }

    default void saveActivationData(ActivationData activationData) {
    }

    default void saveVersion(String str) {
    }

    default LiveData<String> getVersion() {
        return null;
    }

    default Observable<List<Beneficiary>> geBeneficiary() {
        return null;
    }

    default void saveBeneficiary(List<Beneficiary> modules) {
    }

}
