package com.craft.silicon.centemobile.view.model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker.*
import com.craft.silicon.centemobile.data.repository.dynamic.work.DynamicGETWorker
import com.craft.silicon.centemobile.data.worker.CleanDBWorker
import com.craft.silicon.centemobile.data.worker.WorkMangerDataSource
import com.craft.silicon.centemobile.data.worker.WorkerCommons
import com.craft.silicon.centemobile.data.worker.WorkerCommons.IS_WORK_DONE
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WorkerViewModel @Inject constructor(private val worker: WorkMangerDataSource) : ViewModel() {


    fun onWidgetData() {
        val workWorker = OneTimeWorkRequestBuilder<CleanDBWorker>()
        var continuation = worker.getWorkManger()
            .beginUniqueWork(
                WorkerCommons.TAG_DATA_WORKER +
                        BaseClass.generateAlphaNumericString(2),
                ExistingWorkPolicy.REPLACE,
                workWorker.build()
            )

        val moduleWorker = OneTimeWorkRequestBuilder<ModuleGETWorker>()
            .setConstraints(worker.getConstraint())
            .addTag(WorkerCommons.TAG_OUTPUT)
        continuation = continuation.then(moduleWorker.build())


        val formWorker = OneTimeWorkRequestBuilder<FormControlGETWorker>()
            .setConstraints(worker.getConstraint())
            .addTag(WorkerCommons.TAG_OUTPUT)
        continuation = continuation.then(formWorker.build())

        val actionWorker = OneTimeWorkRequestBuilder<ActionControlGETWorker>()
            .setConstraints(worker.getConstraint())
            .addTag(WorkerCommons.TAG_OUTPUT)
        continuation = continuation.then(actionWorker.build())

        val staticWorker = OneTimeWorkRequestBuilder<StaticDataGETWorker>()
            .setConstraints(worker.getConstraint())
            .addTag(WorkerCommons.TAG_OUTPUT)
        continuation = continuation.then(staticWorker.build())

        val atmWorker = OneTimeWorkRequestBuilder<ATMGETWorker>()
            .setConstraints(worker.getConstraint())
            .addTag(WorkerCommons.TAG_OUTPUT)
        continuation = continuation.then(atmWorker.build())

        val branchWorker = OneTimeWorkRequestBuilder<BranchGetWorker>()
            .setConstraints(worker.getConstraint())
            .addTag(WorkerCommons.TAG_OUTPUT)
        continuation = continuation.then(branchWorker.build())

        val carouselWorker = OneTimeWorkRequestBuilder<CarouselGETWorker>()
            .setConstraints(worker.getConstraint())
            .addTag(WorkerCommons.TAG_OUTPUT)
        continuation = continuation.then(carouselWorker.build())

        continuation.enqueue()

    }

    fun routeData(owner: LifecycleOwner, status: WorkStatus) {
        val routeWorker = OneTimeWorkRequestBuilder<DynamicGETWorker>()
            .addTag(WorkerCommons.TAG_OUTPUT).build()
        worker.getWorkManger().enqueue(routeWorker)
        AppLogger.instance.appLog("workInfo:id", Gson().toJson(routeWorker.id))
        worker.getWorkManger().getWorkInfoByIdLiveData(routeWorker.id)
            .observe(owner) { workInfo ->
                if (workInfo != null) {
                    val output = workInfo.outputData
                    val value = output.getBoolean(IS_WORK_DONE, false)
                    AppLogger.instance.appLog("workInfo:value", Gson().toJson(value))
                    status.workDone(value)
                }
            }
    }

}

interface WorkStatus {
    fun workDone(b: Boolean)
}

