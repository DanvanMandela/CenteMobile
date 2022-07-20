package com.craft.silicon.centemobile.data.repository.dynamic;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.RequestData;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseData;
import com.craft.silicon.centemobile.util.AppLogger;
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
