package com.craft.silicon.centemobile.view.binding

import android.text.TextUtils
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.adapter.AutoTextArrayAdapter
import com.craft.silicon.centemobile.view.ep.adapter.NameBaseAdapter
import com.craft.silicon.centemobile.view.ep.data.NameBaseData
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("callback", "form", "storage", "module", "view")
fun AutoCompleteTextView.linkedToInput(
    callbacks: AppCallbacks,
    formControl: FormControl?,
    storage: StorageDataSource?,
    modules: Modules?,
    view: TextInputEditText?
) {

    this.setText("")
    setDefaultValue(formControl, callbacks)
    when (BaseClass.nonCaps(formControl?.controlFormat)) {
        BaseClass.nonCaps(ControlFormatEnum.ACCOUNT_BANK.type) -> {
            if (storage?.accounts?.value != null) {
                val accounts = mutableListOf<NameBaseData>()
                val data =
                    storage.accounts.value!!
                data.forEach {
                    accounts.add(
                        NameBaseData(
                            text = if (it!!.aliasName.isNullOrEmpty()) it.id else it.aliasName,
                            id = it.id
                        )
                    )
                }
                val adapter = NameBaseAdapter(context, 1, accounts)
                this.setAdapter(adapter)
                this.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, p2, _ ->
                        callbacks.userInput(
                            InputData(
                                name = formControl?.controlText,
                                key = formControl?.serviceParamID,
                                value = adapter.getItem(p2)?.id,
                                encrypted = formControl?.isEncrypted!!,
                                mandatory = formControl.isMandatory
                            )
                        )
                    }
            }
        }
        BaseClass.nonCaps(ControlFormatEnum.BENEFICIARY.type) -> {
            if (storage?.beneficiary?.value != null) {

                val beneficiaries = mutableListOf<NameBaseData>()
                val data =
                    storage.beneficiary.value!!
                        .filter { a -> a?.merchantID == modules?.merchantID }
                        .map { it?.accountAlias }
                data.forEach {
                    beneficiaries.add(
                        NameBaseData(
                            text = it,
                            id = it
                        )
                    )
                }


                val adapter = NameBaseAdapter(context, 1, beneficiaries)
                this.setAdapter(adapter)
                this.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, p2, _ ->
                        callbacks.userInput(
                            InputData(
                                name = formControl?.controlText,
                                key = formControl?.serviceParamID,
                                value = adapter.getItem(p2)?.id,
                                encrypted = formControl?.isEncrypted!!,
                                mandatory = formControl.isMandatory
                            )
                        )
                    }
            }
        }
        else -> {
            if (storage?.staticData?.value != null) {
                val staticData =
                    storage.staticData.value!!.filter { a ->
                        BaseClass.nonCaps(a?.id) == BaseClass.nonCaps(
                            formControl!!.dataSourceID
                        )
                    }
                val adapter = AutoTextArrayAdapter(context, 1, staticData)
                this.setAdapter(adapter)
                this.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, p2, _ ->
                        callbacks.userInput(
                            InputData(
                                name = formControl?.controlText,
                                key = formControl?.serviceParamID,
                                value = adapter.getItem(p2)!!.subCodeID,
                                encrypted = formControl?.isEncrypted!!,
                                mandatory = formControl.isMandatory
                            )
                        )
                        if (TextUtils.isEmpty(adapter.getItem(p2)!!.extraField)) {
                            callbacks.globalAutoLiking(adapter.getItem(p2)!!.subCodeID, view)
                        } else {
                            view?.setText(adapter.getItem(p2)?.extraField)
                        }
                    }
            }
        }
    }

}


@BindingAdapter("callback", "form", "storage", "view")
fun AutoCompleteTextView.linkedDropDown(
    callbacks: AppCallbacks,
    formControl: FormControl?,
    storage: StorageDataSource?,
    view: DropDownView
) {
    this.setText("")
    setDefaultValue(formControl, callbacks)
    if (storage?.staticData?.value != null) {
        val staticData =
            storage.staticData.value!!.filter { a ->
                BaseClass.nonCaps(a?.id) == BaseClass.nonCaps(
                    formControl!!.dataSourceID
                )
            }
        val adapter = AutoTextArrayAdapter(context, 1, staticData)
        this.setAdapter(adapter)
        this.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                callbacks.userInput(
                    InputData(
                        name = formControl?.controlText,
                        key = formControl?.serviceParamID,
                        value = adapter.getItem(p2)!!.subCodeID,
                        encrypted = formControl?.isEncrypted!!,
                        mandatory = formControl.isMandatory
                    )
                )
                setLinkedDropDown(
                    view = view,
                    storage = storage,
                    item = adapter.getItem(p2),
                    callbacks = callbacks
                )
            }

    }

}

fun setDefaultValue(formControl: FormControl?, callbacks: AppCallbacks?) {
    callbacks?.userInput(
        InputData(
            name = formControl?.controlText,
            key = formControl?.serviceParamID,
            value = "",
            encrypted = formControl?.isEncrypted!!,
            mandatory = formControl.isMandatory
        )
    )
}

fun setLinkedDropDown(
    view: DropDownView,
    storage: StorageDataSource,
    item: StaticDataDetails?,
    callbacks: AppCallbacks,
) {
    view.dropDown.setText("")
    setDefaultValue(view.data, callbacks)
    val data = storage.staticData.value?.filter { a -> a?.relationID == item?.subCodeID }
    val adapter = AutoTextArrayAdapter(view.dropDown.context, 1, data!!.toMutableList())
    view.dropDown.setAdapter(adapter)
    view.dropDown.onItemClickListener =
        AdapterView.OnItemClickListener { _, _, p2, _ ->
            callbacks.userInput(
                InputData(
                    name = view.data.controlText,
                    key = view.data.serviceParamID,
                    value = adapter.getItem(p2)!!.subCodeID,
                    encrypted = view.data.isEncrypted,
                    mandatory = view.data.isMandatory
                )
            )
            if (view.child != null)
                setChildToChildDropLink(
                    view.child,
                    adapter.getItem(p2),
                    callbacks,
                    storage
                )


        }
}

fun setChildToChildDropLink(
    child: DropDownView?,
    item: StaticDataDetails?,
    callbacks: AppCallbacks,
    storage: StorageDataSource
) {
    child!!.dropDown.setText("")
    setDefaultValue(child.data, callbacks)
    when (BaseClass.nonCaps(child.data.controlFormat)) {
        BaseClass.nonCaps(ControlFormatEnum.BENEFICIARY.type) -> {
            if (storage.beneficiary.value != null) {
                val beneficiaries = mutableListOf<NameBaseData>()
                val data =
                    storage.beneficiary.value!!
                        .filter { a -> a?.merchantID == item?.subCodeID }
                        .map { it?.accountAlias }
                data.forEach {
                    beneficiaries.add(
                        NameBaseData(
                            text = it,
                            id = it
                        )
                    )
                }
                val adapter = NameBaseAdapter(child.dropDown.context, 1, beneficiaries)
                child.dropDown.setAdapter(adapter)
                child.dropDown.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, p2, _ ->
                        callbacks.userInput(
                            InputData(
                                name = child.data.controlText,
                                key = child.data.serviceParamID,
                                value = adapter.getItem(p2)?.id,
                                encrypted = child.data.isEncrypted,
                                mandatory = child.data.isMandatory
                            )
                        )
                    }
            }
        }
    }


}

data class DropDownView(
    val dropDown: AutoCompleteTextView,
    val data: FormControl,
    val child: DropDownView?
)


//@BindingAdapter("callback", "form", "value")
//fun TextInputEditText.setInputLayoutLinked(
//    callbacks: AppCallbacks?,
//    formControl: FormControl?,
//    value: String
//) {
//    this.setText(value)
//    if (!TextUtils.isEmpty(value)) {
//        callbacks?.userInput(
//            InputData(
//                name = formControl?.controlText,
//                key = formControl?.serviceParamID,
//                value = NumberTextWatcherForThousand.trimCommaOfString(value),
//                encrypted = formControl!!.isEncrypted,
//                mandatory = formControl.isMandatory
//            )
//        )
//    }
//    callbacks?.onServerValue(formControl, this)
//    setControl(this, formControl)
//    if (formControl?.controlFormat != null) {
//        if (!TextUtils.isEmpty(formControl.controlFormat)) {
//            when (BaseClass.nonCaps(formControl.controlFormat)) {
//                BaseClass.nonCaps(ControlFormatEnum.AMOUNT.type) -> {
//                    this.inputType =
//                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
//                    this.addTextChangedListener(
//                        NumberTextWatcherForThousand(this, callbacks, formControl)
//                    )
//                }
//                BaseClass.nonCaps(ControlFormatEnum.PIN.type),
//                BaseClass.nonCaps(ControlFormatEnum.PIN_NUMBER.type) -> {
//                    setPasswordWatcher(this, callbacks, formControl)
//                }
//                else -> setDefaultWatcher(this, callbacks, formControl)
//            }
//        } else setDefaultWatcher(this, callbacks, formControl)
//    } else setDefaultWatcher(this, callbacks, formControl!!)
//
//}

@BindingAdapter("callback")
fun ImageButton.setButton(callbacks: AppCallbacks?) {

}



