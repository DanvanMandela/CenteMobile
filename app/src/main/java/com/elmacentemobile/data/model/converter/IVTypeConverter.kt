package com.elmacentemobile.data.model.converter

import android.os.Parcelable
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


class IVTypeConverter {
    @TypeConverter
    fun from(data: IVData?): String? {
        return if (data == null) {
            null
        } else Gson().toJson(data, IVData::class.java)
    }

    @TypeConverter
    fun to(s: String?): IVData? {
        return if (s == null) {
            null
        } else Gson().fromJson(s, IVData::class.java)
    }
}

@Parcelize
data class IVData(
    @field:SerializedName("iv")
    val iv: ByteArray,
    @field:SerializedName("cipherText")
    val cipherText: ByteArray
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as IVData
        if (!iv.contentEquals(other.iv)) return false
        return true
    }

    override fun hashCode(): Int {
        return iv.contentHashCode()
    }
}