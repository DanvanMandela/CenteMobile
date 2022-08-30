package com.craft.silicon.centemobile.view.ep.model

import android.text.TextUtils
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.databinding.BlockTextInputDisabledLayoutBinding
import com.craft.silicon.centemobile.util.NumberTextWatcherForThousand
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.custom.CurrencyInput
import com.craft.silicon.centemobile.view.binding.setDefaultValue

@EpoxyModelClass
open class TextInputModelDisabled : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var form: FormControl

    @EpoxyAttribute
    lateinit var storage: StorageDataSource

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_text_input_disabled_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockTextInputDisabledLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockTextInputDisabledLayoutBinding) {
        binding.data = form
        binding.callback = callbacks
        binding.storage = storage
    }
}



fun TypedEpoxyController<*>.inputDisabledModel(
    form: FormControl?,
    storage: StorageDataSource?,
    callbacks: AppCallbacks?
) {
    textInputModelDisabled {
        id(form?.controlID)
        form(form)
        storage(storage)
        callbacks(callbacks)
    }
}