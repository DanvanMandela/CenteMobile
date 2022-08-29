package com.craft.silicon.centemobile.view.ep.controller

import android.text.TextUtils
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.*
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.ControlIDEnum
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.util.BaseClass.nonCaps
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.FormData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.ep.model.*

class NewFormController(
    val callbacks: AppCallbacks,
    val storageDataSource: StorageDataSource
) :
    TypedEpoxyController<FormData>() {
    override fun buildModels(data: FormData?) {


        for (d in data?.forms?.form!!) {
            if (nonCaps(d.controlType)
                != nonCaps(ControlTypeEnum.CONTAINER.type)
            ) {
                when (nonCaps(d.controlType)) {
                    nonCaps(ControlTypeEnum.DROPDOWN.type) -> {
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
                    nonCaps(ControlTypeEnum.DYNAMICDROPDOWN.type) -> {
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
                    nonCaps(ControlTypeEnum.BUTTON.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            buttonLayout {
                                id(d.controlID)
                                data(d)
                                module(data.forms.module)
                                callback(this@NewFormController.callbacks)
                            }
                    }
                    nonCaps(ControlTypeEnum.TEXT.type) -> setTextInputLayout(d, data)
                    nonCaps(ControlTypeEnum.CHECKBOX.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            checkBoxLayout {
                                id(d.controlID)
                                data(d)
                                module(data.forms.module)
                                callback(this@NewFormController.callbacks)
                            }
                    }
                    nonCaps(ControlTypeEnum.PHONE_CONTACTS.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            phoneContacts(
                                vault = ChildVault(container = d, mainData = data),
                                appCallbacks = this@NewFormController.callbacks
                            )
                    }
                    nonCaps(ControlTypeEnum.HIDDEN.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            hiddenInputLayout {
                                id(d.controlID)
                                data(d)
                                callback(this@NewFormController.callbacks)
                            }
                    }
                    nonCaps(ControlTypeEnum.DATE.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            dateSelect(
                                vault = ChildVault(container = d, mainData = data),
                                appCallbacks = this@NewFormController.callbacks
                            )
                    }
                    nonCaps(ControlTypeEnum.TEXTVIEW.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            textDisplay {
                                id(d.controlID)
                                data(d)
                                callback(this@NewFormController.callbacks)
                            }
                    }
                    nonCaps(ControlTypeEnum.LIST.type) -> setList(d, data.forms)

                    nonCaps(ControlTypeEnum.IMAGE.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            when (nonCaps(d.controlFormat)) {
                                nonCaps(ControlFormatEnum.IMAGE_PANEL.type) -> imageButtonLayout {
                                    id(d.controlID)
                                    data(d)
                                    callback(this@NewFormController.callbacks)
                                }
                                else -> {}
                            }

                    }
                    nonCaps(ControlTypeEnum.QR_SCANNER.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            qRLayout {
                                id(d.controlID)
                                data(d)
                                module(data.forms.module)
                                callback(this@NewFormController.callbacks)
                            }
                    }

                    nonCaps(ControlTypeEnum.LABEL.type) -> {
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
                            when (nonCaps(d.controlFormat)) {
                                nonCaps(ControlFormatEnum.LIST_DATA.type) -> labelListLayout {
                                    id(d.controlID)
                                    data(d)
                                    module(data.forms.module)
                                    callback(this@NewFormController.callbacks)
                                }
                                else -> labelLayout {
                                    id(d.controlID)
                                    data(d)
                                    callback(this@NewFormController.callbacks)
                                }
                            }


                    }


                }

            } else {
                when (nonCaps(d.controlType)) {
                    nonCaps(ControlTypeEnum.CONTAINER.type) -> {
                        when (nonCaps(d.controlFormat)) {
                            nonCaps(ControlFormatEnum.HORIZONTAL_SCROLL.type) -> {
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
                            nonCaps(ControlFormatEnum.RADIO_GROUPS.type) -> {
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
                            nonCaps(ControlFormatEnum.TAB_LAYOUT.type) -> {
                                val children =
                                    data.forms.form?.filter { a -> a.containerID == d.controlID }
                                tabLayoutGroup(
                                    vault = LinkedVault(
                                        container = d,
                                        children = children!!.toMutableList(),
                                        mainData = data
                                    ),
                                    appCallbacks = this@NewFormController.callbacks,
                                    storage = this@NewFormController.storageDataSource
                                )
                            }

                        }

                    }
                }
            }
        }
    }

    private fun setTextInputLayout(d: FormControl, data: FormData) {
        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
            when (nonCaps(d.controlFormat)) {
                nonCaps(ControlFormatEnum.OTP.type) -> otpLayout {
                    id(d.controlID)
                    data(d)
                    callback(this@NewFormController.callbacks)
                    module(data.forms.module)
                    storage(this@NewFormController.storageDataSource)
                }
                nonCaps(ControlFormatEnum.AMOUNT.type) -> amountModel(
                    form = d, storage = storageDataSource, callbacks = callbacks
                )
                nonCaps(ControlFormatEnum.PIN_NUMBER.type),
                nonCaps(ControlFormatEnum.PIN.type) -> passwordModel(
                    form = d, storage = storageDataSource, callbacks = callbacks
                )
                else -> when (nonCaps(d.controlFormat)) {
                    nonCaps(ControlFormatEnum.NUMERIC.type),
                    nonCaps(ControlFormatEnum.NUMBER.type) -> inputNumericModel(
                        form = d,
                        storage = storageDataSource,
                        callbacks = callbacks
                    )
                    else -> inputModel(
                        form = d,
                        storage = storageDataSource,
                        callbacks = callbacks
                    )
                }
            }

    }

    private fun setList(d: FormControl, data: GroupForm) {
        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl))
            when (nonCaps(d.controlID)) {
                nonCaps(ControlIDEnum.RECENT_LIST.type) -> recentListLayout {
                    id(d.controlID)
                    data(d)
                    module(data.module)
                    callback(this@NewFormController.callbacks)
                }
                else -> {
                    when (nonCaps(d.controlFormat)) {
                        nonCaps(ControlFormatEnum.LIST_WITH_OPTIONS.type) -> listWithOptionLayout {
                            id(d.controlID)
                            data(d)
                            module(data.module)
                            callback(this@NewFormController.callbacks)
                        }
                        nonCaps(ControlFormatEnum.STANDING_ORDER.type) -> standingOrderLayout {
                            id(d.controlID)
                            data(d)
                            module(data.module)
                            callback(this@NewFormController.callbacks)
                            storage(this@NewFormController.storageDataSource)
                        }
                    }
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
