package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.data.model.DataResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class WidgetDataTypeConverter {

    @TypeConverter
    fun from(data: String?): List<DataResponse?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<DataResponse?>?>() {}.type
        return gsonBuilder.fromJson<List<DataResponse?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<DataResponse?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}