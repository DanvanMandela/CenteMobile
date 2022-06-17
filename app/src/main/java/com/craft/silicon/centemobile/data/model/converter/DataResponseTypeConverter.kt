package com.craft.silicon.centemobile.data.model.converter

import androidx.room.TypeConverter
import com.craft.silicon.centemobile.data.model.DataResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class DataResponseTypeConverter {
    @TypeConverter
    fun from(dataResponse: DataResponse?): String? {
        return if (dataResponse == null) {
            null
        } else gsonBuilder.toJson(dataResponse, DataResponse::class.java)
    }

    @TypeConverter
    fun to(dataResponse: String?): DataResponse? {
        return if (dataResponse == null) {
            null
        } else gsonBuilder.fromJson(dataResponse, DataResponse::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}