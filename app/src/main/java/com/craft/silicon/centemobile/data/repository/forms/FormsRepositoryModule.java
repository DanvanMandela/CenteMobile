package com.craft.silicon.centemobile.data.repository.forms;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.forms.FormsApiService;
import com.craft.silicon.centemobile.data.source.remote.forms.FormsRemoteDataSource;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class FormsRepositoryModule {

    @Provides
    @Remote
    FormsDataSource remoteData(FormsApiService apiService, StorageDataSource storageDataSource) {
        return new FormsRemoteDataSource(apiService, storageDataSource);
    }
}
