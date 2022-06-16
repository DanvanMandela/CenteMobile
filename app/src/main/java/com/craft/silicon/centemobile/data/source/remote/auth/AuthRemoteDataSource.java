package com.craft.silicon.centemobile.data.source.remote.auth;

import com.craft.silicon.centemobile.data.repository.auth.AuthDataSource;
import com.craft.silicon.centemobile.data.source.remote.callback.RequestData;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class AuthRemoteDataSource implements AuthDataSource {
    private final AuthApiService apiService;

    @Inject
    public AuthRemoteDataSource(AuthApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<ResponseData> authRequest(RequestData data, String path) {
        return apiService.authRequest(data, path);
    }
}
