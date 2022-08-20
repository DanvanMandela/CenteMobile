package com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.craft.silicon.centemobile.data.model.ATMTypeConverter
import com.craft.silicon.centemobile.data.model.AtmData
import com.craft.silicon.centemobile.data.model.SpiltURL
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepository
import com.craft.silicon.centemobile.data.source.constants.Constants
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.*

@HiltWorker
class ATMGETWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val widgetRepository: WidgetRepository,
    private val storageDataSource: StorageDataSource
) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {
        return try {
            widgetRepository.deleteAtms()
            val iv = storageDataSource.deviceData.value!!.run
            val device = storageDataSource.deviceData.value!!.device
            val uniqueID = Constants.getUniqueID()
            val jsonObject = JSONObject()
            val json = JSONObject()

            val latLng = storageDataSource.latLng.value

            json.put("COUNTRY", Constants.Data.COUNTRY)
            json.put("LON", latLng?.latLng?.longitude ?: 0.0)
            json.put("APPNAME", Constants.Data.APP_NAME)
            json.put("LAT",latLng?.latLng?.latitude ?: 0.0)
            json.put("BANKID", Constants.Data.BANK_ID)
            json.put("HEADER", "GetNearestATM")
            jsonObject.put("DynamicForm", json)
            Constants.commonJSON(
                jsonObject,
                applicationContext,
                uniqueID,
                ActionTypeEnum.DB_CALL.type,
                "",
                true,
                storageDataSource
            )

            val newRequest = jsonObject.toString()
            val path =
                (if (storageDataSource.deviceData.value == null) Constants.BaseUrl.UAT else Objects.requireNonNull(
                    storageDataSource.deviceData.value!!.other
                ))?.let {
                    SpiltURL(
                        it
                    ).path
                }
            widgetRepository.requestWidget(
                PayloadData(
                    storageDataSource.uniqueID.value!!,
                    BaseClass.encryptString(newRequest, device, iv)
                ), path
            )
                .doOnError {
                    constructResponse(Result.failure())
                }
                .map {

                    val data = ATMTypeConverter().to(
                        BaseClass.decryptLatest(
                            it.response,
                            storageDataSource.deviceData.value!!.device,
                            true,
                            storageDataSource.deviceData.value!!.run
                        )
                    )
                    AppLogger.instance.appLog("ATMS", Gson().toJson(data))

                    if (data?.data != null) {
                        val atms = mutableListOf<AtmData>()
                        data.data?.forEach { a ->
                            a.isATM = true
                            atms.add(a)
                        }
                        widgetRepository.saveAtms(atms)
                    }

                    constructResponse(Result.success())
                }
                .onErrorReturn {
                    constructResponse(Result.retry())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
        } catch (e: Exception) {
            e.printStackTrace()
            Single.just(Result.failure())
        }
    }

    private fun constructResponse(result: Result): Result {
        return result
    }
}