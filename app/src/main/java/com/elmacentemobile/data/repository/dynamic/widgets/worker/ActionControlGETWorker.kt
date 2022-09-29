package com.elmacentemobile.data.repository.dynamic.widgets.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.elmacentemobile.R
import com.elmacentemobile.data.model.SpiltURL
import com.elmacentemobile.data.model.action.ActionTypeEnum
import com.elmacentemobile.data.model.converter.WidgetDataTypeConverter
import com.elmacentemobile.data.repository.dynamic.widgets.WidgetRepository
import com.elmacentemobile.data.repository.forms.FormsRepository
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.source.remote.callback.PayloadData
import com.elmacentemobile.data.source.sync.SyncData
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
class ActionControlGETWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val widgetRepository: WidgetRepository,
    private val storageDataSource: StorageDataSource,
    private val formsRepository: FormsRepository
) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {
        return try {

            val iv = storageDataSource.deviceData.value!!.run
            val device = storageDataSource.deviceData.value!!.device
            val uniqueID = Constants.getUniqueID()
            val jsonObject = JSONObject()
            Constants.commonJSON(
                jsonObject,
                applicationContext,
                uniqueID,
                ActionTypeEnum.GET_ACTION_CONTROL.type,
                "",
                true,
                storageDataSource
            )
            AppLogger.instance.appLog("ACTION:REQ", Gson().toJson(jsonObject))
            val newRequest = jsonObject.toString()
            val path = storageDataSource.deviceData.value!!.staticData?.let {
                SpiltURL(
                    it
                ).path
            }
            formsRepository.requestWidget(
                PayloadData(
                    storageDataSource.uniqueID.value!!,
                    BaseClass.encryptString(newRequest, device, iv)
                ), path
            )
                .doOnError {
                    constructResponse(Result.failure())
                }
                .map {
                    setSyncData(
                        SyncData(
                            work = 8,
                            message = applicationContext.getString(R.string.loading_)
                        )
                    )
                    val dec = BaseClass.decompressStaticData(it.response)
                    AppLogger.instance.appLog("Actions:Decode", dec)

                    val data = WidgetDataTypeConverter().from(dec)
                    AppLogger.instance.appLog("ACTION", Gson().toJson(data))
                    val status = data?.map { s -> s?.status }?.single()
                    if (status == StatusEnum.SUCCESS.type) {
                        val actions = data.map { s -> s?.actionControls }.single()
                        widgetRepository.saveAction(actions)
                        constructResponse(Result.success())
                    } else constructResponse(Result.retry())

                }
                .onErrorReturn {
                    setSyncData(
                        SyncData(
                            work = 7,
                            message = applicationContext.getString(R.string.loading_)
                        )
                    )
                    constructResponse(Result.retry())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
        } catch (e: Exception) {
            setSyncData(
                SyncData(
                    work = 7,
                    message = applicationContext.getString(R.string.error)
                )
            )
            e.printStackTrace()
            Single.just(Result.failure())
        }
    }

    private fun constructResponse(result: Result): Result {
        return result
    }

    private fun setSyncData(data: SyncData) {
        if (!storageDataSource.version.value.isNullOrEmpty())
            storageDataSource.setSync(data)
    }
}