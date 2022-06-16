package com.craft.silicon.centemobile.data.source.remote.callback

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.String


data class ApiResponse(
    @field:SerializedName("response_code")
    @field:Expose
    var responseCode: String,
    @field:SerializedName("response")
    @field:Expose
    var response: String,
    @field:SerializedName("task")
    @field:Expose
    var task: String,
)

data class ResponseData(
    @field:SerializedName("respCode")
    @field:Expose
    var respCode: String?,
    @field:SerializedName("message")
    @field:Expose
    var message: String?,
    @field:SerializedName("token")
    @field:Expose
    var token: String?,
    @field:SerializedName("data")
    @field:Expose
    var data: MutableList<Int>?,
    @field:SerializedName("payload")
    @field:Expose
    var payload: Payload?,
    @field:SerializedName("requestStatus")
    @field:Expose
    var requestStatus: String?

)

data class Payload(
    @field:SerializedName("MobileNumber")
    @field:Expose
    var mobileNumber: String?,
    @field:SerializedName("Device")
    @field:Expose
    var device: String?,
    @field:SerializedName("Ran")
    @field:Expose
    var ran: String?,
    @field:SerializedName("Routes")
    @field:Expose
    var Routes: String?
)

data class RequestData(
    @field:SerializedName("UniqueId")
    @field:Expose
    var uniqueId: String,
    @field:SerializedName("MobileNumber")
    @field:Expose
    var mobileNumber: String,
    @field:SerializedName("Device")
    @field:Expose
    var device: String,
    @field:SerializedName("CodeBase")
    @field:Expose
    var codeBase: String,
    @field:SerializedName("lat")
    @field:Expose
    var lat: String,
    @field:SerializedName("rashi")
    @field:Expose
    var rashi: String,
    @field:SerializedName("long")
    @field:Expose
    var long: String,
    @field:SerializedName("Appname")
    @field:Expose
    var appName: String
)