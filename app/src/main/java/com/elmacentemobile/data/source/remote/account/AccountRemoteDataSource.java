package com.elmacentemobile.data.source.remote.account;

import com.elmacentemobile.data.repository.account.AccountDataSource;
import com.elmacentemobile.data.source.remote.callback.DynamicResponse;
import com.elmacentemobile.data.source.remote.callback.PayloadData;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class AccountRemoteDataSource implements AccountDataSource {
    private final AccountApiService apiService;

    @Inject
    public AccountRemoteDataSource(AccountApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<DynamicResponse> accountRequestT(String token, PayloadData data, String path) {
        return apiService.accountRequestT(token, data, path);
    }
}
