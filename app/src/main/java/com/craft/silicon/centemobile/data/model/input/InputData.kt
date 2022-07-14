package com.craft.silicon.centemobile.data.model.input

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class InputData(
    @SerializedName("name")
    var name: String?,
    @SerializedName("key")
    var key: String?,
    @SerializedName("value")
    var value: String?,
    @SerializedName("encrypted")
    val encrypted: Boolean,
    @SerializedName("mandatory")
    val mandatory: Boolean
) : Parcelable
