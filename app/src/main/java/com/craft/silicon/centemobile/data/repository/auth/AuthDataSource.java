package com.craft.silicon.centemobile.data.repository.auth;

import android.app.Activity;

import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.craft.silicon.centemobile.data.model.user.ActivationData;
import com.craft.silicon.centemobile.data.model.user.FrequentModules;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

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
}
