package com.craft.silicon.centemobile.data.repository.dynamic;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.dynamic.DynamicApiService;
import com.craft.silicon.centemobile.data.source.remote.dynamic.DynamicRemoteDataSource;

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
