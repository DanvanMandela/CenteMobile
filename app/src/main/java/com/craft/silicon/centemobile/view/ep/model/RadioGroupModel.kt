package com.craft.silicon.centemobile.view.ep.model

import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.DataBindingEpoxyModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.databinding.BlockRadioButtonLayoutBinding
import com.craft.silicon.centemobile.databinding.BlockRadionGroupLayoutBinding
import com.craft.silicon.centemobile.util.BaseClass.nonCaps
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.GroupForm

@EpoxyModelClass
open class RadioGroupModel : DataBindingEpoxyModel() {
    @EpoxyAttribute
    lateinit var data: GroupForm

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks


    override fun getDefaultLayout() = R.layout.block_radion_group_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockRadionGroupLayoutBinding)?.apply {
            addChildren(this.radioGroup)
        }
    }

    private fun addChildren(parent: RadioGroup) {

        val s =
            data.form?.filter { a -> nonCaps(a.controlType) == nonCaps(ControlTypeEnum.R_BUTTON.type) }
        parent.removeAllViews()
        s?.forEachIndexed { index, formControl ->
            val inflater = LayoutInflater.from(parent.context)
            val binding =
                BlockRadioButtonLayoutBinding.inflate(inflater, null, false)
            binding.data = formControl
            binding.callback = callbacks
            binding.root.id = index
            parent.addView(binding.root)
            val v = binding.root as RadioButton
            if (formControl == s.first()) {
                if (formControl.isChecked == null) {
                    v.isChecked = true
                    callbacks.onRadioCheck(formControl, v)
                }

            }
        }
    }
}

fun TypedEpoxyController<*>.addRadioGroup(f: GroupForm, appCallbacks: AppCallbacks) {
    radioGroup {
        id("radioGroup")
        data(f)
        callbacks(appCallbacks)
    }
}