package com.elmacentemobile.data.repository.ip.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.elmacentemobile.data.repository.ip.IpStackRepository
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single

@HiltWorker
class DeviceIpDetailsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repo: IpStackRepository
) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {
        val ip = inputData.getString("ip")
        return repo.ipStack(ip!!).doOnError {
            constructResponse(Result.failure())
        }.map {
            Log.e("IP", Gson().toJson(it))
            constructResponse(Result.success())
        }
    }

    private fun constructResponse(result: Result): Result {
        return result
    }
}