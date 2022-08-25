package com.craft.silicon.centemobile.data.model.ocr

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class ImageResponseTypeConverter {
    @TypeConverter
    fun from(data: ImageResponseData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, ImageResponseData::class.java)
    }

    @TypeConverter
    fun to(data: String?): ImageResponseData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, ImageResponseData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}