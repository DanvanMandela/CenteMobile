package com.craft.silicon.centemobile.data.repository.auth;

import com.craft.silicon.centemobile.data.source.remote.callback.RequestData;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseData;

import io.reactivex.Single;

public interface AuthDataSource {

    default Single<ResponseData> authRequest(RequestData data, String path) {
        return null;
    }

    default Single<ResponseData> loginAccount(String mobile, String pin) {
        return null;
    }

    default Single<ResponseData> verifyOTP(String otp) {
        return null;
    }

}
