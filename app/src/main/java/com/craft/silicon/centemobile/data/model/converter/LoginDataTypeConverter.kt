package com.craft.silicon.centemobile.data.model.converter

import com.craft.silicon.centemobile.data.model.user.LoginUserData
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class LoginDataTypeConverter {
    fun from(data: LoginUserData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, LoginUserData::class.java)
    }

    fun to(data: String?): LoginUserData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, LoginUserData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}