package com.craft.silicon.centemobile.data.model.dynamic

import android.os.Parcelable
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamicDataResponse(
    @field:SerializedName("Status")
    @field:Expose
    var status: String?,
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,
    @field:SerializedName("FormID")
    @field:Expose
    var formID: String?,
    @field:SerializedName("ResultsData")
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
    var display: MutableList<HashMap<String, String>>?
) : Parcelable
