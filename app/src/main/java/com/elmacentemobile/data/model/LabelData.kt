package com.elmacentemobile.data.model

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LabelData(
    @field:SerializedName("ID")
    @field:Expose
    val id: String?,
    @field:SerializedName("Question")
    @field:Expose
    val question: String?
) : Parcelable

class LabelDataTypeConverter {

    fun from(data: LabelData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, LabelData::class.java)
    }

    fun to(data: String?): LabelData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, LabelData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}