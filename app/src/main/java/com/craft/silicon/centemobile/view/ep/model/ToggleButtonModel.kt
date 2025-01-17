package com.craft.silicon.centemobile.view.ep.model

import android.view.LayoutInflater
import android.view.View.VISIBLE
import androidx.compose.runtime.mutableStateListOf
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.*
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.databinding.BlockToggleButtonLayoutBinding
import com.craft.silicon.centemobile.databinding.BlockToggleGroupContainerBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.setChildren
import com.craft.silicon.centemobile.view.ep.controller.LinkedVault
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.google.android.material.button.MaterialButton

@EpoxyModelClass
open class ToggleButtonModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var data: LinkedVault

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks

    override fun getDefaultLayout(): Int = R.layout.block_toggle_group_container

    override fun setDataBindingVariables(binding: ViewDataBinding?) {

        (binding as? BlockToggleGroupContainerBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockToggleGroupContainerBinding) {

        val group = binding.toggle
        group.removeAllViews()


        data.children.forEachIndexed { index, formControl ->
            val inflater = LayoutInflater.from(group.context)
            val buttonBinding = BlockToggleButtonLayoutBinding
                .inflate(inflater, null, false)
            buttonBinding.root.id = index
            group.addView(buttonBinding.root)
            buttonBinding.data = formControl
            buttonBinding.callback = callbacks

            val button = buttonBinding.root as MaterialButton

            button.setOnClickListener {
                setLinked(formControl, binding.childContainer)
            }
            if (formControl == data.children.first()) {
                group.check(button.id)
                setLinked(formControl, binding.childContainer)
            }
        }

    }

    private fun setLinked(formControl: FormControl, child: EpoxyRecyclerView) {
        val list = mutableStateListOf<FormControl>()

        val linked = data.mainData.forms.form?.filter {
            it.linkedToControl == formControl.controlID
        }
        linked?.let { list.addAll(it) }

        child.setChildren(
            callbacks = callbacks,
            dynamic = GroupForm(
                module = data.mainData.forms.module,
                form = list
            ),
            storage = data.mainData.storage
        )

    }


}

fun TypedEpoxyController<*>.toggleLayout(
    vault: LinkedVault,
    appCallbacks: AppCallbacks
) {
    toggleButton {
        id(vault.container.controlID)
        data(vault)
        callbacks(appCallbacks)
    }
}