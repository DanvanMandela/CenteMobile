package com.craft.silicon.centemobile.data.model.converter

import androidx.room.TypeConverter
import com.craft.silicon.centemobile.data.model.user.ModuleDisable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class DisableModulesConverter {
    @TypeConverter
    fun from(data: String?): List<ModuleDisable?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<ModuleDisable?>?>() {}.type
        return gsonBuilder.fromJson<List<ModuleDisable?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<ModuleDisable?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

}