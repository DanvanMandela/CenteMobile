package com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepositoryModule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single

@HiltWorker
class FormControlGETWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val widgetRepositoryModule: WidgetRepositoryModule

) : RxWorker(context, workerParameters){
    override fun createWork(): Single<Result> {
        TODO("Not yet implemented")
    }
}