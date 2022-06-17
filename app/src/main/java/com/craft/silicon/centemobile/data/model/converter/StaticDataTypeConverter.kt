package com.craft.silicon.centemobile.data.model.converter

import androidx.room.TypeConverter
import com.craft.silicon.centemobile.data.model.static_data.StaticData
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class StaticDataTypeConverter {

    @TypeConverter
    fun from(staticData: StaticData?): String? {
        return if (staticData == null) {
            null
        } else gsonBuilder.toJson(staticData, StaticData::class.java)
    }

    @TypeConverter
    fun to(staticData: String?): StaticData? {
        return if (staticData == null) {
            null
        } else gsonBuilder.fromJson(staticData, StaticData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}