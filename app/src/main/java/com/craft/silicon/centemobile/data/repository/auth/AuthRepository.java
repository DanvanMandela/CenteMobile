package com.craft.silicon.centemobile.data.repository.auth;

import com.craft.silicon.centemobile.data.model.user.Accounts;
import com.craft.silicon.centemobile.data.model.user.FrequentModules;
import com.craft.silicon.centemobile.data.scope.Local;
import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse;
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;

@Singleton
public class AuthRepository implements AuthDataSource {

    private final AuthDataSource remoteData;
    private final AuthDataSource localData;

    @Inject
    public AuthRepository(@Remote AuthDataSource remoteData, @Local AuthDataSource localData) {
        this.remoteData = remoteData;
        this.localData = localData;
    }

    @Override
    public Single<DynamicResponse> authRequest(PayloadData data, String path) {
        return remoteData.authRequest(data, path);
    }

    @Override
    public void saveFrequentModule(List<FrequentModules> modules) {
        localData.saveFrequentModule(modules);
    }

    @Override
    public Observable<List<FrequentModules>> getFrequentModules() {
        return localData.getFrequentModules();
    }

    @Override
    public void saveAccountModule(List<Accounts> modules) {
        localData.saveAccountModule(modules);
    }

    @Override
    public Observable<List<Accounts>> getAccount() {
        return localData.getAccount();
    }
}
