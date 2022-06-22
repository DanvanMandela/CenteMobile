package com.craft.silicon.centemobile.data.model.control

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "form_control_tb")
data class FormControl(
    @field:SerializedName("ModuleID")
    @field:ColumnInfo(name = "moduleID")
    @field:Expose
    var moduleID: String?,

    @field:SerializedName("ControlID")
    @field:ColumnInfo(name = "controlID")
    @field:PrimaryKey
    @field:NonNull
    @field:Expose
    var controlID: String,

    @field:SerializedName("ControlText")
    @field:ColumnInfo(name = "controlText")
    @field:Expose
    var controlText: String?,

    @field:SerializedName("ControlType")
    @field:ColumnInfo(name = "controlType")
    @field:Expose
    var controlType: String?,

    @field:SerializedName("DisplayOrder")
    @field:ColumnInfo(name = "displayOrder")
    @field:Expose
    var displayOrder: Double,

    @field:SerializedName("MinValue")
    @field:ColumnInfo(name = "minValue")
    @field:Expose
    var minValue: String?,

    @field:SerializedName("MaxValue")
    @field:ColumnInfo(name = "maxValue")
    @field:Expose
    var maxValue: String?,

    @field:SerializedName("ControlFormat")
    @field:ColumnInfo(name = "ControlFormat")
    @field:Expose
    var controlFormat: String?,

    @field:SerializedName("DisplayControl")
    @field:ColumnInfo(name = "displayControl")
    @field:Expose
    var displayControl: String?,

    @field:SerializedName("ImageURL")
    @field:ColumnInfo(name = "imageURL")
    @field:Expose
    var imageURL: String?,

    @field:SerializedName("ServiceParamID")
    @field:ColumnInfo(name = "serviceParamID")
    @field:Expose
    var serviceParamID: String?,

    @field:SerializedName("ContainerID")
    @field:ColumnInfo(name = "containerID")
    @field:Expose
    var containerID: String?,

    @field:SerializedName("LinkedToRadioButton")
    @field:ColumnInfo(name = "linkedToRadioButton")
    @field:Expose
    var linkedToRadioButton: String?,

    @field:SerializedName("ActionID")
    @field:ColumnInfo(name = "actionID")
    @field:Expose
    var actionID: String?,

    @field:SerializedName("ControlValue")
    @field:ColumnInfo(name = "controlValue")
    @field:Expose
    var controlValue: String?,

    @field:SerializedName("BGColor")
    @field:ColumnInfo(name = "bGColor")
    @field:Expose
    var bGColor: String?,

    @field:SerializedName("TextColor")
    @field:ColumnInfo(name = "textColor")
    @field:Expose
    var textColor: String?,

    @field:SerializedName("OwnLinkValue")
    @field:ColumnInfo(name = "ownLinkValue")
    @field:Expose
    var ownLinkValue: String?,

    @field:SerializedName("ActionType")
    @field:ColumnInfo(name = "actionType")
    @field:Expose
    var actionType: String?,

    @field:SerializedName("DataSourceID")
    @field:ColumnInfo(name = "dataSourceID")
    @field:Expose
    var DataSourceID: String?,

    @field:SerializedName("LinkedToModule")
    @field:ColumnInfo(name = "linkedToModule")
    @field:Expose
    var linkedToModule: String?,

    @field:SerializedName("isMandatory")
    @field:ColumnInfo(name = "isMandatory")
    @field:Expose
    var isMandatory: Boolean?,

    @field:SerializedName("isEncrypted")
    @field:ColumnInfo(name = "isEncrypted")
    @field:Expose
    var isEncrypted: Boolean?
) : Serializable, Parcelable