package com.craft.silicon.centemobile.data.repository.account;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

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
        return remoteData.accountRequestT(token, data, path);
    }
}
