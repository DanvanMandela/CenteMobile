package com.elmacentemobile.view.ep.model

import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.R
import com.elmacentemobile.databinding.BlockDatePickerLayoutBinding
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.controller.ChildVault

@EpoxyModelClass
open class DatePickerModel : DataBindingEpoxyModel() {
    @EpoxyAttribute
    lateinit var data: ChildVault

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks


    override fun getDefaultLayout(): Int = R.layout.block_date_picker_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockDatePickerLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockDatePickerLayoutBinding) {
        binding.callback = callbacks
        binding.data = data.container
        binding.autoInput.setEndIconOnClickListener {
            callbacks.onDateSelect(binding.autoEdit, data.container)
        }
        binding.autoEdit.setOnClickListener {
            callbacks.onDateSelect(binding.autoEdit, data.container)
        }
    }
}

fun TypedEpoxyController<*>.dateSelect(
    vault: ChildVault,
    appCallbacks: AppCallbacks
) {
    datePicker {
        id(vault.container.controlID)
        data(vault)
        callbacks(appCallbacks)
    }
}
