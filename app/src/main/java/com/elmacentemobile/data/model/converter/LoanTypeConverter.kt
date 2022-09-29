package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.data.model.LoanData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class LoanTypeConverter {
    @TypeConverter
    fun from(data: String?): List<LoanData?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<LoanData?>?>() {}.type
        return gsonBuilder.fromJson<List<LoanData?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<LoanData?>?): String? {
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