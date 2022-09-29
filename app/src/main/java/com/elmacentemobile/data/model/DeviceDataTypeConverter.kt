package com.elmacentemobile.data.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder

class DeviceDataTypeConverter {

    fun from(data: DeviceData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, DeviceData::class.java)
    }

    fun to(data: String?): DeviceData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, DeviceData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}