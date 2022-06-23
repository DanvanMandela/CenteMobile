package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.buttonLayout
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.textInputLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.GroupForm

class FormController(val callbacks: AppCallbacks) :
    TypedEpoxyController<GroupForm>() {

    override fun buildModels(data: GroupForm?) {
        for (d in data?.form!!) {
            when (d.controlType?.uppercase()) {
                ControlTypeEnum.TEXT.type -> textInputLayout {
                    id(d.controlID)
                    data(d)
                    module(data.module)
                    callback(this@FormController.callbacks)
                }
                ControlTypeEnum.BUTTON.type -> buttonLayout {
                    id(d.controlID)
                    data(d)
                    module(data.module)
                    callback(this@FormController.callbacks)
                }
            }
        }
    }
}