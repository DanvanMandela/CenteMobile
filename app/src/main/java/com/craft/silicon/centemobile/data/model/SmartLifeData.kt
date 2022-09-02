package com.craft.silicon.centemobile.data.model

import android.os.Parcelable
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
class SmartLifeData(
    @field:SerializedName("accountStatus")
    @field:Expose
    var accountStatus: String?,
    @field:SerializedName("birthplaceDistrict")
    @field:Expose
    var birthplaceDistrict: String?,
    @field:SerializedName("birthplaceVillage")
    @field:Expose
    var birthplaceVillage: String?,
    @field:SerializedName("customerCreated")
    @field:Expose
    var customerCreated: String?,
    @field:SerializedName("employername")
    @field:Expose
    var employerName: String?,
    @field:SerializedName("fathersurname")
    @field:Expose
    var fatherSurname: String?,
    @field:SerializedName("gender")
    @field:Expose
    var gender: String?,
    @field:SerializedName("homeDistrictKey")
    @field:Expose
    var homeDistrictKey: String?,
    @field:SerializedName("mothersurname")
    @field:Expose
    var motherSurname: String?,
    @field:SerializedName("name")
    @field:Expose
    var name: String?,
    @field:SerializedName("nssfNumber")
    @field:Expose
    var nssfNumber: String?,
    @field:SerializedName("othername")
    @field:Expose
    var othername: String?,
    @field:SerializedName("phoneNumber")
    @field:Expose
    var phoneNumber: String?,
    @field:SerializedName("physicaladdress")
    @field:Expose
    var physicalAddress: String?,
    @field:SerializedName("postaladdress")
    @field:Expose
    var postalAddress: String?,
    @field:SerializedName("surname")
    @field:Expose
    var surname: String?
) : Parcelable

@Parcelize
data class SmartLifeResponse(
    @field:SerializedName("Status")
    @field:Expose
    var status: String?,
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,
    @field:SerializedName("returnCode")
    @field:Expose
    var returnCode: String?,
    @field:SerializedName("returnMessage")
    @field:Expose
    var returnMessage: String?,
    @field:SerializedName("returnObject")
    @field:Expose
    var returnObject: SmartLifeData?
) : Parcelable

class SmartLifeResponseTypeConverter {
    @TypeConverter
    fun from(data: SmartLifeResponse?): String? {
        return if (data == null) {
            null
        } else Gson().toJson(data, SmartLifeResponse::class.java)
    }

    @TypeConverter
    fun to(s: String?): SmartLifeResponse? {
        return if (s == null) {
            null
        } else Gson().fromJson(s, SmartLifeResponse::class.java)
    }
}