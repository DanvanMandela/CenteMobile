package com.elmacentemobile.di.module

import android.app.Application
import android.content.Context
import com.elmacentemobile.data.service.CurrentActivity
import com.elmacentemobile.data.service.CurrentActivityDataSource
import com.elmacentemobile.data.service.InteractionDataSource
import com.elmacentemobile.data.service.UserInteractionWatcher
import com.elmacentemobile.data.source.pref.SharedPreferencesStorage
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.source.remote.helper.ConnectionMonitor
import com.elmacentemobile.data.source.remote.helper.ConnectionObserver
import com.elmacentemobile.data.worker.WorkManagerApp
import com.elmacentemobile.data.worker.WorkMangerDataSource
import com.elmacentemobile.util.SimData
import com.elmacentemobile.util.SimUtil
import com.elmacentemobile.util.provider.BaseResourceProvider
import com.elmacentemobile.util.provider.ResourceProvider
import com.elmacentemobile.util.scheduler.BaseSchedulerProvider
import com.elmacentemobile.util.scheduler.SchedulerProvider
import com.elmacentemobile.view.navigation.NavigationDataSource
import com.elmacentemobile.view.navigation.NavigationDirection
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
    abstract fun provideConnectionMonitor(connectionMonitor: ConnectionMonitor): ConnectionObserver

    @Binds
    abstract fun provideScheduler(scheduler: SchedulerProvider): BaseSchedulerProvider

    @Binds
    abstract fun bindResourceProvider(resourceProvider: ResourceProvider): BaseResourceProvider

    @Binds
    abstract fun bindWorkManger(work: WorkManagerApp): WorkMangerDataSource

    @Binds
    abstract fun provideNavigation(navigationDirection: NavigationDirection): NavigationDataSource


    @Binds
    abstract fun provideSimDetails(simUtil: SimUtil): SimData

    @Binds
    abstract fun provideCurrentActivity(currentActivity: CurrentActivity): CurrentActivityDataSource

    @Binds
    abstract fun provideInteraction(userInteractionWatcher: UserInteractionWatcher): InteractionDataSource


}