package com.craft.silicon.centemobile.data.model.converter

import androidx.room.TypeConverter
import com.craft.silicon.centemobile.data.model.dynamic.DynamicAPIResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class DynamicAPIResponseConverter {
    @TypeConverter
    fun from(data: DynamicAPIResponse?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, DynamicAPIResponse::class.java)
    }

    @TypeConverter
    fun to(str: String?): DynamicAPIResponse? {
        return if (str == null) {
            null
        } else gsonBuilder.fromJson(str, DynamicAPIResponse::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}