package com.craft.silicon.centemobile.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CleanDBWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val widgetRepository: WidgetRepository
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        return try {
            widgetRepository.deleteFormControl()
            widgetRepository.deleteAction()
            widgetRepository.deleteFormModule()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

}