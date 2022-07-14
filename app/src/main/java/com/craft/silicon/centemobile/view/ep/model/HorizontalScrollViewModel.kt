package com.craft.silicon.centemobile.view.ep.model

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.databinding.BlockChipLayoutBinding
import com.craft.silicon.centemobile.databinding.BlockHorizontalScrollLayoutBinding
import com.craft.silicon.centemobile.databinding.BlockLinkedInputLayoutBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.controller.LinkedVault
import com.google.android.material.chip.Chip

@EpoxyModelClass
open class HorizontalScrollViewModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var data: LinkedVault

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_horizontal_scroll_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockHorizontalScrollLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(parent: BlockHorizontalScrollLayoutBinding) {
        val child =
            data.mainData.forms.form?.filter { a -> a.linkedToControl == data.container.controlID }


        val group = parent.group
        group.removeAllViews()
        data.children.forEachIndexed { index, formControl ->
            val inflater = LayoutInflater.from(group.context)
            val chipBinding = BlockChipLayoutBinding
                .inflate(inflater, null, false)
            chipBinding.root.id = index
            group.addView(chipBinding.root)
            chipBinding.data = formControl

            val chip = chipBinding.root as Chip


            chip.setOnClickListener {
                setLinkedChild(child, parent.childContainer, formControl)
            }

            if (formControl == data.children.first()) {
                chip.isChecked = true
                setLinkedChild(child, parent.childContainer, formControl)
            }
        }

    }

    private fun setLinkedChild(
        child: List<FormControl>?,
        childContainer: LinearLayout,
        formControl: FormControl
    ) {
        childContainer.removeAllViews()
        if (child!!.isNotEmpty()) {
            for (s in child) {
                when (BaseClass.nonCaps(s.controlType)) {
                    BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> {
                        val binding =
                            BlockLinkedInputLayoutBinding.inflate(
                                LayoutInflater
                                    .from(childContainer.context)
                            )
                        binding.data = s
                        binding.child.tag = s.controlID
                        binding.callback = callbacks
                        binding.value = formControl.controlText
                        childContainer.addView(binding.root)
                    }
                }

            }
        }
    }

}

fun TypedEpoxyController<*>.horizontalContainer(
    vault: LinkedVault,
    appCallbacks: AppCallbacks
) {
    horizontalScrollView {
        id(vault.container.controlID)
        data(vault)
        callbacks(appCallbacks)
    }
}
