package com.elmacentemobile.view.dialog

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DialogData(
    @field:SerializedName("title")
    @StringRes
    val title: Int?,
    @field:SerializedName("subTitle")
    val subTitle: String?,
    @field:SerializedName("avatar")
    @DrawableRes
    val avatar: Int?
) : Parcelable


@Parcelize
data class MainDialogData(
    @field:SerializedName("title")
    val title: String?,
    @field:SerializedName("message")
    val message: String?
) : Parcelable


