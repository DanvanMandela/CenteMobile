package com.craft.silicon.centemobile.data.model.dynamic

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultsData(
    @field:SerializedName("Date")
    @field:ColumnInfo(name = "date")
    @field:Expose
    var date: String?,
    @field:SerializedName("Beneficiary")
    @field:ColumnInfo(name = "beneficiary")
    @field:Expose
    var beneficiary: String?,
    @field:SerializedName("Amount")
    @field:ColumnInfo(name = "amount")
    @field:Expose
    var amount: String?,
    @field:SerializedName("RefToken")
    @field:ColumnInfo(name = "refToken")
    @field:Expose
    var refToken: String?
) : Parcelable

