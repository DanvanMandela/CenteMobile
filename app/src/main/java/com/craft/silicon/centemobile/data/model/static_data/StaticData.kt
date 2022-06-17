package com.craft.silicon.centemobile.data.model.static_data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "static_data_tbl")
data class StaticData(
    @field:SerializedName("Status")
    @field:ColumnInfo(name = "status")
    @field:Expose
    var status: String,
    @field:SerializedName("Message")
    @field:ColumnInfo(name = "message")
    @field:Expose
    var message: String,

    @field:SerializedName("Version")
    @field:ColumnInfo(name = "version")
    @field:Expose
    var version: String,

    @field:SerializedName("UserCode")
    @field:ColumnInfo(name = "userCode")
    @field:Expose
    var userCode: List<UserData>
) : Parcelable
