package com.elmacentemobile.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.elmacentemobile.R
import com.elmacentemobile.data.repository.dynamic.widgets.WidgetRepository
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.source.sync.SyncData
import com.elmacentemobile.data.worker.WorkerCommons.DELAY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class CleanDBWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val widgetRepository: WidgetRepository,
    private val dataSource: StorageDataSource
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            setSyncData(
                SyncData(
                    work = 1,
                    message = applicationContext.getString(R.string.warmin_up)
                )
            )
            widgetRepository.deleteFormControl()
            widgetRepository.deleteAction()
            widgetRepository.deleteFormModule()
            delay(DELAY)
            widgetRepository.storageStaticData()
            Result.success()
        } catch (e: Exception) {
            setSyncData(
                SyncData(
                    work = 1,
                    message = applicationContext.getString(R.string.warmin_up)
                )
            )
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun setSyncData(data: SyncData) {
        if (!dataSource.version.value.isNullOrEmpty())
            dataSource.setSync(data)
    }


}