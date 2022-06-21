package com.craft.silicon.centemobile.data.model.converter

import com.craft.silicon.centemobile.data.source.remote.callback.ResponseDetails
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class ResponseTypeConverter {
    fun from(data: ResponseDetails?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, ResponseDetails::class.java)
    }

    fun to(data: String?): ResponseDetails? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, ResponseDetails::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}