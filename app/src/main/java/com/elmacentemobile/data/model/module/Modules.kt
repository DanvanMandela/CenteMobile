package com.elmacentemobile.data.model.module

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "modules_tbl")
class Modules(

    @field:SerializedName("ParentModule")
    @field:ColumnInfo(name = "parentModule")
    @field:Expose
    var parentModule: String?,

    @field:SerializedName("ModuleID")
    @field:ColumnInfo(name = "moduleID")
    @field:PrimaryKey
    @field:NonNull
    @field:Expose
    var moduleID: String,

    @field:SerializedName("ModuleName")
    @field:ColumnInfo(name = "moduleName")
    @field:Expose
    var moduleName: String?,

    @field:SerializedName("ModuleCategory")
    @field:ColumnInfo(name = "moduleCategory")
    @field:Expose
    var ModuleCategory: String?,

    @field:SerializedName("CustomerType")
    @field:ColumnInfo(name = "customerType")
    @field:Expose
    var customerType: String?,

    @field:SerializedName("ModuleURL")
    @field:ColumnInfo(name = "moduleURL")
    @field:Expose
    var moduleURL: String?,

    @field:SerializedName("Language")
    @field:ColumnInfo(name = "language")
    @field:Expose
    var language: String?,

    @field:SerializedName("ModuleURL2")
    @field:ColumnInfo(name = "moduleURTwo")
    @field:Expose
    var moduleURLTwo: String?,

    @field:SerializedName("MerchantID")
    @field:ColumnInfo(name = "merchantID")
    @field:Expose
    var merchantID: String?,

    @field:SerializedName("isClubbed")
    @field:ColumnInfo(name = "isClubbed")
    @field:Expose
    var isClubbed: Boolean?,

    @field:SerializedName("isChecked")
    @field:ColumnInfo(name = "isChecked")
    @field:Expose
    var isChecked: Boolean?,


    @field:SerializedName("AllowConfirmationPopup")
    @field:ColumnInfo(name = "allowConfirmationPopup")
    @field:Expose
    var allowConfirmationPopup: Boolean?,

    @field:SerializedName("isOtpRequired")
    @field:ColumnInfo(name = "isOtpRequired")
    @field:Expose
    var isOtpRequired: Boolean?,

    @field:SerializedName("BadgeColor")
    @field:ColumnInfo(name = "badgeColor")
    @field:Expose
    var badgeColor: String?,

    @field:SerializedName("BadgeText")
    @field:ColumnInfo(name = "badgeText")
    @field:Expose
    var badgeText: String?,

    @field:SerializedName("ValidationAvailable")
    @field:ColumnInfo(name = "validationAvailable")
    @field:Expose
    var validationAvailable: Boolean?,

    @field:SerializedName("DisplayOrder")
    @field:ColumnInfo(name = "displayOrder")
    @field:Expose
    var displayOrder: String,

    @field:SerializedName("IsDisabled")
    @field:ColumnInfo(name = "isDisabled")
    @field:Expose
    var isDisabled: Boolean,
) : Serializable, Parcelable {

//    @IgnoredOnParcel
//    @field:SerializedName("id")
//    @field:ColumnInfo(name = "id")
//    @field:PrimaryKey
//    @field:NonNull
//    @field:Expose(serialize = false, deserialize = false)
//    var id: String = ""
//
//    fun generateID() {
//        id = "M-${BaseClass.generateAlphaNumericString(10)}"
//    }


    @IgnoredOnParcel
    @Ignore
    @field:SerializedName("notAvailable")
    @field:Expose
    var available: Boolean? = true

    @IgnoredOnParcel
    @Ignore
    @field:SerializedName("message")
    @field:Expose
    var message: String? = "Module disabled"

}

class ModuleDataTypeConverter {


    fun from(data: Modules?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, Modules::class.java)
    }

    fun to(data: String?): Modules? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, Modules::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}