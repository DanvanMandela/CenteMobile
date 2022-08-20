package com.craft.silicon.centemobile.data.model.converter

import com.craft.silicon.centemobile.data.receiver.Notification
import com.craft.silicon.centemobile.data.receiver.NotificationData
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class NotificationDataConverter {
    fun from(data: NotificationData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, NotificationData::class.java)
    }

    fun to(data: String?): NotificationData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, NotificationData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}


class NotificationConverter {
    fun from(data: Notification?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, Notification::class.java)
    }

    fun to(data: String?): Notification? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data.trim(), Notification::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}