package com.craft.silicon.centemobile.view.model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestData
import com.craft.silicon.centemobile.data.model.ocr.ImageRequestDataTypeConverter
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker.*
import com.craft.silicon.centemobile.data.repository.dynamic.work.DynamicGETWorker
import com.craft.silicon.centemobile.data.repository.ocr.worker.IDProcessingWorker
import com.craft.silicon.centemobile.data.repository.ocr.worker.ImageProcessingWorker
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.data.worker.CleanDBWorker
import com.craft.silicon.centemobile.data.worker.SessionWorkManager
import com.craft.silicon.centemobile.data.worker.WorkMangerDataSource
import com.craft.silicon.centemobile.data.worker.WorkerCommons
import com.craft.silicon.centemobile.data.worker.WorkerCommons.IS_OCR_DONE
import com.craft.silicon.centemobile.data.worker.WorkerCommons.IS_WORK_DONE
import com.craft.silicon.centemobile.data.worker.WorkerCommons.IS_WORK_ERROR
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRConverter
import com.craft.silicon.centemobile.view.fragment.go.steps.OCRData
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class WorkerViewModel @Inject constructor(
    private val worker: WorkMangerDataSource,
    private val dataSource: StorageDataSource
) : ViewModel() {


    fun onWidgetData(owner: LifecycleOwner?, status: WorkStatus?) {
        val workWorker = OneTimeWorkRequestBuilder<CleanDBWorker>()
        var continuation = worker.getWorkManger()
            .beginUniqueWork(
                WorkerCommons.TAG_DATA_WORKER,
                ExistingWorkPolicy.KEEP,
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

        if (owner != null && status != null)
            continuation.workInfosLiveData.observe(owner) { workInfo ->
                if (workInfo.isNotEmpty()) {
                    val progress = MutableLiveData(0.0)
                    workInfo.forEachIndexed { index, info ->
                        val state = info.state
                        val start = index.plus(1).toDouble()
                        if (state.isFinished && state == WorkInfo.State.SUCCEEDED) {
                            progress.value = (start.div(workInfo.size)).times(100)
                            if (info == workInfo.first()) {
                                progress.observe(owner) {
                                    status?.progress(it.toInt())
                                }
                            }

                        }
                    }
                }
            }
        dataSource.sync.asLiveData().observe(owner!!) {
            if (it != null) {
                if (it.work == 8) {
                    worker.getWorkManger().cancelUniqueWork(WorkerCommons.TAG_DATA_WORKER)
                }
            }
        }
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

    fun timeOut(owner: LifecycleOwner, status: WorkStatus) {
        val routeWorker = OneTimeWorkRequestBuilder<SessionWorkManager>()
            .addTag(WorkerCommons.TAG_TIME_OUT_WORKER).build()
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


    fun processID(data: ImageRequestData, owner: LifecycleOwner, status: WorkStatus) {
        val processImage = OneTimeWorkRequestBuilder<ImageProcessingWorker>()
            .addTag(WorkerCommons.TAG_OUTPUT)

        processImage.setInputData(
            Data.Builder()
                .putString(WorkerCommons.ID_DATA, ImageRequestDataTypeConverter().from(data))
                .build()
        )
        val workID = "${WorkerCommons.ID_WORKER}${BaseClass.generateAlphaNumericString(2)}"
        var continuation = worker.getWorkManger()
            .beginUniqueWork(
                workID,
                ExistingWorkPolicy.REPLACE,
                processImage.build()
            )
        val processID = OneTimeWorkRequestBuilder<IDProcessingWorker>()
            .setConstraints(worker.getConstraint())
            .addTag(WorkerCommons.TAG_OUTPUT)
        continuation = continuation.then(processID.build())

        continuation.enqueue()

        continuation.workInfosLiveData.observe(owner) { workInfo ->
            if (workInfo.isNotEmpty()) {
                workInfo.forEachIndexed { _, info ->
                    if (info != null) {
                        val output = info.outputData
                        val value = output.getBoolean(IS_WORK_DONE, false)
                        AppLogger.instance.appLog("workInfo:value", Gson().toJson(value))
                        status.workDone(value)
                        val error = output.getString(IS_WORK_ERROR)
                        if (!error.isNullOrEmpty()) {
                            status.error(error)
                            worker.getWorkManger().cancelUniqueWork(workID)
                        }
                        val ocrData = OCRConverter().to(output.getString(IS_OCR_DONE))
                        if (ocrData != null) {
                            AppLogger.instance.appLog("workInfo:ocr", Gson().toJson(ocrData))
                            status.onOCRData(ocrData, false)
                        }
                    }
                }

            }
        }

    }


}

interface WorkStatus {
    fun workDone(b: Boolean) {
        throw Exception("Not implemented")
    }

    fun progress(p: Int) {
        throw Exception("Not implemented")
    }

    fun error(p: String?) {
        throw Exception("Not implemented")
    }

    fun onOCRData(data: OCRData, b: Boolean) {
        throw Exception("Not implemented")
    }


}



