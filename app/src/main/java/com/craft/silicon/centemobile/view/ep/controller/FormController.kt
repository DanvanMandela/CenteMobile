package com.craft.silicon.centemobile.view.ep.controller

import android.text.TextUtils
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.*
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.ControlIDEnum
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.FormData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.ep.model.dateSelect
import com.craft.silicon.centemobile.view.ep.model.phoneContacts
import com.craft.silicon.centemobile.view.ep.model.radioGroup

class FormController(val callbacks: AppCallbacks) :
    TypedEpoxyController<FormData>() {

    override fun buildModels(data: FormData?) {
        for (d in data?.forms?.form!!) {
            when (BaseClass.nonCaps(d.controlType)) {
                BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> textInputLayout {
                    id(d.controlID)
                    data(d)
                    callback(this@FormController.callbacks)
                }

                BaseClass.nonCaps(ControlTypeEnum.BUTTON.type) -> buttonLayout {
                    id(d.controlID)
                    data(d)
                    module(data.forms.module)
                    callback(this@FormController.callbacks)
                }

                BaseClass.nonCaps(ControlTypeEnum.DROPDOWN.type) -> dropDownLayout {
                    id(d.controlID)
                    data(d)
                    callback(this@FormController.callbacks)
                    storage(data.storage)
                    module(data.forms.module)
                }

                BaseClass.nonCaps(ControlTypeEnum.CONTAINER.type) -> setContainer(data.forms, d)

                BaseClass.nonCaps(ControlTypeEnum.CHECKBOX.type) -> checkBoxLayout {
                    id(d.controlID)
                    data(d)
                    module(data.forms.module)
                    callback(this@FormController.callbacks)
                }

                BaseClass.nonCaps(ControlTypeEnum.PHONE_CONTACTS.type) -> {
                    phoneContacts(
                        vault = ChildVault(container = d, mainData = data),
                        appCallbacks = this@FormController.callbacks
                    )
                }

                BaseClass.nonCaps(ControlTypeEnum.HIDDEN.type) -> {
                    hiddenInputLayout {
                        id(d.controlID)
                        data(d)
                        callback(this@FormController.callbacks)
                    }
                }
                BaseClass.nonCaps(ControlTypeEnum.DATE.type) -> {
                    dateSelect(
                        vault = ChildVault(container = d, mainData = data),
                        appCallbacks = this@FormController.callbacks
                    )
                }
                BaseClass.nonCaps(ControlTypeEnum.TEXTVIEW.type) -> {
                    if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                        textDisplay {
                            id(d.controlID)
                            data(d)
                            callback(this@FormController.callbacks)
                        }
                }

                BaseClass.nonCaps(ControlTypeEnum.LIST.type) -> setList(d, data.forms)
            }
        }
    }

    private fun setList(d: FormControl, data: GroupForm) {
        when (BaseClass.nonCaps(d.controlID)) {
            BaseClass.nonCaps(ControlIDEnum.RECENT_LIST.type) -> recentListLayout {
                id(d.controlID)
                data(d)
                module(data.module)
                callback(this@FormController.callbacks)
            }
            else -> listLayout {
                id(d.controlID)
                data(d)
                module(data.module)
                callback(this@FormController.callbacks)
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