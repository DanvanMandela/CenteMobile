package com.craft.silicon.centemobile.data.repository.validation;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

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
        return remoteData.validationRequest(token, data, path);
    }
}
