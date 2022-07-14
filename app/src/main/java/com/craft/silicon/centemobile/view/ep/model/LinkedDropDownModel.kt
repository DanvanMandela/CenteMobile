package com.craft.silicon.centemobile.view.ep.model

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.databinding.BlockDropDownLayoutBinding
import com.craft.silicon.centemobile.databinding.BlockLinkedDropDownLayoutBinding
import com.craft.silicon.centemobile.databinding.BlockTextInputLayoutBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.DropDownView
import com.craft.silicon.centemobile.view.binding.linkedDropDown
import com.craft.silicon.centemobile.view.binding.linkedToInput
import com.craft.silicon.centemobile.view.ep.controller.LinkedVault
import com.google.android.material.textfield.TextInputEditText

@EpoxyModelClass
open class LinkedDropDownModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var data: LinkedVault

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_linked_drop_down_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockLinkedDropDownLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(parent: BlockLinkedDropDownLayoutBinding) {
        parent.data = data.container
        parent.childContainer.removeAllViews()
        for (s in data.children) {
            when (BaseClass.nonCaps(s.controlType)) {
                BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> {
                    val binding =
                        BlockTextInputLayoutBinding.inflate(
                            LayoutInflater
                                .from(parent.root.context)
                        )
                    binding.data = s
                    binding.callback = callbacks
                    parent.childContainer.addView(binding.root)
                    setLinkedToInput(parent, binding.child)
                }
                BaseClass.nonCaps(ControlTypeEnum.DROPDOWN.type) -> {
                    val binding =
                        BlockDropDownLayoutBinding.inflate(
                            LayoutInflater
                                .from(parent.root.context)
                        )
                    setLinkedToDropDown(binding, parent, s)
                }
            }
        }

    }

    private fun setLinkedToDropDown(
        binding: BlockDropDownLayoutBinding,
        parent: BlockLinkedDropDownLayoutBinding,
        s: FormControl
    ) {
        binding.data = s
        parent.childContainer.addView(binding.root)
        parent.autoEdit.linkedDropDown(
            callbacks = callbacks,
            formControl = data.container,
            storage = data.mainData.storage,
            view = DropDownView(
                dropDown = binding.autoEdit,
                data = s
            )
        )
    }

    private fun setLinkedToInput(
        parent: BlockLinkedDropDownLayoutBinding,
        s: TextInputEditText
    ) {
        parent.autoEdit.linkedToInput(
            callbacks = callbacks,
            formControl = data.container,
            storage = data.mainData.storage,
            modules = data.mainData.forms.module,
            view = s
        )
    }
}

fun TypedEpoxyController<*>.linkedDropDownLayout(
    vault: LinkedVault,
    appCallbacks: AppCallbacks
) {
    linkedDropDown {
        id(vault.container.controlID)
        data(vault)
        callbacks(appCallbacks)
    }
}