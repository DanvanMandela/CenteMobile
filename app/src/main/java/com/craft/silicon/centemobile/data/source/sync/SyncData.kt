package com.craft.silicon.centemobile.data.source.sync


import android.os.Parcelable
import androidx.room.TypeConverter
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.abs


@Parcelize
data class SyncData(
    @field:SerializedName("message")
    @field:Expose
    var message: String? = null,
    @field:SerializedName("work")
    @field:Expose
    var work: Int = 0,
    @field:SerializedName("complete")
    @field:Expose
    var complete: Boolean = false
) : AppData(), Parcelable {
    @IgnoredOnParcel
    @field:SerializedName("percentage")
    @field:Expose
    var percentage: Double = calculatePercentage()

    private fun calculatePercentage(): Double {
        return abs((work.toDouble().div(SyncEnum.TOTAL.num).times(100)))
    }
}


enum class SyncEnum(val num: Int) {
    TOTAL(8)
}

class SyncDataTypeConverter {
    @TypeConverter
    fun from(syncData: SyncData?): String? {
        return if (syncData == null) {
            null
        } else gsonBuilder.toJson(syncData, SyncData::class.java)
    }

    @TypeConverter
    fun to(data: String?): SyncData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, SyncData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}
