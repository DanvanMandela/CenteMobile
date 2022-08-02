package com.craft.silicon.centemobile.data.model.dynamic

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultsData(
    @field:SerializedName("Date")
    @field:Expose
    var date: String?,
    @field:SerializedName("Beneficiary")
    @field:Expose
    var beneficiary: String?,
    @field:SerializedName("Amount")
    @field:Expose
    var amount: String?,
    @field:SerializedName("RefToken")
    @field:Expose
    var refToken: String?
) : Parcelable

