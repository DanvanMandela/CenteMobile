package com.elmacentemobile.data.model.static_data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "static_data_details_tbl")
data class StaticDataDetails(
    @field:SerializedName("ID")
    @field:ColumnInfo(name = "id")
    @field:Expose
    var id: String,
    @field:SerializedName("SubCodeID")
    @field:ColumnInfo(name = "subCodeID")
    @field:PrimaryKey
    @field:Expose
    var subCodeID: String,
    @field:SerializedName("Description")
    @field:ColumnInfo(name = "description")
    @field:Expose
    var description: String?,
    @field:SerializedName("RelationID")
    @field:ColumnInfo(name = "relationID")
    @field:Expose
    var relationID: String?,
    @field:SerializedName("ExtraField")
    @field:ColumnInfo(name = "extraField")
    @field:Expose
    var extraField: String?,
    @field:SerializedName("DisplayOrder")
    @field:ColumnInfo(name = "displayOrder")
    @field:Expose
    var displayOrder: Int?,
    @field:SerializedName("IsDefault")
    @field:ColumnInfo(name = "IsDefault")
    @field:Expose
    var isDefault: Boolean?
) : Parcelable {
    override fun toString(): String {
        return description!!
    }
}
