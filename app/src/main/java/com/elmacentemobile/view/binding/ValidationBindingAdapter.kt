package com.elmacentemobile.view.binding

import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.databinding.BlockToggleButtonLayoutBinding
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.controller.FormController
import com.elmacentemobile.view.ep.controller.NewFormController
import com.elmacentemobile.view.ep.data.DynamicData
import com.elmacentemobile.view.ep.data.FormData
import com.elmacentemobile.view.ep.data.GroupForm
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup


@BindingAdapter("callback", "form")
fun MaterialButtonToggleGroup.setToggleGroup(callbacks: AppCallbacks?, form: GroupForm?) {
    if (form != null) {
        this.tag = "toggleGroup"
        val data = form.form?.filter { a ->
            BaseClass.nonCaps(a.controlType) == BaseClass.nonCaps(
                ControlTypeEnum.R_BUTTON.type
            )
        }
        this.removeAllViews()
        for (d in 0..data!!.size.minus(1)) {
            val inflater = LayoutInflater.from(this.context)
            val binding = BlockToggleButtonLayoutBinding.inflate(inflater, null, false)
            binding.root.id = d.plus(1)
            this.addView(binding.root)
            binding.callback = callbacks
            binding.data = data[d]
            val button = binding.root as MaterialButton

            if (data[d].isChecked == null) {
                if (data[d] == data.first()) {
                    data[d].isChecked = true
                    this.check(button.id)
                    callbacks?.onToggleButton(data[d])
                } else data[d].isChecked = false
            } else {
                if (data[d].isChecked!!) {
                    this.check(button.id)
                }
            }
        }
    }
}

@BindingAdapter("callback", "validation", "storage")
fun EpoxyRecyclerView.validationForm(
    callbacks: AppCallbacks, dynamic: DynamicData?,
    storage: StorageDataSource?
) {
    var controller: EpoxyController? = null
    if (storage != null)
        if (dynamic != null) {
            when (dynamic) {
                is GroupForm -> {
                    controller = NewFormController(callbacks, storage)
                    controller.setData(FormData(forms = dynamic, storage = storage))
                }
            }
            if (controller != null) {
                this.setController(controller)
            }
        }
}


@BindingAdapter("callback", "button")
fun MaterialButton.setToggle(callbacks: AppCallbacks?, data: FormControl?) {
    val param = LinearLayout.LayoutParams(
        0, LinearLayout.LayoutParams.MATCH_PARENT, 1F
    )
    this.layoutParams = param
    if (data != null) {
        this.text = data.controlText
    }
    if (data?.controlValue != null)
        if (!TextUtils.isEmpty(data.controlValue))
            callbacks?.userInput(
                InputData(
                    name = data.controlText,
                    key = data.serviceParamID,
                    value = data.controlValue,
                    encrypted = data.isEncrypted,
                    mandatory = data.isMandatory,
                    linked = !data.linkedToControl.isNullOrBlank()
                )
            )
}


@BindingAdapter("callback", "linked", "storage")
fun EpoxyRecyclerView.setChildren(
    callbacks: AppCallbacks, dynamic: DynamicData?,
    storage: StorageDataSource?
) {
    val controller = FormController(callbacks, storage)
    this.setController(controller)
    if (dynamic != null) {
        when (dynamic) {
            is GroupForm -> {
                controller.setData(FormData(forms = dynamic, storage = storage))
            }
        }
    }
}

