package com.craft.silicon.centemobile.data.repository.dynamic.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.craft.silicon.centemobile.data.model.DeviceDataTypeConverter
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum
import com.craft.silicon.centemobile.data.repository.dynamic.DynamicRepository
import com.craft.silicon.centemobile.data.source.constants.Constants
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.data.source.remote.callback.RequestData
import com.craft.silicon.centemobile.data.worker.WorkerCommons.IS_WORK_DONE
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

@HiltWorker
class DynamicGETWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val dynamicRepository: DynamicRepository,
    private val storageDataSource: StorageDataSource
) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {
        return try {

            val uniqueID = Constants.getUniqueID()
            val jsonObject = JSONObject()
            Constants.commonJSON(
                jsonObject,
                applicationContext,
                uniqueID,
                ActionTypeEnum.REQUEST_BASE.type,
                "",
                false
            )

            dynamicRepository.requestBase(
                RequestData(
                    uniqueID,
                    Constants.Data.CUSTOMER_ID,
                    Constants.getIMEIDeviceId(applicationContext),
                    Constants.Data.CODE_BASE,
                    "0.00",
                    "0.00",
                    BaseClass.hashLatest(jsonObject.toString()),
                    Constants.Data.APP_NAME
                )
            )
                .doOnError {
                    constructResponse(Result.failure())
                }
                .map {
                    if (it.respCode == StatusEnum.SUCCESS.type) {
                        var keys = ""
                        val device = it.payload?.device
                        val vice: CharArray = device!!.toCharArray()

                        for (i in it.data!!.toIntArray()) {
                            keys += vice[i]
                        }
                        val data = DeviceDataTypeConverter().to(
                            BaseClass.decryptLatest(
                                it.payload?.Routes,
                                keys, false,
                                it.payload?.ran
                            )
                        )
                        data?.token = it.token!!
                        data?.run = it.payload!!.ran
                        data?.device = keys
                        storageDataSource.setDeviceData(data!!)
                        AppLogger.instance.appLog("Routes", Gson().toJson(data))
                        constructResponse(
                            Result.success(
                                Data.Builder().putBoolean(IS_WORK_DONE, true).build()
                            )
                        )
                    } else constructResponse(Result.retry())

                }
                .onErrorReturn {
                    constructResponse(Result.retry())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
        } catch (e: Exception) {
            e.printStackTrace()
            e.localizedMessage?.let { AppLogger.instance.appLog("Routes", it) }
            Single.just(Result.failure())
        }
    }


    private fun constructResponse(result: Result): Result {
        return result
    }
}