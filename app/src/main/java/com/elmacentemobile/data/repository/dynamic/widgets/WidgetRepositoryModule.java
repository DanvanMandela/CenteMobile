package com.elmacentemobile.data.repository.dynamic.widgets;

import com.elmacentemobile.data.scope.Local;
import com.elmacentemobile.data.scope.Remote;
import com.elmacentemobile.data.source.local.module.dynamic.widgets.WidgetDao;
import com.elmacentemobile.data.source.local.module.dynamic.widgets.WidgetLocalDataSource;
import com.elmacentemobile.data.source.pref.StorageDataSource;
import com.elmacentemobile.data.source.remote.dynamic.widgets.WidgetApiService;
import com.elmacentemobile.data.source.remote.dynamic.widgets.WidgetRemoteDataSource;
import com.elmacentemobile.util.scheduler.BaseSchedulerProvider;

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

    @Provides
    @Local
    WidgetDataSource localData(WidgetDao widgetDao, BaseSchedulerProvider schedulerProvider) {
        return new WidgetLocalDataSource(widgetDao, schedulerProvider);
    }
}
