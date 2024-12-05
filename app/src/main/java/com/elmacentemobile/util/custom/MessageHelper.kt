package com.elmacentemobile.util.custom

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun String.cleanMessage(): String {
    val gson = Gson()
    val listType = object : TypeToken<List<String>>() {}.type
    val infoList: List<String> = gson.fromJson(this, listType)
    val randomMessage = infoList.random()
    return randomMessage.ifBlank { "" }
}