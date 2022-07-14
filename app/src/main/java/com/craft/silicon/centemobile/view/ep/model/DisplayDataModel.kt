package com.craft.silicon.centemobile.view.ep.model

import android.text.TextUtils
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.BlockDisplayContainerBinding
import com.craft.silicon.centemobile.databinding.BlockDisplayItemLayoutBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.controller.DisplayVault
import com.craft.silicon.centemobile.view.ep.data.DisplayContent
import com.google.gson.Gson

@EpoxyModelClass
open class DisplayDataModel : DataBindingEpoxyModel() {
    @EpoxyAttribute
    lateinit var data: DisplayVault

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks


    override fun getDefaultLayout(): Int = R.layout.block_display_container

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockDisplayContainerBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockDisplayContainerBinding) {
        AppLogger.instance.appLog("DISPLAY:controller", Gson().toJson(data))
        val parent = binding.displayLay
        parent.removeAllViews()
        if (!data.children.isNullOrEmpty())
            for (d in data.children!!) {
                for (e in d.entries) {
                    if (!TextUtils.isEmpty(e.value)) {
                        val display =
                            BlockDisplayItemLayoutBinding
                                .inflate(LayoutInflater.from(parent.context))
                        val m = DisplayContent(key = e.key, value = e.value)
                        display.data = m
                        binding.displayLay.addView(display.root)
                    }

                }
            }

    }
}

fun TypedEpoxyController<*>.displayModel(
    vault: DisplayVault,
    appCallbacks: AppCallbacks
) {
    displayData {
        id(vault.parent.controlID)
        data(vault)
        callbacks(appCallbacks)
    }
}