package com.elmacentemobile.view.ep.controller

import android.text.TextUtils
import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.buttonLayout
import com.elmacentemobile.checkBoxLayout
import com.elmacentemobile.data.model.control.ControlFormatEnum
import com.elmacentemobile.data.model.control.ControlIDEnum
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.disabledAmountTextInputLayout
import com.elmacentemobile.dropDownLayout
import com.elmacentemobile.hiddenInputLayout
import com.elmacentemobile.imageButtonLayout
import com.elmacentemobile.labelLayout
import com.elmacentemobile.labelListLayout
import com.elmacentemobile.labelTextLayout
import com.elmacentemobile.listWithOptionLayout
import com.elmacentemobile.otpLayout
import com.elmacentemobile.qRLayout
import com.elmacentemobile.recentListLayout
import com.elmacentemobile.standingOrderLayout
import com.elmacentemobile.textDisplayJsonLayout
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass.nonCaps
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.FormData
import com.elmacentemobile.view.ep.data.GroupForm
import com.elmacentemobile.view.ep.model.amountModel
import com.elmacentemobile.view.ep.model.beneficiaryViewModel
import com.elmacentemobile.view.ep.model.dateSelect
import com.elmacentemobile.view.ep.model.horizontalContainer
import com.elmacentemobile.view.ep.model.inputDisabledModel
import com.elmacentemobile.view.ep.model.inputModel
import com.elmacentemobile.view.ep.model.inputNumericModel
import com.elmacentemobile.view.ep.model.inputPanModel
import com.elmacentemobile.view.ep.model.linkedDropDownLayout
import com.elmacentemobile.view.ep.model.linkedDynamicDropDownLayout
import com.elmacentemobile.view.ep.model.passwordModel
import com.elmacentemobile.view.ep.model.phoneContacts
import com.elmacentemobile.view.ep.model.tabLayoutGroup
import com.elmacentemobile.view.ep.model.toggleLayout
import com.google.gson.Gson

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
                        AppLogger.instance.appLog("Linked:Drop", Gson().toJson(d))
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

                    nonCaps(ControlTypeEnum.BENEFICIARY.type) -> {
                        AppLogger.instance.appLog("Beneficiary", Gson().toJson(d))
                        if (d.linkedToControl == null || TextUtils.isEmpty(d.linkedToControl)) {
                            beneficiaryViewModel(
                                formControl = d,
                                appCallbacks = this@NewFormController.callbacks,
                                storage = data.storage!!,
                                module = data.forms.module
                            )
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
                        textDisplayJsonLayout {
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

                                nonCaps(ControlFormatEnum.LABEL_TEXT.type) -> labelTextLayout {
                                    id(d.controlID)
                                    data(d)
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

                nonCaps(ControlFormatEnum.AMOUNT.type) -> {
                    if (!d.displayControl.isNullOrEmpty()) {
                        if (d.displayControl == "true") {
                            disabledAmountTextInputLayout {
                                id(d.controlID)
                                data(d)
                                callback(this@NewFormController.callbacks)
                            }
                        } else amountModel(
                            form = d, storage = storageDataSource, callbacks = callbacks
                        )
                    } else amountModel(
                        form = d, storage = storageDataSource, callbacks = callbacks
                    )
                }

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

                    nonCaps(ControlFormatEnum.PAN.type) -> inputPanModel(
                        form = d,
                        storage = storageDataSource,
                        callbacks = callbacks
                    )

                    else -> {
                        if (!d.displayControl.isNullOrEmpty()) {
                            if (d.displayControl == "true")
                                inputDisabledModel(
                                    form = d,
                                    storage = storageDataSource,
                                    callbacks = callbacks
                                )
                            else inputModel(
                                form = d,
                                storage = storageDataSource,
                                callbacks = callbacks
                            )
                        } else {
                            inputModel(
                                form = d,
                                storage = storageDataSource,
                                callbacks = callbacks
                            )
                        }
                    }
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
