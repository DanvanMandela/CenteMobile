package com.craft.silicon.centemobile.view.binding

import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.adapter.AutoTextArrayAdapter
import com.craft.silicon.centemobile.view.ep.adapter.BeneficiaryArrayAdapter
import com.craft.silicon.centemobile.view.ep.adapter.NameBaseAdapter
import com.craft.silicon.centemobile.view.ep.data.NameBaseData
import com.google.gson.Gson

@BindingAdapter("callback", "auto_date")
fun AutoCompleteTextView.contacts(
    callbacks: AppCallbacks,
    formControl: FormControl?
) {
    AppLogger.instance.appLog("DROP:DOWN", Gson().toJson(formControl))
    this.setText("")
    setDefaultValue(formControl, callbacks)
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(e: Editable?) {
            callbacks.userInput(
                InputData(
                    name = formControl?.controlText,
                    key = formControl?.serviceParamID,
                    value = e.toString().replace("+", "")
                        .replace(" ", ""),
                    encrypted = formControl?.isEncrypted!!,
                    mandatory = formControl.isMandatory
                )
            )
        }
    })


}

@BindingAdapter("callback", "form", "storage", "module")
fun AutoCompleteTextView.setDropDownData(
    callbacks: AppCallbacks?,
    formControl: FormControl?,
    storage: StorageDataSource?,
    modules: Modules?
) {
    AppLogger.instance.appLog("DROP:DOWN", Gson().toJson(formControl))
    AppLogger.instance.appLog("DROP:MERCHANT", Gson().toJson(modules))
    AppLogger.instance.appLog("DROP:BENEFICIARY", Gson().toJson(storage?.beneficiary?.value))
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
                if (accounts.isNotEmpty()) {
                    this.setText(adapter.getItem(0)?.text, false)
                    this.setSelection(0)
                    callbacks?.userInput(
                        InputData(
                            name = formControl?.controlText,
                            key = formControl?.serviceParamID,
                            value = adapter.getItem(0)?.id,
                            encrypted = formControl?.isEncrypted!!,
                            mandatory = formControl.isMandatory
                        )
                    )
                }
                this.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, p2, _ ->
                        callbacks?.userInput(
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
                val beneficiaries =
                    storage.beneficiary.value!!
                        .filter { a -> a?.merchantID == modules?.merchantID }

                val adapter = BeneficiaryArrayAdapter(
                    context, 1,
                    beneficiaries
                )
                this.setAdapter(adapter)
                if (beneficiaries.isNotEmpty()) {
                    this.setText(adapter.getItem(0)?.accountAlias, false)
                    this.setSelection(0)
                    callbacks?.userInput(
                        InputData(
                            name = formControl?.controlText,
                            key = formControl?.serviceParamID,
                            value = adapter.getItem(0)?.accountID,
                            encrypted = formControl?.isEncrypted!!,
                            mandatory = formControl.isMandatory
                        )
                    )
                }

                this.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, p2, _ ->
                        callbacks?.userInput(
                            InputData(
                                name = formControl?.controlText,
                                key = formControl?.serviceParamID,
                                value = adapter.getItem(p2)?.accountID,
                                encrypted = formControl?.isEncrypted!!,
                                mandatory = formControl.isMandatory
                            )
                        )
                    }
            }
        }
        else -> {
            if (storage?.staticData?.value != null) {
                AppLogger.instance.appLog("DROP:DOWN:static", Gson().toJson(formControl))
                val staticData =
                    storage.staticData.value!!.filter { a ->
                        BaseClass.nonCaps(a?.id) == BaseClass.nonCaps(
                            formControl!!.dataSourceID
                        )
                    }
                val adapter = AutoTextArrayAdapter(context, 1, staticData)
                this.setAdapter(adapter)
                if (staticData.isNotEmpty()) {
                    this.setText(adapter.getItem(0)?.description, false)
                    this.setSelection(0)
                    callbacks?.userInput(
                        InputData(
                            name = formControl?.controlText,
                            key = formControl?.serviceParamID,
                            value = adapter.getItem(0)!!.subCodeID,
                            encrypted = formControl?.isEncrypted!!,
                            mandatory = formControl.isMandatory
                        )
                    )
                }

                this.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, p2, _ ->
                        callbacks?.userInput(
                            InputData(
                                name = formControl?.controlText,
                                key = formControl?.serviceParamID,
                                value = adapter.getItem(p2)!!.subCodeID,
                                encrypted = formControl?.isEncrypted!!,
                                mandatory = formControl.isMandatory
                            )
                        )
                    }

            }
        }
    }

}


@BindingAdapter("callback", "form", "module")
fun AutoCompleteTextView.setDynamicDropDown(
    callbacks: AppCallbacks?,
    formControl: FormControl?,
    modules: Modules?
) {
    this.setText("")
    setDefaultValue(formControl, callbacks)
}

