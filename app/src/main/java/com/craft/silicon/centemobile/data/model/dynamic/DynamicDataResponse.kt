package com.craft.silicon.centemobile.data.model.dynamic

import android.os.Parcelable
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.craft.silicon.centemobile.data.source.remote.callback.ReceiptDetails
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import javax.annotation.Nullable

@Parcelize
data class DynamicDataResponse(
    @field:SerializedName("Status")
    @field:Expose
    var status: String?,
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,
    @field:SerializedName("NextFormSequence")
    @field:Expose
    @field:Nullable
    var next: Int?,
    @field:SerializedName("FormID")
    @field:Expose
    var formID: String?,
    @field:SerializedName("Data")
    @field:Expose
    var resultsData: MutableList<ResultsData>?,
    @field:SerializedName("Beneficiary")
    @field:Expose
    var beneficiary: MutableList<Beneficiary>?,
    @field:SerializedName("FormFields")
    @field:Expose
    var formFields: MutableList<FormField>?,
    @field:SerializedName("Display")
    @field:Expose
    var display: MutableList<HashMap<String, String>>?,

    @field:SerializedName("ReceiptDetails")
    @field:Expose
    var receipt: MutableList<ReceiptDetails>?,
    @field:SerializedName("Notifications")
    @field:Expose
    var notifications: MutableList<Notifications>?,

    @field:SerializedName("AccountStatement")
    @field:Expose
    var accountStatement: MutableList<HashMap<String, String>>?

) : Parcelable


@Parcelize
data class DynamicAPIResponse(
    @field:SerializedName("Status")
    @field:Expose
    var status: String?,
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,
    @field:SerializedName("NextFormSequence")
    @field:Expose
    @field:Nullable
    var next: Int?,
    @field:SerializedName("FormID")
    @field:Expose
    var formID: String?,
    @field:SerializedName("Data")
    @field:Expose
    var resultsData: MutableList<FormField>?,
    @field:SerializedName("FormFields")
    @field:Expose
    var formField: MutableList<FormField>?,
    @field:SerializedName("Beneficiary")
    @field:Expose
    var beneficiary: MutableList<Beneficiary>?,
    @field:SerializedName("Display")
    @field:Expose
    var display: MutableList<HashMap<String, String>>?,
    @field:SerializedName("ReceiptDetails")
    @field:Expose
    var receipt: MutableList<ReceiptDetails>?,
    @field:SerializedName("Notifications")
    @field:Expose
    var notifications: MutableList<Notifications>?,
    @field:SerializedName("ResultsData")
    @field:Expose
    var rData: MutableList<FormField>?,
    @field:SerializedName("AccountStatement")
    @field:Expose
    var accountStatement: MutableList<HashMap<String, String>>?

) : Parcelable

@Parcelize
data class Notifications(
    @field:SerializedName("NotifyTo")
    @field:Expose
    var notifyTo: String?,
    @field:SerializedName("NotifyType")
    @field:Expose
    var notifyType: String?,
    @field:SerializedName("NotifyID")
    @field:Expose
    var notifyID: String?,
    @field:SerializedName("NotifyText")
    @field:Expose
    var notifyText: String?,
) : Parcelable


@Parcelize
data class GlobalResponse(
    @field:SerializedName("Status")
    @field:Expose
    var status: String?,
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,
    @field:SerializedName("FormID")
    @field:Expose
    var formID: String?,
    @field:SerializedName("Data")
    @field:Expose
    var data: MutableList<HashMap<String, String>>?
) : Parcelable







