package com.craft.silicon.centemobile.data.model.input

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InputData(
    @SerializedName("key")
    var key: String,
    @SerializedName("value")
    var value: String
) : Parcelable
