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
class BranchGetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val widgetRepository: WidgetRepository,
    private val storageDataSource: StorageDataSource
) : RxWorker(context, workerParameters) {

    override fun createWork(): Single<Result> {
        return try {
            val iv = storageDataSource.deviceData.value!!.run
            val device = storageDataSource.deviceData.value!!.device
            val uniqueID = Constants.getUniqueID()
            val jsonObject = JSONObject()
            val json = JSONObject()

            val latLng: LatLng = storageDataSource.latLng.value!!.latLng

            json.put("COUNTRY", Constants.Data.COUNTRY)
            json.put("LON", latLng.longitude)
            json.put("APPNAME", Constants.Data.APP_NAME)
            json.put("LAT", latLng.latitude)
            json.put("BANKID", Constants.Data.BANK_ID)
            json.put("HEADER", "GetNearestBranch")
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
                    AppLogger.instance.appLog("Branch", Gson().toJson(data))
                    if (data?.data != null) {
                        val branches = mutableListOf<AtmData>()
                        data.data?.forEach { a ->
                            a.isATM = false
                            branches.add(a)
                        }
                        widgetRepository.saveAtms(branches)
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