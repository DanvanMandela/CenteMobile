package com.craft.silicon.centemobile.data.repository.auth;

import com.craft.silicon.centemobile.data.scope.Local;
import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.local.module.auth.AuthDao;
import com.craft.silicon.centemobile.data.source.local.module.auth.AuthLocalDataSource;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.auth.AuthApiService;
import com.craft.silicon.centemobile.data.source.remote.auth.AuthRemoteDataSource;
import com.craft.silicon.centemobile.util.scheduler.BaseSchedulerProvider;

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
