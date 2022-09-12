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
@Entity(tableName = "carousel_tbl")
data class CarouselData(
    @field:SerializedName("ImageURL")
    @field:ColumnInfo(name = "imageURL")
    @field:Expose
    var imageURL: String?,
    @field:SerializedName("ImageInfoURL")
    @field:ColumnInfo(name = "imageInfoURL")
    @field:Expose
    var imageInfoURL: String?,
    @field:SerializedName("ImageCategory")
    @field:ColumnInfo(name = "imageCategory")
    @field:Expose
    var category: String?,
) : Parcelable {
    @IgnoredOnParcel
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey(autoGenerate = true)
    @field:NonNull
    @field:Expose
    var id: Int? = null
}

data class CarouselResponse(
    @field:SerializedName("Data")
    @field:Expose
    var data: MutableList<CarouselData>?,
    @field:SerializedName("Status")
    @field:Expose
    var status: String?
)

class CarouselConverter {
    fun from(data: CarouselResponse?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, CarouselResponse::class.java)
    }

    fun to(data: String?): CarouselResponse? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, CarouselResponse::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}