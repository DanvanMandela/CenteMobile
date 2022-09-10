package com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.SpiltURL
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum
import com.craft.silicon.centemobile.data.model.converter.WidgetDataTypeConverter
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepository
import com.craft.silicon.centemobile.data.source.constants.Constants
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData
import com.craft.silicon.centemobile.data.source.remote.helper.DynamicURL
import com.craft.silicon.centemobile.data.source.sync.SyncData
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.*

@HiltWorker
class ModuleGETWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val widgetRepository: WidgetRepository,
    private val storageDataSource: StorageDataSource,
    private val dataSource: StorageDataSource

) : RxWorker(context, workerParameters) {
    override fun createWork(): Single<Result> {
        return try {

            val activeData = storageDataSource.activationData.value
            val iv = storageDataSource.deviceData.value!!.run
            val device = storageDataSource.deviceData.value!!.device
            val uniqueID = Constants.getUniqueID()
            val jsonObject = JSONObject()
            Constants.commonJSON(
                jsonObject,
                applicationContext,
                uniqueID,
                ActionTypeEnum.GET_MENU.type,
                "",
                true,
                storageDataSource
            )
            AppLogger.instance.appLog("MODULES:REQ", Gson().toJson(jsonObject))
            val newRequest = jsonObject.toString()
            val path =
                (if (storageDataSource.deviceData.value == null) DynamicURL.static else Objects.requireNonNull(
                    storageDataSource.deviceData.value!!.staticData //TODO CHECK MODULE WORKER
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
                    setSyncData(
                        SyncData(
                            work = 6,
                            message = applicationContext.getString(R.string.loading_)
                        )
                    )

                    val dec = BaseClass.decompressStaticData(it.response)
                    AppLogger.instance.appLog(
                        "${ModuleGETWorker::class.simpleName}:Decode", dec
                    )

                    val data = WidgetDataTypeConverter().from(
//                        BaseClass.decryptLatest(
//                            it.response,
//                            storageDataSource.deviceData.value!!.device,
//                            true,
//                            storageDataSource.deviceData.value!!.run
//                        )
                        dec
                    )
                    AppLogger.instance.appLog("MODULES", Gson().toJson(data))
                    val status = data?.map { s -> s!!.status }?.single()
                    if (status == StatusEnum.SUCCESS.type) {
                        val message = data.map { s -> s!!.message }.single()
                        activeData?.message = message
                        activeData?.let { it1 -> storageDataSource.setActivationData(it1) }
                        val modules = data.map { s -> s?.modules }.single()
                        // modules?.forEach { s -> s.generateID() }
                        widgetRepository.saveModule(modules)
                        constructResponse(Result.success())

                    } else constructResponse(Result.retry())
                }
                .onErrorReturn {
                    setSyncData(
                        SyncData(
                            work = 5,
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
                    work = 5,
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
        if (!dataSource.version.value.isNullOrEmpty())
            dataSource.setSync(data)
    }
}