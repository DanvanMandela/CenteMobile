package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.data.model.user.AlertServices
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class AlertsTypeConverter {
    @TypeConverter
    fun from(data: String?): List<AlertServices?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<AlertServices?>?>() {}.type
        return gsonBuilder.fromJson<List<AlertServices?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<AlertServices?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}