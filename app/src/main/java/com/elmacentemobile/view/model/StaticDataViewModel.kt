package com.elmacentemobile.view.model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.elmacentemobile.data.model.SpiltURL
import com.elmacentemobile.data.model.action.ActionTypeEnum
import com.elmacentemobile.data.repository.dynamic.widgets.WidgetDataSource
import com.elmacentemobile.data.repository.dynamic.widgets.WidgetRepository
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.source.remote.callback.DynamicResponse
import com.elmacentemobile.data.source.remote.callback.PayloadData
import com.elmacentemobile.data.source.remote.dynamic.widgets.WidgetApiService
import com.elmacentemobile.util.BaseClass
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Inject


@HiltViewModel
class StaticDataViewModel @Inject constructor(
    private val widgetRepository: WidgetRepository,
    private val storageDataSource: StorageDataSource
) : ViewModel(), WidgetDataSource {


    fun fetchStaticData(context: Context): Single<DynamicResponse> {
        val iv = storageDataSource.deviceData.value!!.run
        val device = storageDataSource.deviceData.value!!.device
        val uniqueID = Constants.getUniqueID()
        val jsonObject = JSONObject()
        Constants.commonJSON(
            jsonObject,
            context,
            uniqueID,
            ActionTypeEnum.STATIC_DATA.type,
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
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BaseUrl.UAT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val client = retrofit.create(WidgetApiService::class.java)
        return client.requestWidget(
            PayloadData(
                storageDataSource.uniqueID.value!!,
                BaseClass.encryptString(newRequest, device, iv)
            ), path
        )
    }

    fun fetchAtmData(context: Context): Single<DynamicResponse> {


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
        json.put("LAT", latLng?.latLng?.latitude ?: 0.0)
        json.put("BANKID", Constants.Data.BANK_ID)
        json.put("HEADER", "GetNearestATM")
        jsonObject.put("DynamicForm", json)
        Constants.commonJSON(
            jsonObject,
            context,
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


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BaseUrl.UAT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val client = retrofit.create(WidgetApiService::class.java)
        return client.requestWidget(
            PayloadData(
                storageDataSource.uniqueID.value!!,
                BaseClass.encryptString(newRequest, device, iv)
            ), path
        )
    }


}