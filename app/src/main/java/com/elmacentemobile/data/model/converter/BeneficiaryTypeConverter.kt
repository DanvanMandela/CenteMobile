package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.data.model.user.Beneficiary
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class BeneficiaryTypeConverter {
    @TypeConverter
    fun from(data: String?): List<Beneficiary?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<Beneficiary?>?>() {}.type
        return gsonBuilder.fromJson<List<Beneficiary?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<Beneficiary?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}