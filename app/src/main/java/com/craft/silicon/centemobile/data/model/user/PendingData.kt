package com.craft.silicon.centemobile.data.model.user

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.util.*

@Parcelize
data class PendingTrxDisplay(
    @field:SerializedName("Name")
    @field:Expose
    var name: String?,
    @field:SerializedName("Comments")
    @field:Expose
    var comments: String?,
    @field:SerializedName("Trx Type")
    @field:Expose
    var trxType: String?,
    @field:SerializedName("Send to")
    @field:Expose
    var sendTo: String?,
    @field:SerializedName("Amount")
    @field:Expose
    var Amount: String?,
    @field:SerializedName("pendingUniqueID")
    @field:Expose
    var pendingUniqueID: String?

) : Parcelable

@Parcelize
data class PendingTrxPayload(
    @field:SerializedName("ModuleID")
    @field:Expose
    var moduleID: String?,
    @field:SerializedName("MerchantID")
    @field:Expose
    var merchantID: String?,
    @field:SerializedName("ACCOUNTID")
    @field:Expose
    var accountID: String?,
    @field:SerializedName("AMOUNT")
    @field:Expose
    var amount: String?,
    @field:SerializedName("PendingUniqueID")
    @field:Expose
    var pendingUniqueID: String?

) : Parcelable

@Parcelize
data class PendingTrxFormControls(
    @field:SerializedName("ModuleID")
    @field:Expose
    var moduleID: String?,
    @field:SerializedName("ControlID")
    @field:Expose
    var controlID: String?,
    @field:SerializedName("ControlText")
    @field:Expose
    var controlText: String?,
    @field:SerializedName("ControlType")
    @field:Expose
    var controlType: String?,
    @field:SerializedName("ControlFormat")
    @field:Expose
    var controlFormat: String?,
    @field:SerializedName("DisplayControl")
    @field:Expose
    var displayControl: String?,
    @field:SerializedName("ServiceParamID")
    @field:Expose
    var serviceParamID: String?,
    @field:SerializedName("ActionType")
    @field:Expose
    var actionType: String?,
    @field:SerializedName("DisplayOrder")
    @field:Expose
    var displayOrder: String?,
    @field:SerializedName("IsMandatory")
    @field:Expose
    var isMandatory: Boolean,
    @field:SerializedName("IsEncrypted")
    @field:Expose
    var isEncrypted: Boolean
) : Parcelable

@Parcelize
data class PendingTrxActionControls(
    @field:SerializedName("ModuleID")
    @field:Expose
    var moduleID: String?,
    @field:SerializedName("ActionID")
    @field:Expose
    var actionID: String?,
    @field:SerializedName("ActionType")
    @field:Expose
    var actionType: String?,
    @field:SerializedName("ServiceParamIDs")
    @field:Expose
    var serviceParamIDs: String?,
    @field:SerializedName("WebHeader")
    @field:Expose
    var webHeader: String?
) : Parcelable


@Entity(tableName = "pending_table")
@Parcelize
data class PendingTransaction(


    @field:SerializedName("display")
    @field:TypeConverters(DisplayHashTypeConverter::class)
    @field:ColumnInfo(name = "display")
    @field:Expose
    var display: DisplayHash,

    @field:SerializedName("payload")
    @field:ColumnInfo(name = "payload")
    @field:TypeConverters(DisplayHashTypeConverter::class)
    @field:Expose
    var payload: DisplayHash,

    @field:SerializedName("form")
    @field:Expose
    @field:ColumnInfo(name = "form")
    @field:TypeConverters(PendingTrxFormControlsConverter::class)
    var form: MutableList<PendingTrxFormControls>,

    @field:SerializedName("action")
    @field:TypeConverters(PendingTrxActionControlsControlsConverter::class)
    @field:Expose
    @field:ColumnInfo(name = "action")
    var action: MutableList<PendingTrxActionControls>
) : Parcelable {

    @IgnoredOnParcel
    @field:SerializedName("id")
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey(autoGenerate = true)
    @field:NonNull
    var id: Int = 0
}

@Parcelize
data class PendingTransactionList(
    @field:SerializedName("list")
    @field:Expose
    val list: MutableList<PendingTransaction>?
) : AppData(), Parcelable


@Parcelize
data class DisplayHash(
    @field:SerializedName("list")
    @field:Expose
    val list: HashMap<String, String>
) : Parcelable, AppData()

class PendingTrxFormControlsConverter {
    @TypeConverter
    fun from(data: String?): MutableList<PendingTrxFormControls?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<MutableList<PendingTrxFormControls?>?>() {}.type
        return gsonBuilder.fromJson<MutableList<PendingTrxFormControls?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: MutableList<PendingTrxFormControls?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gsonBuilder.toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}

class PendingTrxActionControlsControlsConverter {
    @TypeConverter
    fun from(data: String?): MutableList<PendingTrxActionControls?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<MutableList<PendingTrxActionControls?>?>() {}.type
        return gsonBuilder.fromJson<MutableList<PendingTrxActionControls?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: MutableList<PendingTrxActionControls?>?): String? {
        if (someObjects == null) {
            return null
        }
        return gsonBuilder.toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}

class DisplayHashTypeConverter {
    @TypeConverter
    fun from(data: DisplayHash?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, DisplayHash::class.java)
    }

    @TypeConverter
    fun to(data: String?): DisplayHash? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, DisplayHash::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}