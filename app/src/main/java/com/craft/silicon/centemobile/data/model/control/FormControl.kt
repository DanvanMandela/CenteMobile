package com.craft.silicon.centemobile.data.model.control

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class FormControl(
    var ModuleID: String?,
    var ControlID: String?,
    var ControlText: String?,
    var ControlType: String?,
    var DisplayOrder: Double,
    var MinValue: String?,
    var MaxValue: String?,
    var ControlFormat: String?,
    var DisplayControl: String?,
    var ImageURL: String?,
    var ServiceParamID: String?,
    var ContainerID: String?,
    var LinkedToRadioButton: String?,
    var ActionID: String?,
    var ControlValue: String?,
    var BGColor: String?,
    var TextColor: String?,
    var OwnLinkValue: String?,
    var ActionType: String?,
    var DataSourceID: String?,
    var LinkedToModule: String?,
    var isMandatory: Boolean?,
    var isEncrypted: Boolean?
) : Serializable, Parcelable