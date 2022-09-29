package com.elmacentemobile.data.model.ocr

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class ImageRequestDataTypeConverter {

    @TypeConverter
    fun from(data: ImageRequestData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, ImageRequestData::class.java)
    }

    @TypeConverter
    fun to(data: String?): ImageRequestData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, ImageRequestData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}