package com.elmacentemobile.di.module

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import com.elmacentemobile.data.model.DeviceData
import com.elmacentemobile.data.scope.BaseUrl
import com.elmacentemobile.data.scope.Token
import com.elmacentemobile.data.scope.UserId
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.source.local.database.AppDatabase
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Provides
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.excludeFieldsWithoutExposeAnnotation()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }

    @Provides
    fun provideDb(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            Constants.RoomDatabase.DATABASE_NAME
        ).addCallback(object : RoomDatabase.Callback() {
        }).fallbackToDestructiveMigration().build()
    }

    @Token
    @Provides
    fun provideAuthToken(storage: StorageDataSource): String? {
        return storage.token.value
    }

    @BaseUrl
    @Provides
    fun provideBaseUrl(storage: StorageDataSource): LiveData<DeviceData?> {
        return storage.deviceData.asLiveData()
    }

//
//    @UserId
//    @Provides
//    fun provideUserId(storage: StorageDataSource): String? {
//        return storage.userId.value
//    }

    @Provides
    @UserId
    fun deviceData(storage: StorageDataSource): LiveData<DeviceData?> {
        return storage.deviceData.asLiveData()
    }
}