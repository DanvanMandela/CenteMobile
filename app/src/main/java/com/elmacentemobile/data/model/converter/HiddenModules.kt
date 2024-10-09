package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.data.model.user.ModuleHide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class HiddenModules {
    @TypeConverter
    fun from(data: String?): List<ModuleHide?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<ModuleHide?>?>() {}.type
        return gsonBuilder.fromJson<List<ModuleHide?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<ModuleHide?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}