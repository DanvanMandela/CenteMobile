package com.craft.silicon.centemobile.view.dialog

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class DialogData(
    @StringRes
    val title: Int,
    val subTitle: String,
    @DrawableRes
    val avatar: Int
) : Parcelable


