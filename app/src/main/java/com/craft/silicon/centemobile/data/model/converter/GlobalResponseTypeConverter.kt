package com.craft.silicon.centemobile.data.model.converter

import androidx.room.TypeConverter
import com.craft.silicon.centemobile.data.model.dynamic.GlobalResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GlobalResponseTypeConverter {
    @TypeConverter
    fun from(data: GlobalResponse?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, GlobalResponse::class.java)
    }

    @TypeConverter
    fun to(str: String?): GlobalResponse? {
        return if (str == null) {
            null
        } else gsonBuilder.fromJson(str, GlobalResponse::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}