package com.elmacentemobile.data.repository.ip.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.elmacentemobile.data.repository.ip.IpStackRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single

@HiltWorker
class DevicePublicIpWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repo: IpStackRepository
) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {
        return repo.publicIp().doOnError {
            constructResponse(Result.failure())
        }.map {
            if (it.isEmpty()) constructResponse(Result.failure())
            else {
                val ip = it["ip"]
                constructResponse(
                    Result.success(
                        Data.Builder().putString("ip", ip).build()
                    )
                )
            }
        }
    }

    private fun constructResponse(result: Result): Result {
        return result
    }
}