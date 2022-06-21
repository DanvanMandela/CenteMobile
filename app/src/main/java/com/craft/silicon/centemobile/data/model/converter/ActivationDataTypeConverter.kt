package com.craft.silicon.centemobile.data.model.converter

import com.craft.silicon.centemobile.data.model.user.ActivationData
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class ActivationDataTypeConverter {
    fun from(data: ActivationData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, ActivationData::class.java)
    }

    fun to(data: String?): ActivationData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, ActivationData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}