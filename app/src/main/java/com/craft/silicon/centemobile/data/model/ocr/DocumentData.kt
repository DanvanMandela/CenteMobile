package com.craft.silicon.centemobile.data.model.ocr

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Nullable

data class DocumentRequestData(
    @field:SerializedName("Country")
    @field:Expose
    val country: String?,
    @field:SerializedName("ProcessID")
    @field:Expose
    val processID: String?
)

data class DocumentResponseData(
    @field:SerializedName("Status")
    @field:Expose
    val status: String?,
    @field:SerializedName("Message")
    @field:Expose
    val message: String?,
    @field:SerializedName("fields")
    @field:Expose
    @field:Nullable
    val fields: MutableList<UserDetail>?
)

data class FaceRequestData(
    @field:SerializedName("APIKey")
    @field:Expose
    @field:Nullable
    val apiKey: String?,
    @field:SerializedName("ImageURL")
    @field:Expose
    @field:Nullable
    val imageURL: String?
)

data class DataHolder(
    @field:SerializedName("text")
    @field:Expose
    @field:Nullable
    val text: String?,
    @field:SerializedName("confidence")
    @field:Expose
    @field:Nullable
    val confidence: String?
)

data class UserDetail(
    @field:SerializedName("Nationality")
    @field:Expose
    @field:Nullable
    val nationality: MutableList<DataHolder>?,
    @field:SerializedName("Sex")
    @field:Expose
    @field:Nullable
    val sex: MutableList<DataHolder>?,
    @field:SerializedName("Surname")
    @field:Expose
    @field:Nullable
    val surname: MutableList<DataHolder>?,
    @field:SerializedName("CardNumber")
    @field:Expose
    @field:Nullable
    val cardNumber: MutableList<DataHolder>?,
    @field:SerializedName("DateOfExpiry")
    @field:Expose
    @field:Nullable
    val dateExpire: MutableList<DataHolder>?,
    @field:SerializedName("NIN")
    @field:Expose
    @field:Nullable
    val nin: MutableList<DataHolder>?,
    @field:SerializedName("DateOfBirth")
    @field:Expose
    @field:Nullable
    val dob: MutableList<DataHolder>?,
    @field:SerializedName("Name")
    @field:Expose
    @field:Nullable
    val name: MutableList<DataHolder>?,
)
