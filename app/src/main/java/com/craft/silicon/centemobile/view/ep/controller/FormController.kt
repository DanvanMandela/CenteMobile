package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.buttonLayout
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.dropDownLayout
import com.craft.silicon.centemobile.textInputLayout
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.ep.model.addMainLayout
import com.craft.silicon.centemobile.view.ep.model.radioGroup

class FormController(val callbacks: AppCallbacks) :
    TypedEpoxyController<GroupForm>() {

    override fun buildModels(data: GroupForm?) {
        //addMainLayout(data!!, callbacks)
        for (d in data?.form!!) {
            when (BaseClass.nonCaps(d.controlType)) {
                BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> textInputLayout {
                    id(d.controlID)
                    data(d)
                    module(data.module)
                    callback(this@FormController.callbacks)
                }

                BaseClass.nonCaps(ControlTypeEnum.BUTTON.type) -> buttonLayout {
                    id(d.controlID)
                    data(d)
                    module(data.module)
                    callback(this@FormController.callbacks)
                }

                BaseClass.nonCaps(ControlTypeEnum.DROPDOWN.type) -> dropDownLayout {
                    id(d.controlID)
                    data(d)
                    form(data)
                    callback(this@FormController.callbacks)
                }

                BaseClass.nonCaps(ControlTypeEnum.CONTAINER.type) -> setContainer(data, d)
            }
        }
    }

    private fun setContainer(data: GroupForm, d: FormControl) {
        when (BaseClass.nonCaps(d.controlFormat)) {
            BaseClass.nonCaps(ControlFormatEnum.RADIO_GROUPS.type) -> radioGroup {
                id("radioGroup")
                data(data)
                callbacks(this@FormController.callbacks)
            }
        }
    }

}