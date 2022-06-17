package com.craft.silicon.centemobile.data.model.input

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class FormInputData(
    @SerializedName("moduleId")
    var moduleId: String,
    @SerializedName("actionType")
    var actiontype: String,
    @SerializedName("inputData")
    var inputData: @RawValue MutableList<InputData>
) : Parcelable