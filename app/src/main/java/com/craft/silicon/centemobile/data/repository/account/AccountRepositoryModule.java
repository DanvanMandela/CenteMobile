package com.craft.silicon.centemobile.data.repository.account;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.account.AccountApiService;
import com.craft.silicon.centemobile.data.source.remote.account.AccountRemoteDataSource;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class AccountRepositoryModule {


    @Provides
    @Remote
    public AccountDataSource remoteData(AccountApiService apiService) {
        return new AccountRemoteDataSource(apiService);
    }
}
