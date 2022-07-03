package com.craft.silicon.centemobile.view.model

import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.craft.silicon.centemobile.data.model.converter.LoginDataTypeConverter
import com.craft.silicon.centemobile.data.model.user.LoginUserData
import com.craft.silicon.centemobile.data.repository.auth.worker.AuthWorker
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker.ActionControlGETWorker
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker.FormControlGETWorker
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker.ModuleGETWorker
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker.StaticDataGETWorker
import com.craft.silicon.centemobile.data.worker.CleanDBWorker
import com.craft.silicon.centemobile.data.worker.WorkMangerDataSource
import com.craft.silicon.centemobile.data.worker.WorkerCommons
import com.craft.silicon.centemobile.util.BaseClass
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkerViewModel @Inject constructor(private val worker: WorkMangerDataSource) : ViewModel() {

    fun onLoginData(data: LoginUserData) {
        val authWorker = OneTimeWorkRequestBuilder<AuthWorker>()
        authWorker.setInputData(
            Data.Builder()
                .putString(
                    WorkerCommons.TAG_APP_DATA,
                    LoginDataTypeConverter().from(data)
                )
                .build()
        )
        val continuation = worker.getWorkManger()
            .beginUniqueWork(
                "${WorkerCommons.TAG_DATA_WORKER}${BaseClass.generateAlphaNumericString(2)}",
                ExistingWorkPolicy.REPLACE,
                authWorker.build()
            )
        continuation.enqueue()
    }

    fun onWidgetData() {
        val workWorker = OneTimeWorkRequestBuilder<CleanDBWorker>()
        var continuation = worker.getWorkManger()
            .beginUniqueWork(
                "${WorkerCommons.TAG_DATA_WORKER}${BaseClass.generateAlphaNumericString(2)}",
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

        continuation.enqueue()
    }

}