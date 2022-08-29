package com.craft.silicon.centemobile.view.ep.model

import android.text.TextUtils
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.databinding.BlockTextInputNumericLayoutBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks

@EpoxyModelClass
open class TextNumericInputModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var form: FormControl

    @EpoxyAttribute
    lateinit var storage: StorageDataSource

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_text_input_numeric_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockTextInputNumericLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockTextInputNumericLayoutBinding) {
        binding.data = form
        binding.callback = callbacks
        binding.storage = storage
        if (form.maxValue != null) {
            if (!TextUtils.isEmpty(form.maxValue)) {
                BaseClass.setMaxLength(binding.child, form.maxValue!!.toInt())
            }
        }

        if (form.displayControl != null) {
            if (!TextUtils.isEmpty(form.displayControl)) {
                if (form.displayControl == "true")
                    binding.child.isEnabled = false
            }
        }
    }
}

fun TypedEpoxyController<*>.inputNumericModel(
    form: FormControl?,
    storage: StorageDataSource?,
    callbacks: AppCallbacks?
) {
    textNumericInput {
        id(form?.controlID)
        form(form)
        storage(storage)
        callbacks(callbacks)
    }
}