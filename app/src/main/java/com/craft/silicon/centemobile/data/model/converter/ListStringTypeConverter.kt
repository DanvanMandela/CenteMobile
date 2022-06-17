package com.craft.silicon.centemobile.data.model.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class ListStringTypeConverter {

    @TypeConverter
    fun from(data: String?): List<String?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<String?>?>() {}.type
        return gsonBuilder.fromJson<List<String?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<String?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gsonBuilder.toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}