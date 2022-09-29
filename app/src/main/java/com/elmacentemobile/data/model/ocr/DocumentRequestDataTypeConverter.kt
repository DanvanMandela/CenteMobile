package com.elmacentemobile.data.model.ocr

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class DocumentRequestDataTypeConverter {
    @TypeConverter
    fun from(data: DocumentRequestData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, DocumentRequestData::class.java)
    }

    @TypeConverter
    fun to(data: String?): DocumentRequestData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, DocumentRequestData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}