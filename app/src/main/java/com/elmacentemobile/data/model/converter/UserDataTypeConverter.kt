package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.data.model.user.LoginUserData
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class UserDataTypeConverter {
    @TypeConverter
    fun from(data: LoginUserData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, LoginUserData::class.java)
    }

    @TypeConverter
    fun to(data: String?): LoginUserData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, LoginUserData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}