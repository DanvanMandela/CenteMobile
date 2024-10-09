package com.elmacentemobile.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.elmacentemobile.data.source.pref.StorageDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class SessionWorkManager @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val storage: StorageDataSource
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        delay(storage.timeout.value!!.toLong())
        return Result.success(
            Data.Builder().putBoolean(WorkerCommons.IS_WORK_DONE, true).build()
        )
    }

}