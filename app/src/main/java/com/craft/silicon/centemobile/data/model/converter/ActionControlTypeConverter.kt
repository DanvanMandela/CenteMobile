package com.craft.silicon.centemobile.data.model.converter

import androidx.room.TypeConverter
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class ActionControlTypeConverter {

    @TypeConverter
    fun from(data: String?): List<ActionControls?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<ActionControls?>?>() {}.type
        return gsonBuilder.fromJson<List<ActionControls?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<ActionControls?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}