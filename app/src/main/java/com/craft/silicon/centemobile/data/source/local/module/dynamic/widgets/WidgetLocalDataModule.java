package com.craft.silicon.centemobile.data.source.local.module.dynamic.widgets;

import com.craft.silicon.centemobile.data.source.local.database.AppDatabase;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class WidgetLocalDataModule {

    @Provides
    WidgetDao provideDao(AppDatabase appDatabase) {
        return appDatabase.widgetDao();
    }
}
