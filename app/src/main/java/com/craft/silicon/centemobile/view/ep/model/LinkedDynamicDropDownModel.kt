package com.craft.silicon.centemobile.view.ep.model

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.databinding.*
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.controller.LinkedVault
import com.google.android.material.textfield.TextInputEditText


@EpoxyModelClass
open class LinkedDynamicDropDownModel : DataBindingEpoxyModel() {
    @EpoxyAttribute
    lateinit var data: LinkedVault

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_dymanic_drop_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockDymanicDropLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockDymanicDropLayoutBinding) {
        binding.childContainer.removeAllViews()
        val param = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        binding.data = data.container
        val editList = mutableListOf<TextInputEditText>()
        param.setMargins(
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_24dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_24dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_24dp),
            0
        )
        for (s in data.children) {
            when (BaseClass.nonCaps(s.controlType)) {
                BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> {

                    when (BaseClass.nonCaps(s.controlFormat)) {
                        BaseClass.nonCaps(ControlFormatEnum.AMOUNT.type) -> {
                            if (!s.displayControl.isNullOrBlank()) {
                                if (s.displayControl == "true") {
                                    val text = BlockDisabledAmountTextInputLayoutBinding
                                        .inflate(LayoutInflater.from(binding.root.context))
                                    text.child.tag = s.controlID
                                    text.data = s
                                    text.callback = callbacks
                                    binding.childContainer.addView(text.root)
                                    editList.add(text.child)
                                } else {
                                    val text = BlockAmountTextInputLayoutBinding
                                        .inflate(LayoutInflater.from(binding.root.context))
                                    text.child.tag = s.controlID
                                    text.data = s
                                    text.callback = callbacks
                                    binding.childContainer.addView(text.root)
                                    editList.add(text.child)
                                }
                            } else {
                                val text = BlockAmountTextInputLayoutBinding
                                    .inflate(LayoutInflater.from(binding.root.context))
                                text.child.tag = s.controlID
                                text.data = s
                                text.callback = callbacks
                                binding.childContainer.addView(text.root)
                                editList.add(text.child)
                            }
                        }
                        else -> {
                            if (!s.displayControl.isNullOrBlank()) {
                                if (s.displayControl == "true") {
                                    val text = BlockTextInputDisabledLayoutBinding
                                        .inflate(LayoutInflater.from(binding.root.context))
                                    text.child.tag = s.controlID
                                    text.data = s
                                    text.callback = callbacks
                                    binding.childContainer.addView(text.root)
                                    editList.add(text.child)
                                } else {
                                    val text = BlockTextInputLayoutBinding
                                        .inflate(LayoutInflater.from(binding.root.context))
                                    text.child.tag = s.controlID
                                    text.data = s
                                    text.callback = callbacks
                                    binding.childContainer.addView(text.root)
                                    editList.add(text.child)

                                }
                            } else {
                                val text = BlockTextInputLayoutBinding
                                    .inflate(LayoutInflater.from(binding.root.context))
                                text.child.tag = s.controlID
                                text.data = s
                                text.callback = callbacks
                                binding.childContainer.addView(text.root)
                                editList.add(text.child)
                            }
                        }
                    }
//                    val text = BlockTextInputLayoutBinding
//                        .inflate(LayoutInflater.from(binding.root.context))
//                    text.child.tag = s.controlID
//                    text.data = s
//                    text.callback = callbacks
//                    text.outlinedTextField.layoutParams = param
//                    binding.childContainer.addView(text.root)
//                    editList.add(text.child)
                }
                BaseClass.nonCaps(ControlTypeEnum.HIDDEN.type) -> {
                    val text = BlockHiddenInputLayoutBinding
                        .inflate(LayoutInflater.from(binding.root.context))
                    text.child.tag = s.controlID
                    text.data = s
                    text.callback = callbacks
                    text.outlinedTextField.layoutParams = param
                    binding.childContainer.addView(text.root)
                    editList.add(text.child)
                }
            }
        }


        if (editList.isNotEmpty())
            callbacks.onDynamicDropDown(binding.autoEdit, data.container, editList)
    }

}


fun TypedEpoxyController<*>.linkedDynamicDropDownLayout(
    vault: LinkedVault,
    appCallbacks: AppCallbacks
) {
    linkedDynamicDropDown {
        id(vault.container.controlID)
        data(vault)
        callbacks(appCallbacks)
    }
}