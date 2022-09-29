package com.elmacentemobile.data.repository.account;

import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;
import com.elmacentemobile.util.AppLogger;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class AccountRepository implements AccountDataSource {
    private final AccountDataSource remoteData;

    @Inject
    public AccountRepository(@Remote AccountDataSource remoteData) {
        this.remoteData = remoteData;
    }

    @Override
    public Single<DynamicResponse> accountRequestT(String token, PayloadData data, String path) {
        AppLogger.Companion.getInstance().appLog("MAIN:DATA", new Gson().toJson(data));
        return remoteData.accountRequestT(token, data, path);
    }
}
