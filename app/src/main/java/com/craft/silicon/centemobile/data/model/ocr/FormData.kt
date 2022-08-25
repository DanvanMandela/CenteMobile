package com.craft.silicon.centemobile.data.model.ocr

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Nullable

data class FormDataRequest(
    @field:SerializedName("FormID")
    @field:Expose
    @field:Nullable
    val formID: String?,
    @field:SerializedName("UNIQUEID")
    @field:Expose
    @field:Nullable
    val uniqueID: String?,
    @field:SerializedName("SessionID")
    @field:Expose
    @field:Nullable
    val sessionID: String?,
    @field:SerializedName("MobileNumber")
    @field:Expose
    @field:Nullable
    val mobileNumber: String?,
    @field:SerializedName("IMEI")
    @field:Expose
    @field:Nullable
    val imei: String?,
    @field:SerializedName("CodeBase")
    @field:Expose
    @field:Nullable
    val codeBase: String?,
    @field:SerializedName("DeviceName")
    @field:Expose
    @field:Nullable
    val deviceName: String?,
    @field:SerializedName("LanguageID")
    @field:Expose
    @field:Nullable
    val languageID: String?,
    @field:SerializedName("City")
    @field:Expose
    @field:Nullable
    val city: String?,
    @field:SerializedName("Country")
    @field:Expose
    @field:Nullable
    val country: String?,
    @field:SerializedName("RegisteredCountry")
    @field:Expose
    @field:Nullable
    val registeredCountry: String?,
    @field:SerializedName("NetworkCountry")
    @field:Expose
    @field:Nullable
    val networkCountry: String?,
    @field:SerializedName("CarrierName")
    @field:Expose
    @field:Nullable
    val carrierName: String?,
    @field:SerializedName("RiderLL")
    @field:Expose
    @field:Nullable
    val riderLL: String?,
    @field:SerializedName("LatLong")
    @field:Expose
    @field:Nullable
    val latLong: String?,
    @field:SerializedName("APKVERSION")
    @field:Expose
    @field:Nullable
    val apkVersion: String?,
    @field:SerializedName("SOFTWAREVERSION")
    @field:Expose
    @field:Nullable
    val softwareVersion: String?,
)

