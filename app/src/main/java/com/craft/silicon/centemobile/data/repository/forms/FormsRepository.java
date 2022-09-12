package com.craft.silicon.centemobile.data.repository.forms;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class FormsRepository implements FormsDataSource {
    private final FormsDataSource remoteData;

    @Inject
    public FormsRepository(@Remote FormsDataSource remoteData) {
        this.remoteData = remoteData;
    }

    @Override
    public Single<DynamicResponse> requestWidget(PayloadData data, String path) {
        return remoteData.requestWidget(data, path);
    }
}
