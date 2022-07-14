package com.craft.silicon.centemobile.view.binding

import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
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
import com.craft.silicon.centemobile.view.ep.controller.FormController
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.FormData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
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
                val accounts = storage.accounts.value!!.map { it?.id }
                val adapter = NameBaseAdapter(context, 1, accounts)
                this.setAdapter(adapter)
                this.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, p2, _ ->
                        callbacks.userInput(
                            InputData(
                                name = formControl?.controlText,
                                key = formControl?.serviceParamID,
                                value = adapter.getItem(p2),
                                encrypted = formControl?.isEncrypted!!,
                                mandatory = formControl.isMandatory
                            )
                        )
                    }
            }
        }
        BaseClass.nonCaps(ControlFormatEnum.BENEFICIARY.type) -> {
            if (storage?.beneficiary?.value != null) {
                val beneficiaries =
                    storage.beneficiary.value!!
                        .filter { a -> a?.merchantID == modules?.merchantID }
                        .map { it?.accountAlias }
                val adapter = NameBaseAdapter(context, 1, beneficiaries)
                this.setAdapter(adapter)
                this.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, p2, _ ->
                        callbacks.userInput(
                            InputData(
                                name = formControl?.controlText,
                                key = formControl?.serviceParamID,
                                value = adapter.getItem(p2),
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

                        view?.setText(adapter.getItem(p2)?.extraField)
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


        }
}

data class DropDownView(
    val dropDown: AutoCompleteTextView,
    val data: FormControl
)


@BindingAdapter("callback", "form", "value")
fun TextInputEditText.setInputLayoutLinked(
    callbacks: AppCallbacks?,
    formControl: FormControl?,
    value: String
) {
    this.setText(value)
    callbacks?.onServerValue(formControl, this)
    this.setInput(formControl?.controlFormat)
    callbacks?.userInput(
        InputData(
            name = formControl?.controlText,
            key = formControl?.serviceParamID,
            value = value,
            encrypted = formControl?.isEncrypted!!,
            mandatory = formControl.isMandatory
        )
    )

    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(e: Editable?) {
            callbacks?.userInput(
                InputData(
                    name = formControl?.controlText,
                    key = formControl?.serviceParamID,
                    value = e.toString(),
                    encrypted = formControl?.isEncrypted!!,
                    mandatory = formControl.isMandatory
                )
            )
        }
    })


}

@BindingAdapter("callback")
fun ImageButton.setButton(callbacks: AppCallbacks?) {

}

@BindingAdapter("callback", "data")
fun EpoxyRecyclerView.setLinkedForm(
    callbacks: AppCallbacks,
    data: FormData?
) {
    val controller = FormController(callbacks)
    controller.setData(data)
    this.setController(controller)
}




