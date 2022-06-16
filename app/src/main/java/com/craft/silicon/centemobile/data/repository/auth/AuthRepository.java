package com.craft.silicon.centemobile.data.repository.auth;

import android.util.Log;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.RequestData;
import com.craft.silicon.centemobile.data.source.remote.callback.ResponseData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class AuthRepository implements AuthDataSource {

    private final AuthDataSource remoteData;

    @Inject
    public AuthRepository(@Remote AuthDataSource remoteData) {
        this.remoteData = remoteData;
    }

    @Override
    public Single<ResponseData> authRequest(RequestData data, String path) {
        return remoteData.authRequest(data, path);
    }
}
