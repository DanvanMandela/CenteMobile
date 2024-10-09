package com.elmacentemobile.data.repository.auth;

import com.elmacentemobile.data.model.user.Accounts;
import com.elmacentemobile.data.model.user.Beneficiary;
import com.elmacentemobile.data.model.user.FrequentModules;
import com.elmacentemobile.data.scope.Local;
import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;
import com.elmacentemobile.util.AppLogger;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;

@Singleton
public class AuthRepository implements AuthDataSource {

    private final AuthDataSource remoteData;
    private final AuthDataSource localData;
    private final StorageDataSource storageDataSource;

    @Inject
    public AuthRepository(@Remote AuthDataSource remoteData, @Local AuthDataSource localData, StorageDataSource storageDataSource) {
        this.remoteData = remoteData;
        this.localData = localData;
        this.storageDataSource = storageDataSource;
    }

    @Override
    public Single<DynamicResponse> authRequest(PayloadData data, String path) {
        AppLogger.Companion.getInstance().appLog("MAIN:DATA", new Gson().toJson(data));
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
    public void saveVersion(String str) {
        storageDataSource.setVersion(str);
    }

    @Override
    public Observable<List<Accounts>> getAccount() {
        return localData.getAccount();
    }

    @Override
    public Observable<List<Beneficiary>> geBeneficiary() {
        return localData.geBeneficiary();
    }

    @Override
    public void saveBeneficiary(List<Beneficiary> modules) {
        localData.saveBeneficiary(modules);
    }
}
