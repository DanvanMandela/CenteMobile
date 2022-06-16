package com.craft.silicon.centemobile.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Nullable

data class DeviceData(
    @field:SerializedName("token")
    @field:Expose
    @Nullable
    var token: String?,
    @field:SerializedName("version")
    @field:Expose
    @Nullable
    val version: String?,
    @field:SerializedName("auth")
    @field:Expose
    @Nullable
    val auth: String?,
    @field:SerializedName("account")
    @field:Expose
    @Nullable
    val account: String?,
    @field:SerializedName("purchase")
    @field:Expose
    @Nullable
    val purchase: String?,
    @field:SerializedName("validate")
    @field:Expose
    @Nullable
    val validate: String?,
    @field:SerializedName("other")
    @field:Expose
    @Nullable
    val other: String?,
    @field:SerializedName("balance")
    @field:Expose
    @Nullable
    val balance: String?
)

open class SpiltURL(url: String) {
    var base: String
    var path: String

    init {
        val split = url.split("/").toMutableList()
        path = split.removeAt(split.size.minus(1))
        base = split.joinToString("/").plus("/")
    }
}