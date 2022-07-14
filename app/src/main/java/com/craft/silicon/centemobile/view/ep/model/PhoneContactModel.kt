package com.craft.silicon.centemobile.view.ep.model

import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.BlockContactInputLayoutBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.controller.ChildVault

@EpoxyModelClass
open class PhoneContactModel : DataBindingEpoxyModel() {
    @EpoxyAttribute
    lateinit var data: ChildVault

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks


    override fun getDefaultLayout(): Int = R.layout.block_contact_input_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockContactInputLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockContactInputLayoutBinding) {
        binding.callback = callbacks
        binding.data = data.container

        binding.autoInput.setEndIconOnClickListener {
            callbacks.onContactSelect(binding.autoEdit)
        }


    }

}


fun TypedEpoxyController<*>.phoneContacts(
    vault: ChildVault,
    appCallbacks: AppCallbacks
) {
    phoneContact {
        id(vault.container.controlID)
        data(vault)
        callbacks(appCallbacks)
    }
}