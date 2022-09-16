package com.craft.silicon.centemobile.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.net.MalformedURLException
import java.net.URL
import javax.annotation.Nullable

data class DeviceData(
    @field:SerializedName("token")
    @field:Expose
    var token: String,
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
    @field:SerializedName("card")
    @field:Expose
    @Nullable
    val card: String?,
    @field:SerializedName("balance")
    @field:Expose
    @Nullable
    val balance: String?,
    @field:SerializedName("staticdata")
    @field:Expose
    @Nullable
    val staticData: String?,
    @field:SerializedName("run")
    @field:Expose
    @Nullable
    var run: String?,
    @field:SerializedName("device")
    @field:Expose
    @Nullable
    var device: String?
)

open class SpiltURL(str: String) {
    lateinit var base: String
    lateinit var path: String

    init {
        try {
            val url = URL(str)
            base = "${url.protocol}://${url.host}/"
            path = url.path.substring(1)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }
}

open class GetBaseURL(str: String) {
    lateinit var base: String
    lateinit var path: String

    init {
        try {
            val url = URL(str)
            base = "${url.protocol}://${url.host}/"
            path = url.path.substring(1)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

}