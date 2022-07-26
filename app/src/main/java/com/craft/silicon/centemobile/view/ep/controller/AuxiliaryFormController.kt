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
import com.craft.silicon.centemobile.view.ep.model.*

class NewFormController(val callbacks: AppCallbacks) :
    TypedEpoxyController<FormData>() {
    override fun buildModels(data: FormData?) {


        for (d in data?.forms?.form!!) {
            if (BaseClass.nonCaps(d.controlType)
                != BaseClass.nonCaps(ControlTypeEnum.CONTAINER.type)
            ) {
                when (BaseClass.nonCaps(d.controlType)) {
                    BaseClass.nonCaps(ControlTypeEnum.DROPDOWN.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl)) {
                            val children =
                                data.forms.form?.filter { it -> it.linkedToControl == d.controlID }
                            if (children.isNullOrEmpty()) {
                                dropDownLayout {
                                    id(d.controlID)
                                    data(d)
                                    callback(this@NewFormController.callbacks)
                                    storage(data.storage)
                                    module(data.forms.module)
                                }
                            } else {
                                linkedDropDownLayout(
                                    vault = LinkedVault(
                                        container = d,
                                        children = children.toMutableList(),
                                        mainData = data
                                    ),
                                    appCallbacks = this@NewFormController.callbacks
                                )
                            }
                        }
                    }
                    BaseClass.nonCaps(ControlTypeEnum.DYNAMICDROPDOWN.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl)) {
                            val children =
                                data.forms.form?.filter { it -> it.linkedToControl == d.controlID }
                            if (children.isNullOrEmpty()) {
                                dropDownLayout {
                                    id(d.controlID)
                                    data(d)
                                    callback(this@NewFormController.callbacks)
                                    storage(data.storage)
                                    module(data.forms.module)
                                }
                            } else {
                                linkedDynamicDropDownLayout(
                                    vault = LinkedVault(
                                        container = d,
                                        children = children.toMutableList(),
                                        mainData = data
                                    ),
                                    appCallbacks = this@NewFormController.callbacks
                                )
                            }
                        }
                    }
                    BaseClass.nonCaps(ControlTypeEnum.BUTTON.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            buttonLayout {
                                id(d.controlID)
                                data(d)
                                module(data.forms.module)
                                callback(this@NewFormController.callbacks)
                            }
                    }
                    BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            when (BaseClass.nonCaps(d.controlFormat)) {
                                BaseClass.nonCaps(ControlFormatEnum.OTP.type) -> otpLayout {
                                    id(d.controlID)
                                    data(d)
                                    callback(this@NewFormController.callbacks)
                                    module(data.forms.module)
                                }
                                else -> textInputLayout {
                                    id(d.controlID)
                                    data(d)
                                    callback(this@NewFormController.callbacks)
                                }
                            }
                    }
                    BaseClass.nonCaps(ControlTypeEnum.CHECKBOX.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            checkBoxLayout {
                                id(d.controlID)
                                data(d)
                                module(data.forms.module)
                                callback(this@NewFormController.callbacks)
                            }
                    }
                    BaseClass.nonCaps(ControlTypeEnum.PHONE_CONTACTS.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            phoneContacts(
                                vault = ChildVault(container = d, mainData = data),
                                appCallbacks = this@NewFormController.callbacks
                            )
                    }
                    BaseClass.nonCaps(ControlTypeEnum.HIDDEN.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            hiddenInputLayout {
                                id(d.controlID)
                                data(d)
                                callback(this@NewFormController.callbacks)
                            }
                    }
                    BaseClass.nonCaps(ControlTypeEnum.DATE.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            dateSelect(
                                vault = ChildVault(container = d, mainData = data),
                                appCallbacks = this@NewFormController.callbacks
                            )
                    }
                    BaseClass.nonCaps(ControlTypeEnum.TEXTVIEW.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            textDisplay {
                                id(d.controlID)
                                data(d)
                                callback(this@NewFormController.callbacks)
                            }
                    }
                    BaseClass.nonCaps(ControlTypeEnum.LIST.type) -> setList(d, data.forms)

                    BaseClass.nonCaps(ControlTypeEnum.IMAGE.type) -> imageButtonLayout {
                        id(d.controlID)
                        data(d)
                        callback(this@NewFormController.callbacks)
                    }
                }

            } else {
                when (BaseClass.nonCaps(d.controlType)) {
                    BaseClass.nonCaps(ControlTypeEnum.CONTAINER.type) -> {
                        when (BaseClass.nonCaps(d.controlFormat)) {
                            BaseClass.nonCaps(ControlFormatEnum.HORIZONTAL_SCROLL.type) -> {
                                val children =
                                    data.forms.form?.filter { a -> a.containerID == d.controlID }
                                horizontalContainer(
                                    vault = LinkedVault(
                                        container = d,
                                        children = children!!.toMutableList(),
                                        mainData = data
                                    ),
                                    appCallbacks = this@NewFormController.callbacks
                                )
                            }
                            BaseClass.nonCaps(ControlFormatEnum.RADIO_GROUPS.type) -> {
                                val children =
                                    data.forms.form?.filter { a -> a.containerID == d.controlID }
                                toggleLayout(
                                    vault = LinkedVault(
                                        container = d,
                                        children = children!!.toMutableList(),
                                        mainData = data
                                    ),
                                    appCallbacks = this@NewFormController.callbacks
                                )
                            }
                            BaseClass.nonCaps(ControlFormatEnum.TAB_LAYOUT.type) -> {
                                val children =
                                    data.forms.form?.filter { a -> a.containerID == d.controlID }
                                tabLayoutGroup(
                                    vault = LinkedVault(
                                        container = d,
                                        children = children!!.toMutableList(),
                                        mainData = data
                                    ),
                                    appCallbacks = this@NewFormController.callbacks
                                )
                            }

                        }

                    }
                }
            }
        }
    }

    private fun setList(d: FormControl, data: GroupForm) {
        when (BaseClass.nonCaps(d.controlID)) {
            BaseClass.nonCaps(ControlIDEnum.RECENT_LIST.type) -> recentListLayout {
                id(d.controlID)
                data(d)
                module(data.module)
                callback(this@NewFormController.callbacks)
            }
        }
    }

}

data class LinkedVault(
    var container: FormControl,
    var children: MutableList<FormControl>,
    var mainData: FormData
)

data class ChildVault(
    var container: FormControl,
    var mainData: FormData
)
