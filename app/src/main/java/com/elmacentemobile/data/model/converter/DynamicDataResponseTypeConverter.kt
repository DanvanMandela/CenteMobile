package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.data.model.dynamic.DynamicDataResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class DynamicDataResponseTypeConverter {

    @TypeConverter
    fun from(data: DynamicDataResponse?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, DynamicDataResponse::class.java)
    }

    @TypeConverter
    fun to(str: String?): DynamicDataResponse? {
        return if (str == null) {
            null
        } else gsonBuilder.fromJson(str, DynamicDataResponse::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}