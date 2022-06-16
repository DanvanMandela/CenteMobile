package com.craft.silicon.centewallet.util.provider

import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


interface BaseResourceProvider {

    fun getString(@StringRes id: Int): String

    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String

    fun getDrawable(@DrawableRes resId: Int): Drawable

    fun getStringArray(@ArrayRes resId: Int): Array<String>

}