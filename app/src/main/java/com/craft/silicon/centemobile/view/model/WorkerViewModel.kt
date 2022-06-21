package com.craft.silicon.centemobile.view.model

import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import com.craft.silicon.centemobile.data.model.converter.LoginDataTypeConverter
import com.craft.silicon.centemobile.data.model.user.LoginUserData
import com.craft.silicon.centemobile.data.repository.auth.worker.AuthWorker
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

}