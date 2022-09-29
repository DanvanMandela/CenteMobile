package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.*
import com.elmacentemobile.data.model.control.ControlFormatEnum
import com.elmacentemobile.data.model.control.ControlIDEnum
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.FormData
import com.elmacentemobile.view.ep.data.GroupForm
import com.elmacentemobile.view.ep.model.*

class FormController(
    val callbacks: AppCallbacks,
    val storage: StorageDataSource?
) :
    TypedEpoxyController<FormData>() {

    override fun buildModels(data: FormData?) {
        for (d in data?.forms?.form!!) {
            when (BaseClass.nonCaps(d.controlType)) {

                BaseClass.nonCaps(ControlTypeEnum.TEXT.type) -> setTextInputLayout(d, data)

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
                BaseClass.nonCaps(ControlTypeEnum.HIDDEN.type) -> {
                    hiddenInputLayout {
                        id(d.controlID)
                        data(d)
                        callback(this@FormController.callbacks)
                    }
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
                    textDisplayJsonLayout {
                        id(d.controlID)
                        data(d)
                        callback(this@FormController.callbacks)
                    }
                }

                BaseClass.nonCaps(ControlTypeEnum.LIST.type) -> setList(d, data.forms)

                BaseClass.nonCaps(ControlTypeEnum.QR_SCANNER.type) -> qRLayout {
                    id(d.controlID)
                    data(d)
                    module(data.forms.module)
                    callback(this@FormController.callbacks)
                }
                BaseClass.nonCaps(ControlTypeEnum.LABEL.type) -> {
                    when (BaseClass.nonCaps(d.controlFormat)) {
                        BaseClass.nonCaps(ControlFormatEnum.LIST_DATA.type) -> labelListLayout {
                            id(d.controlID)
                            data(d)
                            module(data.forms.module)
                            callback(this@FormController.callbacks)
                        }
                        else -> labelLayout {
                            id(d.controlID)
                            data(d)
                            callback(this@FormController.callbacks)
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

    private fun setTextInputLayout(d: FormControl, data: FormData) {
        when (BaseClass.nonCaps(d.controlFormat)) {
            BaseClass.nonCaps(ControlFormatEnum.OTP.type) -> otpLayout {
                id(d.controlID)
                data(d)
                callback(this@FormController.callbacks)
                module(data.forms.module)
                storage(this@FormController.storage)
            }
            BaseClass.nonCaps(ControlFormatEnum.AMOUNT.type) -> {
                if (!d.displayControl.isNullOrEmpty()) {
                    if (d.displayControl == "true") {
//                        inputDisabledModel(
//                            form = d,
//                            storage = storage,
//                            callbacks = callbacks
//                        )
                        disabledAmountTextInputLayout {
                            id(d.controlID)
                            data(d)
                            callback(this@FormController.callbacks)
                        }
                    } else amountModel(form = d, storage = storage, callbacks = callbacks)
                } else amountModel(form = d, storage = storage, callbacks = callbacks)
            }
            BaseClass.nonCaps(ControlFormatEnum.PIN_NUMBER.type),
            BaseClass.nonCaps(ControlFormatEnum.PIN.type) -> passwordModel(
                form = d, storage = storage, callbacks = callbacks
            )
            else -> when (BaseClass.nonCaps(d.controlFormat)) {
                BaseClass.nonCaps(ControlFormatEnum.NUMERIC.type),
                BaseClass.nonCaps(ControlFormatEnum.NUMBER.type) -> inputNumericModel(
                    form = d,
                    storage = storage,
                    callbacks = callbacks
                )
                BaseClass.nonCaps(ControlFormatEnum.PAN.type) -> inputPanModel(
                    form = d,
                    storage = storage,
                    callbacks = callbacks
                )
                else -> {
                    if (!d.displayControl.isNullOrEmpty()) {
                        if (d.displayControl == "true")
                            inputDisabledModel(form = d, storage = storage, callbacks = callbacks)
                        else inputModel(form = d, storage = storage, callbacks = callbacks)
                    } else {
                        inputModel(form = d, storage = storage, callbacks = callbacks)
                    }
                }
            }
        }

    }

}