package com.craft.silicon.centemobile.data.model.dynamic

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class TransactionData(
    @field:ColumnInfo(name = "type")
    @field:SerializedName("TrxType")
    @field:Expose
    var type: String?,

    @field:ColumnInfo(name = "serviceName")
    @field:SerializedName("ServiceName")
    @field:Expose
    var serviceName: String?,

    @field:ColumnInfo(name = "date")
    @field:SerializedName("Date")
    @field:Expose
    var date: String?,

    @field:ColumnInfo(name = "amount")
    @field:SerializedName("Amount")
    @field:Expose
    var amount: String?,

    @field:ColumnInfo(name = "status")
    @field:SerializedName("Status")
    @field:Expose
    var status: String?,

    @field:ColumnInfo(name = "ref")
    @field:SerializedName("RefNo")
    @field:Expose
    var refNo: String?,

    @field:ColumnInfo(name = "message")
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,

    @field:ColumnInfo(name = "bankAccountID")
    @field:SerializedName("BankAccountID")
    @field:Expose
    var bankAccountID: String?,

    @field:ColumnInfo(name = "merchantRefNo")
    @field:SerializedName("MerchantRefNo")
    @field:Expose
    var merchantRefNo: String?,

    @field:ColumnInfo(name = "serviceAccountID")
    @field:SerializedName("ServiceAccountID")
    @field:Expose
    var ServiceAccountID: String?
) : Parcelable

@Parcelize
data class TransactionResponse(
    @field:SerializedName("Status")
    @field:Expose
    var status: String?,
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,

    @field:SerializedName("Data")
    @field:Expose
    var data: MutableList<TransactionData>?

) : Parcelable

class TransactionResponseResponseConverter {
    @TypeConverter
    fun from(data: TransactionResponse?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, TransactionResponse::class.java)
    }

    @TypeConverter
    fun to(str: String?): TransactionResponse? {
        return if (str == null) {
            null
        } else gsonBuilder.fromJson(str, TransactionResponse::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}