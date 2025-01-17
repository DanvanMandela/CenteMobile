package com.craft.silicon.centemobile.data.repository.dynamic.widgets.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.AtmData
import com.craft.silicon.centemobile.data.model.SpiltURL
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum
import com.craft.silicon.centemobile.data.model.converter.StaticDataTypeConverter
import com.craft.silicon.centemobile.data.repository.dynamic.widgets.WidgetRepository
import com.craft.silicon.centemobile.data.repository.forms.FormsRepository
import com.craft.silicon.centemobile.data.source.constants.Constants
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.data.source.remote.callback.PayloadData
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

@HiltWorker
class StaticDataGETWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val forms: FormsRepository,
    private val widgetRepository: WidgetRepository,
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
                ActionTypeEnum.STATIC_DATA.type,
                if (customerID.isNullOrBlank()) "" else customerID,
                true,
                storageDataSource
            )
            val newRequest = jsonObject.toString()
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
                    constructResponse(Result.failure())
                }
                .map {
                    setSyncData(
                        SyncData(
                            work = 2,
                            message = applicationContext.getString(R.string.loading_)
                        )
                    )
                    val dec = BaseClass.decompressStaticData(it.response)
                    AppLogger.instance.appLog("STATIC:Decode", dec)


                    val data = StaticDataTypeConverter().to(dec)
                    AppLogger.instance.appLog("STATIC:DATA", Gson().toJson(data))
                    AppLogger.instance.appLog("STATIC:ATM", Gson().toJson(data?.atms))
                    AppLogger.instance.appLog("STATIC:Branch", Gson().toJson(data?.branch))
                    AppLogger.instance.appLog("STATIC:Carousel", Gson().toJson(data?.carousel))

                    if (data?.status == StatusEnum.SUCCESS.type) {
                        if (data.userCode.isNotEmpty())
                            storageDataSource.setStaticData(data.userCode.toMutableList())
                        if (data.accountProduct.isNotEmpty())
                            storageDataSource.setProductAccountData(
                                data.accountProduct.toMutableList()
                            )
                        if (data.bankBranch.isNotEmpty())
                            storageDataSource.setBranchData(
                                data.bankBranch.toMutableList()
                            )
                        if (!data.appIdleTimeout.isNullOrBlank())
                            storageDataSource.setTimeout(
                                data.appIdleTimeout!!.toLong()
                            )
                        if (!data.rateMax.isNullOrBlank())
                            storageDataSource.setFeedbackTimerMax(
                                data.rateMax!!.toInt()
                            )
                        if (!data.branch.isNullOrEmpty()) {
                            val branches = mutableListOf<AtmData>()
                            data.branch?.forEach { a ->
                                a.isATM = false
                                branches.add(a)
                            }
                            widgetRepository.saveAtms(branches)
                            setSyncData(
                                SyncData(
                                    work = 3,
                                    message = applicationContext.getString(R.string.loading_)
                                )
                            )
                        }
                        if (!data.atms.isNullOrEmpty()) {
                            val branches = mutableListOf<AtmData>()
                            data.atms?.forEach { a ->
                                a.isATM = true
                                branches.add(a)
                            }
                            widgetRepository.saveAtms(branches)
                            setSyncData(
                                SyncData(
                                    work = 4,
                                    message = applicationContext.getString(R.string.loading_)
                                )
                            )
                        }
                        if (!data.carousel.isNullOrEmpty()) {
                            val carousels = data.carousel!!.filter { c -> c.category == "ADDS" }
                            widgetRepository.saveCarousel(carousels)
                            setSyncData(
                                SyncData(
                                    work = 5,
                                    message = applicationContext.getString(R.string.loading_)
                                )
                            )
                        }
                        constructResponse(Result.success())
                    } else {
                        constructResponse(Result.retry())
                    }
                }
                .onErrorReturn {
                    setSyncData(
                        SyncData(
                            work = 1,
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
                    work = 1,
                    message = applicationContext.getString(R.string.error)
                )
            )
            e.printStackTrace()
            e.localizedMessage?.let { Log.e("StaticDataGET", it) }
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