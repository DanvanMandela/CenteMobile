package com.elmacentemobile.data.repository.dynamic;

import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.remote.callback.RequestData;
import com.elmacentemobile.data.source.remote.callback.ResponseData;
import com.elmacentemobile.util.AppLogger;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class DynamicRepository implements DynamicDataSource {
    private final DynamicDataSource remoteData;

    @Inject
    public DynamicRepository(@Remote DynamicDataSource remoteData) {
        this.remoteData = remoteData;
    }

    @Override
    public Single<ResponseData> requestBase(RequestData data) {
        new AppLogger().appLog("Routes:request", new Gson().toJson(data));
        return remoteData.requestBase(data);
    }
}
