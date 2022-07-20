package com.craft.silicon.centemobile.di.module

import android.app.Application
import android.content.Context
import com.craft.silicon.centemobile.data.source.pref.SharedPreferencesStorage
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.data.source.remote.helper.NetworkDataSource
import com.craft.silicon.centemobile.data.source.remote.helper.NetworkMonitor
import com.craft.silicon.centemobile.data.worker.WorkManagerApp
import com.craft.silicon.centemobile.data.worker.WorkMangerDataSource
import com.craft.silicon.centemobile.util.provider.ResourceProvider
import com.craft.silicon.centemobile.util.scheduler.BaseSchedulerProvider
import com.craft.silicon.centemobile.util.scheduler.SchedulerProvider
import com.craft.silicon.centemobile.view.navigation.NavigationDataSource
import com.craft.silicon.centemobile.view.navigation.NavigationDirection
import com.craft.silicon.centemobile.util.provider.BaseResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindContext(application: Application?): Context?

    @Binds
    abstract fun provideStorage(storage: SharedPreferencesStorage): StorageDataSource

    @Binds
    abstract fun provideNetworkMonitor(monitor: NetworkMonitor): NetworkDataSource

    @Binds
    abstract fun provideScheduler(scheduler: SchedulerProvider): BaseSchedulerProvider

    @Binds
    abstract fun bindResourceProvider(resourceProvider: ResourceProvider): BaseResourceProvider

    @Binds
    abstract fun bindWorkManger(work: WorkManagerApp): WorkMangerDataSource

    @Binds
    abstract fun provideNavigation(navigationDirection: NavigationDirection): NavigationDataSource


}