package com.craft.silicon.centemobile.data.repository.auth;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.auth.AuthApiService;
import com.craft.silicon.centemobile.data.source.remote.auth.AuthRemoteDataSource;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AuthRepositoryModule {

    @Provides
    @Remote
    AuthDataSource remoteDataSource(AuthApiService apiService) {
        return new AuthRemoteDataSource(apiService);
    }

}
