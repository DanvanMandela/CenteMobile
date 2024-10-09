package com.elmacentemobile.data.worker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import com.elmacentemobile.data.worker.WorkerCommons.TAG_OUTPUT
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class WorkManagerApp @Inject constructor(@ApplicationContext private val context: Context) :
    WorkMangerDataSource {
    override fun getWorkManger(): WorkManager {
        return WorkManager.getInstance(context)
    }

    override fun getConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }

    override fun getConstraint(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    override fun outputWorkInfo(): LiveData<List<WorkInfo>> {
        return getWorkManger().getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

}