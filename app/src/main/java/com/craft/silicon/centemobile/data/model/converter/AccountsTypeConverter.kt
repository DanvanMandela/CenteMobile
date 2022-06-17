package com.craft.silicon.centemobile.data.model.converter


import androidx.room.TypeConverter
import com.example.waroftheworlds.data.entity.user.Accounts
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class AccountsTypeConverter {

    @TypeConverter
    fun from(data: String?): List<Accounts?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<Accounts?>?>() {}.type
        return gsonBuilder.fromJson<List<Accounts?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<Accounts?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}