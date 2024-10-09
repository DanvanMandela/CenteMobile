package com.elmacentemobile.data.model.converter

import androidx.room.TypeConverter
import com.elmacentemobile.view.ep.data.GroupForm
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class GroupFormTypeConverter {

    @TypeConverter
    fun from(data: GroupForm?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, GroupForm::class.java)
    }

    @TypeConverter
    fun to(str: String?): GroupForm? {
        return if (str == null) {
            null
        } else gsonBuilder.fromJson(str, GroupForm::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}