package com.craft.silicon.centemobile.data.repository.account;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;
import com.craft.silicon.centemobile.util.AppLogger;
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
