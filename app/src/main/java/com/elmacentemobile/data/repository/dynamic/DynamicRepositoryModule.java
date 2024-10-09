package com.elmacentemobile.data.repository.dynamic;

import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.remote.dynamic.DynamicApiService;
import com.elmacentemobile.data.source.remote.dynamic.DynamicRemoteDataSource;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DynamicRepositoryModule {

    @Provides
    @Remote
    DynamicDataSource dynamicRemoteDataSource(DynamicApiService apiService) {
        return new DynamicRemoteDataSource(apiService);
    }
}
