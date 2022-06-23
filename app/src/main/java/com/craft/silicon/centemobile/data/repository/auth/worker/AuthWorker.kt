package com.craft.silicon.centemobile.data.repository.auth.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.craft.silicon.centemobile.data.model.converter.LoginDataTypeConverter
import com.craft.silicon.centemobile.data.model.user.LoginUserData
import com.craft.silicon.centemobile.data.repository.auth.AuthRepository
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.data.worker.WorkerCommons
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AuthWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val authRepository: AuthRepository,
    private val storageSource: StorageDataSource? = null
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        return try {
            val data: LoginUserData? =
                LoginDataTypeConverter().to(
                    inputData.getString(
                        WorkerCommons.TAG_APP_DATA
                    )
                )
            updateActivationData(data)
            authRepository.saveVersion(data?.version)
            authRepository.saveBeneficiary(data?.beneficiary)
            authRepository.saveAccountModule(data?.accounts)
            authRepository.saveFrequentModule(data?.modules)
            Result.success()

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun updateActivationData(data: LoginUserData?) {
        val userData = storageSource?.activationData?.value
        userData?.firstName = data?.firstName
        userData?.lastName = data?.lastName
        userData?.email = data?.emailId
        userData?.imageURL = data?.imageURL
        userData?.iDNumber = data?.iDNumber
        if (!data?.message.isNullOrEmpty())
            userData?.message = data?.message
        storageSource?.setActivationData(userData!!)
    }

}