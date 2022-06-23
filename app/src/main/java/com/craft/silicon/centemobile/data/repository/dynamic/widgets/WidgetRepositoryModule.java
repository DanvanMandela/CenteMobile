package com.craft.silicon.centemobile.data.repository.dynamic.widgets;

import com.craft.silicon.centemobile.data.scope.Local;
import com.craft.silicon.centemobile.data.scope.Remote;
import com.craft.silicon.centemobile.data.source.local.module.dynamic.widgets.WidgetDao;
import com.craft.silicon.centemobile.data.source.local.module.dynamic.widgets.WidgetLocalDataSource;
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource;
import com.craft.silicon.centemobile.data.source.remote.dynamic.widgets.WidgetApiService;
import com.craft.silicon.centemobile.data.source.remote.dynamic.widgets.WidgetRemoteDataSource;
import com.craft.silicon.centemobile.util.scheduler.BaseSchedulerProvider;

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
