package com.elmacentemobile.data.model.ocr

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Nullable

data class ImageResponseData(
    @field:SerializedName("Status")
    @field:Expose
    val status: String?,
    @field:SerializedName("Message")
    @field:Expose
    val message: String?,
    @field:SerializedName("ImageURL")
    @field:Expose
    @field:Nullable
    val imageUrl: String?
)

data class ImageRequestData(
    @field:SerializedName("APIKey")
    @field:Expose
    val apiKey: String?,
    @field:SerializedName("ImageID")
    @field:Expose
    val imageID: String?,
    @field:SerializedName("ImageURL")
    @field:Expose
    val imageURL: String?,
    @field:SerializedName("Country")
    @field:Expose
    val country: String?,
)

data class ImageRequestResponseData(
    @field:SerializedName("Status")
    @field:Expose
    val status: String?,
    @field:SerializedName("Message")
    @field:Expose
    val message: String?,
    @field:SerializedName("requestID")
    @field:Expose
    val requestID: String?,
    @field:SerializedName("processID")
    @field:Expose
    val processID: String?,
)

