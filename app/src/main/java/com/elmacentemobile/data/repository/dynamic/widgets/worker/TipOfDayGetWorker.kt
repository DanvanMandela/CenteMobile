package com.elmacentemobile.data.repository.dynamic.widgets.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.elmacentemobile.data.model.SpiltURL
import com.elmacentemobile.data.model.action.ActionTypeEnum
import com.elmacentemobile.data.model.converter.StaticDataTypeConverter
import com.elmacentemobile.data.repository.forms.FormsRepository
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.source.remote.callback.PayloadData
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

@HiltWorker
class TipOfDayGetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val forms: FormsRepository,
    private val storageDataSource: StorageDataSource
) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {
        return try {
            val iv = storageDataSource.deviceData.value!!.run
            val device = storageDataSource.deviceData.value!!.device
            val uniqueID = Constants.getUniqueID()
            val customerID: String? = storageDataSource.activationData.value?.id
            val jsonObject = JSONObject()
            Constants.commonJSON(
                jsonObject,
                applicationContext,
                uniqueID,
                ActionTypeEnum.DAY_TIP.type,
                if (customerID.isNullOrBlank()) "" else customerID,
                true,
                storageDataSource
            )
            val newRequest = jsonObject.toString()
            AppLogger.instance.appLog("TIPS","$newRequest")
            val path = storageDataSource.deviceData.value!!.staticData?.let {
                SpiltURL(
                    it
                ).path
            }
            forms.requestWidget(
                PayloadData(
                    storageDataSource.uniqueID.value!!,
                    BaseClass.encryptString(newRequest, device, iv)
                ), path
            )
                .doOnError {
                    constructResponse(Result.retry())
                }
                .map {
                    val dec = BaseClass.decompressStaticData(it.response)
                    AppLogger.instance.appLog("TipOFDay:Decode", dec)
                    AppLogger.instance.appLog("TipOFDay:encode", "${it.response}")
                    val data = StaticDataTypeConverter().to(dec)
                    AppLogger.instance.appLog("TIP:DATA", Gson().toJson(data?.tips))

                    if (data?.status == StatusEnum.SUCCESS.type) {
                        if (!data.tips.isNullOrEmpty()) {
                            storageDataSource.dayTipData(data.tips)
                        }
                        constructResponse(Result.success())
                    } else {
                        constructResponse(Result.retry())
                    }
                }
                .onErrorReturn {
                    constructResponse(Result.retry())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
        } catch (e: Exception) {
            e.printStackTrace()
            e.localizedMessage?.let { Log.e("TipDayGET", it) }
            Single.just(Result.retry())
        }
    }

    private fun constructResponse(result: Result): Result {
        return result
    }
}