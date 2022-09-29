package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.data.model.static_data.StaticDataDetails
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class StaticDataDetailTypeConverter {

    @TypeConverter
    fun from(data: String?): List<StaticDataDetails?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<StaticDataDetails?>?>() {}.type
        return gsonBuilder.fromJson<List<StaticDataDetails?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<StaticDataDetails?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}