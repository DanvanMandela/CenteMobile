package com.elmacentemobile.data.model.dynamic

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FormField(
    @field:SerializedName("ControlID")
    @field:Expose
    var controlID: String?,
    @field:SerializedName("ControlValue")
    @field:Expose
    var controlValue: String?
) : Parcelable
