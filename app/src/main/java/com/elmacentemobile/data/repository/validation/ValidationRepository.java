package com.elmacentemobile.data.repository.validation;

import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;
import com.elmacentemobile.util.AppLogger;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class ValidationRepository implements ValidationDataSource {

    private final ValidationDataSource remoteData;

    @Inject
    public ValidationRepository(@Remote ValidationDataSource remoteData) {
        this.remoteData = remoteData;
    }

    @Override
    public Single<DynamicResponse> validationRequest(String token, PayloadData data, String path) {
        AppLogger.Companion.getInstance().appLog("MAIN:DATA", new Gson().toJson(data));
        return remoteData.validationRequest(token, data, path);
    }
}
