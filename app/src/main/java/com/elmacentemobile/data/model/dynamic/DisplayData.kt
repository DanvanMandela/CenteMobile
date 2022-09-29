package com.elmacentemobile.data.model.dynamic

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DisplayData(
    @field:SerializedName("Date")
    @field:ColumnInfo(name = "date")
    @field:Expose
    var date: String?,
) : Parcelable