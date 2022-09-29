package com.elmacentemobile.view.model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import com.elmacentemobile.data.model.ocr.ImageRequestData
import com.elmacentemobile.data.model.ocr.ImageRequestDataTypeConverter
import com.elmacentemobile.data.repository.dynamic.widgets.worker.ActionControlGETWorker
import com.elmacentemobile.data.repository.dynamic.widgets.worker.FormControlGETWorker
import com.elmacentemobile.data.repository.dynamic.widgets.worker.ModuleGETWorker
import com.elmacentemobile.data.repository.dynamic.widgets.worker.StaticDataGETWorker
import com.elmacentemobile.data.repository.dynamic.work.DynamicGETWorker
import com.elmacentemobile.data.repository.ocr.worker.IDProcessingWorker
import com.elmacentemobile.data.repository.ocr.worker.ImageProcessingWorker
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.worker.CleanDBWorker
import com.elmacentemobile.data.worker.WorkMangerDataSource
import com.elmacentemobile.data.worker.WorkerCommons
import com.elmacentemobile.data.worker.WorkerCommons.IS_OCR_DONE
import com.elmacentemobile.data.worker.WorkerCommons.IS_WORK_DONE
import com.elmacentemobile.data.worker.WorkerCommons.IS_WORK_ERROR
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.view.fragment.go.steps.OCRConverter
import com.elmacentemobile.view.fragment.go.steps.OCRData
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
                ExistingWorkPolicy.REPLACE,
                workWorker.build()
            )


        val staticWorker = OneTimeWorkRequestBuilder<StaticDataGETWorker>()
            .setConstraints(worker.getConstraint())
            .addTag(WorkerCommons.TAG_OUTPUT)
        continuation = continuation.then(staticWorker.build())

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
                                    status.progress(it.toInt())
                                }
                            }

                        }
                    }
                }
            }
        dataSource.sync.asLiveData().observe(owner!!) {
            if (it != null) {
                if (it.work == 8) {
                    this@WorkerViewModel.apply {
                        Handler(Looper.myLooper()!!).postDelayed({
                            worker.getWorkManger().cancelUniqueWork(WorkerCommons.TAG_DATA_WORKER)
                        }, 2000)
                    }
                }
            }
        }
    }

    fun routeData(owner: LifecycleOwner, status: WorkStatus) {
        val routeWorker = OneTimeWorkRequestBuilder<DynamicGETWorker>()
            .addTag(WorkerCommons.TAG_OUTPUT)
            .build()
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



