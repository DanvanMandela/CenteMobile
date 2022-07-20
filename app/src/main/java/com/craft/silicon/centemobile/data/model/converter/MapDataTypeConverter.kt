package com.craft.silicon.centemobile.data.model.converter

import com.craft.silicon.centemobile.view.fragment.map.MapData
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class MapDataTypeConverter {
    fun from(data: MapData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, MapData::class.java)
    }

    fun to(data: String?): MapData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, MapData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}