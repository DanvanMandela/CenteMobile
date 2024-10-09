package com.elmacentemobile.data.repository.forms;


import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.data.source.remote.forms.FormsApiService;
import com.elmacentemobile.data.source.remote.forms.FormsRemoteDataSource;

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
