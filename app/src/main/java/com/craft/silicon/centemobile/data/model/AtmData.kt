package com.craft.silicon.centemobile.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "atm_branch_tbl")
data class AtmData(
    @field:SerializedName("Longitude")
    @field:ColumnInfo(name = "lng")
    @field:Expose
    var longitude: Double?,

    @field:SerializedName("Latitude")
    @field:ColumnInfo(name = "lat")
    @field:Expose
    var latitude: Double?,

    @field:SerializedName("Location")
    @field:ColumnInfo(name = "location")
    @field:Expose
    var location: String?,

    @field:SerializedName("Distance")
    @field:ColumnInfo(name = "distance")
    @field:Expose
    var distance: Double?

) : Parcelable {
    @IgnoredOnParcel
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey(autoGenerate = true)
    @field:NonNull
    @field:Expose
    var id: Int? = null

    @IgnoredOnParcel
    @field:ColumnInfo(name = "type")
    @field:Expose
    var isATM: Boolean? = null
}

data class ATMResponse(
    @field:SerializedName("Data")
    @field:Expose
    var data: MutableList<AtmData>?,
    @field:SerializedName("Status")
    @field:Expose
    var status: String?
)

class ATMTypeConverter {
    fun from(data: ATMResponse?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, ATMResponse::class.java)
    }

    fun to(data: String?): ATMResponse? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, ATMResponse::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}

