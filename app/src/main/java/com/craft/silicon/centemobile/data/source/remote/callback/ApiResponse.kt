package com.craft.silicon.centemobile.data.source.remote.callback

import com.craft.silicon.centemobile.data.model.user.Accounts
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlin.String


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
    var requestStatus: String?,
    @field:SerializedName("Response")
    @field:Expose
    var response: String?
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
    @field:SerializedName("codeBase")
    @field:Expose
    var codeBase: String,
    @field:SerializedName("lat")
    @field:Expose
    var lat: String,
    @field:SerializedName("rashi")
    @field:Expose
    var rashi: String,
    @field:SerializedName("longit")
    @field:Expose
    var long: String,
    @field:SerializedName("appName")
    @field:Expose
    var appName: String
)

data class PayloadData(
    @field:SerializedName("UniqueId")
    @field:Expose
    var uniqueId: String,
    @field:SerializedName("Data")
    @field:Expose
    var data: String
)

data class DynamicResponse(
    @field:SerializedName("Response")
    @field:Expose
    var response: String?,
)

data class ResponseDetails(
    @field:SerializedName("Status")
    @field:Expose
    var status: String,
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,
    @field:SerializedName("FormID")
    @field:Expose
    var FormID: String?,
    @field:SerializedName("CustomerID")
    @field:Expose
    var customerID: String?,
)



