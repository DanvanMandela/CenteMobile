package com.elmacentemobile.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.util.*


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

class CarouselDataConverter {
    @TypeConverter
    fun from(data: String?): List<CarouselData>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<CarouselData>?>() {}.type
        return gsonBuilder.fromJson<List<CarouselData>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<CarouselData>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}

val carouselList = mutableListOf(
    CarouselData(
        imageInfoURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRS-_1LAysgW0Vr9VTu5P8lpU08uqlE5eeR7w&usqp=CAU",
        imageURL = "https://mir-s3-cdn-cf.behance.net/project_modules/fs/2bbcfa99737217.5ef9be3dbb9a9.jpg",
        category = "BEHANCE"
    ),

    CarouselData(
        imageInfoURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRS-_1LAysgW0Vr9VTu5P8lpU08uqlE5eeR7w&usqp=CAU",
        imageURL = "https://cdnb.artstation.com/p/assets/images/images/011/153/149/large/huy-hoang-banner-2.jpg?1528124250",
        category = "NIKE AIR"
    )
)