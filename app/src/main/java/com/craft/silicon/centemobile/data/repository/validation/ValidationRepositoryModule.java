package com.craft.silicon.centemobile.data.repository.validation;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.remote.validation.ValidationApiService;
import com.craft.silicon.centemobile.data.source.remote.validation.ValidationRemoteDataSource;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ValidationRepositoryModule {

    @Provides
    @Remote
    public ValidationDataSource remoteDataSource(ValidationApiService apiService) {
        return new ValidationRemoteDataSource(apiService);
    }

}
