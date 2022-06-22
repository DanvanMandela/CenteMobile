package com.craft.silicon.centemobile.data.repository.dynamic.widgets;

import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.dynamic.widgets.WidgetApiService;
import com.craft.silicon.centemobile.data.source.remote.dynamic.widgets.WidgetRemoteDataSource;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class WidgetRepositoryModule {

    @Provides
    @Remote
    WidgetDataSource remoteData(WidgetApiService apiService, StorageDataSource storageDataSource) {
        return new WidgetRemoteDataSource(apiService, storageDataSource);
    }
}
