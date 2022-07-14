package com.craft.silicon.centemobile.data.model.converter

import androidx.room.TypeConverter
import com.craft.silicon.centemobile.data.model.InsuranceData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class InsuranceTypeConverter {

    @TypeConverter
    fun from(data: String?): List<InsuranceData?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<InsuranceData?>?>() {}.type
        return gsonBuilder.fromJson<List<InsuranceData?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<InsuranceData?>?): String? {
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