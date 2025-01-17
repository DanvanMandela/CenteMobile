package com.craft.silicon.centemobile.data.source.remote.auth;

import com.craft.silicon.centemobile.data.repository.auth.AuthDataSource;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class AuthRemoteDataSource implements AuthDataSource {
    private final AuthApiService apiService;
    private final StorageDataSource storage;

    @Inject
    public AuthRemoteDataSource(AuthApiService apiService, StorageDataSource storage) {
        this.apiService = apiService;
        this.storage = storage;
    }

    @Override
    public Single<DynamicResponse> authRequest(PayloadData data, String path) {
        return apiService.authRequestT(storage.getDeviceData().getValue().getToken(), data, path);
    }
}
