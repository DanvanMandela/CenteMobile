package com.elmacentemobile.data.model.ip

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Nullable

data class IpStackModel(
    @field:SerializedName("ip")
    @field:Expose
    @field:Nullable
    val ip: String?,
    @field:SerializedName("hostname")
    @field:Expose
    @field:Nullable
    val hostname: String?,
    @field:SerializedName("type")
    @field:Expose
    @field:Nullable
    val type: String?,
    @field:SerializedName("continent_name")
    @field:Expose
    @field:Nullable
    val continentName: String?,
    @field:SerializedName("region_code")
    @field:Expose
    @field:Nullable
    val regionCode: String?,
    @field:SerializedName("country_name")
    @field:Expose
    @field:Nullable
    val countryName: String?,
    @field:SerializedName("region_name")
    @field:Expose
    @field:Nullable
    val regionName: String?,
    @field:SerializedName("city")
    @field:Expose
    @field:Nullable
    val city: String?,
    @field:SerializedName("latitude")
    @field:Expose
    @field:Nullable
    val latitude: Double?,
    @field:SerializedName("longitude")
    @field:Expose
    @field:Nullable
    val longitude: String?,
    @field:SerializedName("security")
    @field:Expose
    @field:Nullable
    val security: HashMap<String, Any>?,
    @field:SerializedName("connection")
    @field:Expose
    @field:Nullable
    val connection: HashMap<String, Any>?,

    @field:SerializedName("time_zone")
    @field:Expose
    @field:Nullable
    val time_zone: HashMap<String, Any>?,
)