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
import com.craft.silicon.centemobile.databinding.BlockMainDisplayItemLayoutBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass.generateAlphaNumericString
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.DisplayContent
import com.google.gson.Gson

@EpoxyModelClass
open class MainDisplayModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var data: HashMap<String, String>

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks


    override fun getDefaultLayout(): Int = R.layout.block_display_container

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockDisplayContainerBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockDisplayContainerBinding) {
        AppLogger.instance.appLog("DISPLAY:MAIN:controller", Gson().toJson(data))
        val parent = binding.displayLay
        parent.removeAllViews()
        for (e in data.entries) {
            if (!TextUtils.isEmpty(e.value)) {
                val display =
                    BlockMainDisplayItemLayoutBinding
                        .inflate(LayoutInflater.from(parent.context))
                val m = DisplayContent(key = e.key, value = e.value)
                display.data = m
                binding.displayLay.addView(display.root)
            }

        }

    }
}

fun TypedEpoxyController<*>.mainDisplayLay(
    data: HashMap<String, String>,
    appCallbacks: AppCallbacks
) {
    mainDisplay {
        id(generateAlphaNumericString(4))
        data(data)
        callbacks(appCallbacks)
    }
}