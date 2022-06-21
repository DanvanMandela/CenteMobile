package com.craft.silicon.centemobile.data.model.converter

import androidx.room.TypeConverter
import com.craft.silicon.centemobile.data.model.user.FrequentModules
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class FrequentModulesTypeConverter {
    @TypeConverter
    fun from(data: String?): List<FrequentModules?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<FrequentModules?>?>() {}.type
        return gsonBuilder.fromJson<List<FrequentModules?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<FrequentModules?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}