package com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.craft.silicon.centemobile.data.model.converter.NotificationDataConverter
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepository
import com.craft.silicon.centemobile.data.worker.WorkerCommons
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val widgetRepository: WidgetRepository
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val data =
                NotificationDataConverter()
                    .to(inputData.getString(WorkerCommons.TAG_NOTIFICATION_DATA))
            widgetRepository.saveNotifications(data)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

}

