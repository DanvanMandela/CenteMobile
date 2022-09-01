package com.craft.silicon.centemobile.data.model.control

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "form_control_tb")
data class FormControl(

    @field:SerializedName("ModuleID")
    @field:ColumnInfo(name = "moduleID")
    @field:Expose
    var moduleID: String? = null,

    @field:SerializedName("RowID")
    @field:ColumnInfo(name = "RowID")
    @field:PrimaryKey
    @NonNull
    @field:Expose
    var rowID: String = "0",

    @field:SerializedName("FormID")
    @field:ColumnInfo(name = "formID")
    @field:Expose
    var formID: String? = null,

    @field:SerializedName("ControlID")
    @field:ColumnInfo(name = "controlID")
    @field:Expose
    var controlID: String? = null,

    @field:SerializedName("ControlText")
    @field:ColumnInfo(name = "controlText")
    @field:Expose
    var controlText: String? = null,

    @field:SerializedName("ControlType")
    @field:ColumnInfo(name = "controlType")
    @field:Expose
    var controlType: String? = null,

    @field:SerializedName("DisplayOrder")
    @field:ColumnInfo(name = "displayOrder")
    @field:Expose
    var displayOrder: Double? = null,

    @field:SerializedName("MinValue")
    @field:ColumnInfo(name = "minValue")
    @field:Expose
    var minValue: String? = null,

    @field:SerializedName("MaxValue")
    @field:ColumnInfo(name = "maxValue")
    @field:Expose
    var maxValue: String? = null,

    @field:SerializedName("ControlFormat")
    @field:ColumnInfo(name = "ControlFormat")
    @field:Expose
    var controlFormat: String? = null,

    @field:SerializedName("DisplayControl")
    @field:ColumnInfo(name = "displayControl")
    @field:Expose
    var displayControl: String? = null,

    @field:SerializedName("ImageURL")
    @field:ColumnInfo(name = "imageURL")
    @field:Expose
    var imageURL: String? = null,

    @field:SerializedName("ServiceParamID")
    @field:ColumnInfo(name = "serviceParamID")
    @field:Expose
    var serviceParamID: String? = null,

    @field:SerializedName("ContainerID")
    @field:ColumnInfo(name = "containerID")
    @field:Expose
    var containerID: String? = null,

    @field:SerializedName("LinkedToRadioButton")
    @field:ColumnInfo(name = "linkedToRadioButton")
    @field:Expose
    var linkedToRadioButton: String? = null,

    @field:SerializedName("ActionID")
    @field:ColumnInfo(name = "actionID")
    @field:Expose
    var actionID: String? = null,

    @field:SerializedName("ControlValue")
    @field:ColumnInfo(name = "controlValue")
    @field:Expose
    var controlValue: String? = null,

    @field:SerializedName("BGColor")
    @field:ColumnInfo(name = "bGColor")
    @field:Expose
    var bGColor: String? = null,

    @field:SerializedName("TextColor")
    @field:ColumnInfo(name = "textColor")
    @field:Expose
    var textColor: String? = null,

    @field:SerializedName("OwnLinkValue")
    @field:ColumnInfo(name = "ownLinkValue")
    @field:Expose
    var ownLinkValue: String? = null,

    @field:SerializedName("ActionType")
    @field:ColumnInfo(name = "actionType")
    @field:Expose
    var actionType: String? = null,

    @field:SerializedName("DataSourceID")
    @field:ColumnInfo(name = "dataSourceID")
    @field:Expose
    var dataSourceID: String? = null,

    @field:SerializedName("LinkedToModule")
    @field:ColumnInfo(name = "linkedToModule")
    @field:Expose
    var linkedToModule: String? = null,

    @field:SerializedName("LinkedToControl")
    @field:ColumnInfo(name = "LinkedToControl")
    @field:Expose
    var linkedToControl: String? = null,

    @field:SerializedName("IsMandatory")
    @field:ColumnInfo(name = "isMandatory")
    @field:Expose
    var isMandatory: Boolean,

    @field:SerializedName("IsEncrypted")
    @field:ColumnInfo(name = "isEncrypted")
    @field:Expose
    var isEncrypted: Boolean,

    @field:SerializedName("FormSequence")
    @field:ColumnInfo(name = "formSequence")
    @field:Expose
    var formSequence: String? = null,

    @field:SerializedName("PreviousFormID")
    @field:ColumnInfo(name = "previousFormID")
    @field:Expose
    var previousFormID: String? = null,

    @field:SerializedName("NextFormID")
    @field:ColumnInfo(name = "NextFormID")
    @field:Expose
    var nextFormID: String? = null
) : Serializable, Parcelable {

    @IgnoredOnParcel
    @field:SerializedName("checkMe")
    @field:Expose
    @field:Ignore
    var isChecked: Boolean? = null

}

enum class FormNavigation {
    VALIDATE,
    PAYMENT,
    FINGERPRINT
}