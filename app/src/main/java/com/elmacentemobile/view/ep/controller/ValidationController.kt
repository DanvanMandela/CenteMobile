package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.*
import com.elmacentemobile.data.model.control.ControlFormatEnum
import com.elmacentemobile.data.model.control.ControlIDEnum
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.FormData
import com.elmacentemobile.view.ep.data.GroupForm

class ValidationController(val callbacks: AppCallbacks) :
    TypedEpoxyController<FormData>() {
    override fun buildModels(data: FormData?) {
        for (d in data?.forms?.form!!) {
            when (BaseClass.nonCaps(d.controlType)) {
                BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> textInputLayout {
                    id(d.controlID)
                    data(d)
                    callback(this@ValidationController.callbacks)
                }

                BaseClass.nonCaps(ControlTypeEnum.BUTTON.type) -> buttonLayout {
                    id(d.controlID)
                    data(d)
                    module(data.forms.module)
                    callback(this@ValidationController.callbacks)
                }

                BaseClass.nonCaps(ControlTypeEnum.DROPDOWN.type) -> dropDownLayout {
                    id(d.controlID)
                    data(d)
                    callback(this@ValidationController.callbacks)
                    storage(data.storage)
                    module(data.forms.module)
                }

                BaseClass.nonCaps(ControlTypeEnum.CONTAINER.type) -> setContainer(data.forms, d)

                BaseClass.nonCaps(ControlTypeEnum.CHECKBOX.type) -> checkBoxLayout {
                    id(d.controlID)
                    data(d)
                    module(data.forms.module)
                    callback(this@ValidationController.callbacks)
                }

                BaseClass.nonCaps(ControlTypeEnum.LIST.type) -> setList(d, data.forms)
            }
        }
    }

    private fun setContainer(forms: GroupForm, form: FormControl) {
        when (BaseClass.nonCaps(form.controlFormat)) {
            BaseClass.nonCaps(ControlFormatEnum.RADIO_GROUPS.type) -> toggleGroupLayout {
                id("toggleGroup")
                data(forms)
                callback(this@ValidationController.callbacks)
            }
        }
    }

    private fun setList(d: FormControl, forms: GroupForm) {
        when (BaseClass.nonCaps(d.controlID)) {
            BaseClass.nonCaps(ControlIDEnum.RECENT_LIST.type) -> recentListLayout {
                id(d.controlID)
                data(d)
                module(forms.module)
                callback(this@ValidationController.callbacks)
            }
        }
    }
}