package com.craft.silicon.centemobile.data.model.action

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.craft.silicon.centemobile.util.BaseClass
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "action_control_tb")
data class ActionControls(


    @field:SerializedName("ModuleID")
    @field:ColumnInfo(name = "moduleID")
    @field:Expose
    var moduleID: String?,


    @field:SerializedName("FormID")
    @field:ColumnInfo(name = "formID")
    @field:Expose
    var formID: String?,

    @field:SerializedName("ControlID")
    @field:ColumnInfo(name = "controlID")
    @field:Expose
    var controlID: String?,

    @field:SerializedName("ActionID")
    @field:ColumnInfo(name = "actionID")
    @field:Expose
    var actionID: String?,

    @field:SerializedName("ActionType")
    @field:ColumnInfo(name = "actionType")
    @field:Expose
    var actionType: String?,

    @field:SerializedName("ServiceParamIDs")
    @field:ColumnInfo(name = "serviceParamIDs")
    @field:Expose
    var serviceParamIDs: String?,

    @field:SerializedName("NextModuleID")
    @field:ColumnInfo(name = "nextModuleID")
    @field:Expose
    var nextModuleID: String?,

    @field:SerializedName("DisplayFormID")
    @field:ColumnInfo(name = "displayFormID")
    @field:Expose
    var displayFormID: String?,

    @field:SerializedName("ActionCommand")
    @field:ColumnInfo(name = "ActionCommand")
    @field:Expose
    var ActionCommand: String?,

    @field:SerializedName("ConfirmationModuleID")
    @field:ColumnInfo(name = "confirmationModuleID")
    @field:Expose
    var confirmationModuleID: String?,

    @field:SerializedName("MerchantID")
    @field:ColumnInfo(name = "merchantID")
    @field:Expose
    var merchantID: String?,

    @field:SerializedName("WebHeader")
    @field:ColumnInfo(name = "webHeader")
    @field:Expose
    var webHeader: String?
) : Serializable, Parcelable {

    @IgnoredOnParcel
    @field:SerializedName("id")
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    @field:NonNull
    @field:Expose(serialize = false, deserialize = false)
    var id: String = ""

    fun generateID() {
        id = "A-${BaseClass.generateAlphaNumericString(10)}"
    }
}