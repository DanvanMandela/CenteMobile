package com.craft.silicon.centemobile.data.source.local.module.auth;

import com.craft.silicon.centemobile.data.source.local.database.AppDatabase;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AuthLocalDataModule {


    @Provides
    AuthDao authDao(AppDatabase appDatabase) {
        return appDatabase.authDao();
    }

}
