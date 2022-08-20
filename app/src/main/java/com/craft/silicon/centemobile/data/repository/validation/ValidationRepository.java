package com.craft.silicon.centemobile.data.repository.validation;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.util.AppLogger;
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
