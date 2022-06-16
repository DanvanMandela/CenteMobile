package com.craft.silicon.centemobile.util.provider

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.craft.silicon.centewallet.util.provider.BaseResourceProvider
import com.google.android.gms.common.internal.Preconditions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class ResourceProvider @Inject constructor(@ApplicationContext context: Context) :
    BaseResourceProvider {
    private var context: Context = Preconditions.checkNotNull(context, "context cannot be null")

    override fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String {
        return context.getString(resId, *formatArgs)
    }

    override fun getDrawable(resId: Int): Drawable {
        return ContextCompat.getDrawable(context, resId)!!
    }

    override fun getStringArray(resId: Int): Array<String> {
        return context.resources.getStringArray(resId)
    }


}