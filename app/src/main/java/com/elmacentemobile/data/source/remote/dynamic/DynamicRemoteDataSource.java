package com.elmacentemobile.data.source.remote.dynamic;

import com.elmacentemobile.data.repository.dynamic.DynamicDataSource;
import com.elmacentemobile.data.source.remote.callback.RequestData;
import com.elmacentemobile.data.source.remote.callback.ResponseData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class DynamicRemoteDataSource implements DynamicDataSource {

    private final DynamicApiService apiService;

    @Inject
    public DynamicRemoteDataSource(DynamicApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<ResponseData> requestBase(RequestData data) {
        return apiService.requestBase(data);
    }
}
