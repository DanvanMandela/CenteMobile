package com.craft.silicon.centemobile.view.ep.model

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.databinding.*
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.GroupForm

@EpoxyModelClass
open class MainLayoutModel : DataBindingEpoxyModel() {
    @EpoxyAttribute
    lateinit var data: GroupForm

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks


    override fun getDefaultLayout(): Int = R.layout.block_main_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockMainLayoutBinding)?.apply {
            addChildren(this.mainParent)
        }
    }

    private fun addChildren(mainParent: LinearLayout) {
        mainParent.removeAllViews()
        for (d in data.form) {
            when (BaseClass.nonCaps(d.controlType)) {
                BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> setTextLayout(mainParent, d)
                BaseClass.nonCaps(ControlTypeEnum.BUTTON.type) -> setButtonLayout(mainParent, d)
                BaseClass.nonCaps(ControlTypeEnum.DROPDOWN.type) -> setDropDownLayout(mainParent, d)
                BaseClass.nonCaps(ControlTypeEnum.CONTAINER.type) -> setContainer(mainParent, d)
            }
        }
        callbacks.onChildren(mainParent)
    }

    private fun setContainer(mainParent: LinearLayout, d: FormControl) {
        when (BaseClass.nonCaps(d.controlFormat)) {
            BaseClass.nonCaps(ControlFormatEnum.RADIO_GROUPS.type) -> setRadioGroupLayout(
                mainParent, d
            )
        }
    }

    private fun setRadioGroupLayout(mainParent: LinearLayout, d: FormControl) {
        val inflater = LayoutInflater.from(mainParent.context)
        val child = BlockRadionGroupLayoutBinding.inflate(inflater, null, false)
        child.data = data
        child.callback = callbacks
        mainParent.addView(child.root)
    }

    private fun setDropDownLayout(mainParent: LinearLayout, d: FormControl) {
        val inflater = LayoutInflater.from(mainParent.context)
        val binding = BlockDropDownLayoutBinding.inflate(inflater, mainParent, false)
        binding.data = d
        binding.callback = callbacks
        binding.form = data
        mainParent.addView(binding.root)
    }


    private fun setButtonLayout(mainParent: LinearLayout, d: FormControl) {
        val inflater = LayoutInflater.from(mainParent.context)
        val binding = BlockButtonLayoutBinding.inflate(inflater, mainParent, false)
        binding.data = d
        binding.callback = callbacks
        binding.module = data.module
        mainParent.addView(binding.root)

    }

    private fun setTextLayout(mainParent: LinearLayout, d: FormControl) {
        val inflater = LayoutInflater.from(mainParent.context)
        val binding = BlockTextInputLayoutBinding.inflate(inflater, mainParent, false)
        binding.data = d
        binding.callback = callbacks
        binding.module = data.module
        mainParent.addView(binding.root)
    }
}

fun TypedEpoxyController<*>.addMainLayout(f: GroupForm, appCallbacks: AppCallbacks) {
    mainLayout {
        id("mainLay")
        data(f)
        callbacks(appCallbacks)
    }
}