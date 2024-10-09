package com.elmacentemobile.data.repository.auth;

import com.elmacentemobile.data.scope.Local;
import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.local.module.auth.AuthDao;
import com.elmacentemobile.data.source.local.module.auth.AuthLocalDataSource;
import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.data.source.remote.auth.AuthApiService;
import com.elmacentemobile.data.source.remote.auth.AuthRemoteDataSource;
import com.elmacentemobile.util.scheduler.BaseSchedulerProvider;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AuthRepositoryModule {

    @Provides
    @Remote
    AuthDataSource remoteDataSource(AuthApiService apiService, StorageDataSource storage) {
        return new AuthRemoteDataSource(apiService, storage);
    }

    @Provides
    @Local
    AuthDataSource localDataSource(AuthDao authDao, BaseSchedulerProvider schedulerProvider) {
        return new AuthLocalDataSource(authDao, schedulerProvider);
    }

}
