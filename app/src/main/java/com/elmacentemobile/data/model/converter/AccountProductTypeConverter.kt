package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.data.model.static_data.OnlineAccountProduct
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class AccountProductTypeConverter {
    @TypeConverter
    fun from(data: String?): List<OnlineAccountProduct?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<OnlineAccountProduct?>?>() {}.type
        return gsonBuilder.fromJson<List<OnlineAccountProduct?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<OnlineAccountProduct?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}