package com.elmacentemobile.data.model

import android.os.Parcelable
import androidx.room.TypeConverter
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.view.ep.data.AppData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.util.*

@Parcelize
data class StandingOrder(
    @field:SerializedName("DebitAccountID")
    val debitAccountID: String?,
    @field:SerializedName("LastExecutionDate")
    val lastExecutionDate: String?,
    @field:SerializedName("StoppedBy")
    val stoppedBy: String?,
    @field:SerializedName("CreatedBy")
    val createdBy: String?,
    @field:SerializedName("Narration")
    val narration: String?,
    @field:SerializedName("FrequencyID")
    val frequencyID: String?,
    @field:SerializedName("Amount")
    val amount: String?,
    @field:SerializedName("StoppedOn")
    val stoppedOn: String?,
    @field:SerializedName("CustomerID")
    val customerID: String?,
    @field:SerializedName("BeneficiaryAccountID")
    val beneficiaryAccountID: String?,
    @field:SerializedName("BeneficiaryBankID")
    val beneficiaryBankID: String?,
    @field:SerializedName("SupervisedBy")
    val supervisedBy: String?,
    @field:SerializedName("ServiceID")
    val serviceID: String?,
    @field:SerializedName("SOID")
    val SOID: String?,
    @field:SerializedName("StoppedReason")
    val stoppedReason: String?,
    @field:SerializedName("RegularExecutionDay")
    val regularExecutionDay: String?,
    @field:SerializedName("SupervisedOn")
    val supervisedOn: String?,
    @field:SerializedName("RequestData")
    val requestData: String?,
    @field:SerializedName("Country")
    val country: String?,
    @field:SerializedName("CreatedOn")
    val createdOn: String?,
    @field:SerializedName("BankID")
    val bankID: String?,
    @field:SerializedName("NoOfExecutions")
    val noOfExecutions: String?,
    @field:SerializedName("BeneficiaryBranchID")
    val beneficiaryBranchID: String?,
    @field:SerializedName("EffectiveDate")
    val effectiveDate: String?
) : Parcelable

@Parcelize
data class StandingOrderList(
    @field:SerializedName("list")
    @field:Expose
    val list: MutableList<StandingOrder>?,
    @field:SerializedName("module")
    @field:Expose
    val module: Modules?,
    @field:SerializedName("form")
    @field:Expose
    val formControl: FormControl?
) : AppData(), Parcelable

class StandingOrderTypeConverter {
    @TypeConverter
    fun from(data: String?): List<StandingOrder>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<StandingOrder>?>() {}.type
        return gsonBuilder.fromJson<List<StandingOrder>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<StandingOrder?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}

@Parcelize
data class StandingResponse(
    @field:SerializedName("Status")
    @field:Expose
    var status: String?,
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,
    @field:SerializedName("FormID")
    @field:Expose
    var formID: String?,
    @field:SerializedName("Data")
    @field:Expose
    var data: MutableList<StandingOrder>?
) : Parcelable


class StandingResponseTypeConverter {
    @TypeConverter
    fun from(data: StandingResponse?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, StandingResponse::class.java)
    }

    @TypeConverter
    fun to(str: String?): StandingResponse? {
        return if (str == null) {
            null
        } else gsonBuilder.fromJson(str, StandingResponse::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}