package com.elmacentemobile.data.model

import android.os.Parcelable
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
class SmartLifeData(
    @field:SerializedName("AccountStatus")
    @field:Expose
    var accountStatus: String?,
    @field:SerializedName("DateOfBirth")
    @field:Expose
    var dob: String?,
    @field:SerializedName("BirthPlaceDistrict")
    @field:Expose
    var birthplaceDistrict: String?,
    @field:SerializedName("BirthPlaceVillage")
    @field:Expose
    var birthplaceVillage: String?,
    @field:SerializedName("CustomerCreated")
    @field:Expose
    var customerCreated: String?,
    @field:SerializedName("EmployerName")
    @field:Expose
    var employerName: String?,
    @field:SerializedName("FatherSurname")
    @field:Expose
    var fatherSurname: String?,
    @field:SerializedName("Gender")
    @field:Expose
    var gender: String?,
    @field:SerializedName("MaritalStatus")
    @field:Expose
    var maritalStatus: String?,
    @field:SerializedName("HomeDistrictKey")
    @field:Expose
    var homeDistrictKey: String?,
    @field:SerializedName("MotherSurname")
    @field:Expose
    var motherSurname: String?,
    @field:SerializedName("Name")
    @field:Expose
    var name: String?,
    @field:SerializedName("NSSFNumber")
    @field:Expose
    var number: String?,
    @field:SerializedName("NationalID")
    @field:Expose
    var nationalID: String?,

    @field:SerializedName("OtherName")
    @field:Expose
    var othername: String?,
    @field:SerializedName("PhoneNumber")
    @field:Expose
    var phoneNumber: String?,
    @field:SerializedName("PhysicalAddress")
    @field:Expose
    var physicalAddress: String?,
    @field:SerializedName("PostalAddress")
    @field:Expose
    var postalAddress: String?,
    @field:SerializedName("Surname")
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
    @field:SerializedName("ExtraData")
    @field:Expose
    var returnObject: String?
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

class SmartLifeConverter {
    @TypeConverter
    fun from(data: SmartLifeData?): String? {
        return if (data == null) {
            null
        } else Gson().toJson(data, SmartLifeData::class.java)
    }

    @TypeConverter
    fun to(s: String?): SmartLifeData? {
        return if (s == null) {
            null
        } else Gson().fromJson(s, SmartLifeData::class.java)
    }
}