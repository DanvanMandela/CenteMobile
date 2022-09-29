package com.elmacentemobile.data.model.input

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class FormInputData(
    @SerializedName("moduleId")
    var moduleId: String,
    @SerializedName("actionType")
    var actiontype: String,
    @SerializedName("inputData")
    var inputData: @RawValue MutableList<InputData>
) : Parcelable