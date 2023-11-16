package com.elmacentemobile.view.binding.auto

import android.content.Context
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.elmacentemobile.data.model.control.ControlFormatEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.model.static_data.StaticDataDetails
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.binding.setDefaultValue
import com.elmacentemobile.view.ep.adapter.AutoTextArrayAdapter
import com.elmacentemobile.view.ep.adapter.BeneficiaryArrayAdapter
import com.elmacentemobile.view.ep.adapter.NameBaseAdapter
import com.elmacentemobile.view.ep.data.NameBaseData

data class DropDownData(
    val callback: AppCallbacks,
    val form: FormControl,
    val modules: Modules,
    val storage: StorageDataSource,
    val view: AutoCompleteTextView,
    val child: DropDownData? = null
)

fun dropDownLinking(data: DropDownData) {
    data.view.setText(String())
    val context = data.view.context
    setDefaultValue(formControl = data.form, callbacks = data.callback)
    when (BaseClass.nonCaps(data.form.controlFormat)) {
        BaseClass.nonCaps(ControlFormatEnum.ACCOUNT_BANK.type) -> {
            val accounts = mutableListOf<NameBaseData>()
            val formData =
                data.storage.accounts.value!!
            formData.forEach {
                accounts.add(
                    NameBaseData(
                        text = if (it!!.aliasName.isNullOrEmpty()) it.id else it.aliasName,
                        id = it.id
                    )
                )
            }
            val adapter = NameBaseAdapter(context, 1, accounts)
            data.view.setAdapter(adapter)
            if (accounts.isNotEmpty()) {
                data.view.setText(adapter.getItem(0)?.text, false)
                data.view.setSelection(0)
                data.callback.userInput(
                    InputData(
                        name = data.form.controlText,
                        key = data.form.serviceParamID,
                        value = adapter.getItem(0)!!.id,
                        encrypted = data.form.isEncrypted,
                        mandatory = data.form.isMandatory
                    )
                )
            }

            data.view.onItemClickListener = AdapterView.OnItemClickListener { _, _, p2, _ ->
                data.callback.userInput(
                    InputData(
                        name = data.form.controlText,
                        key = data.form.serviceParamID,
                        value = adapter.getItem(p2)!!.id,
                        encrypted = data.form.isEncrypted,
                        mandatory = data.form.isMandatory
                    )
                )
            }
        }

        BaseClass.nonCaps(ControlFormatEnum.BENEFICIARY.type) -> {
            val beneficiaries =
                data.storage.beneficiary.value!!
                    .filter { a -> a?.merchantID == data.modules.merchantID }

            val adapter = BeneficiaryArrayAdapter(
                context, 1,
                beneficiaries
            )
            data.view.setAdapter(adapter)
            if (beneficiaries.isNotEmpty()) {
                data.view.setText(adapter.getItem(0)?.accountAlias, false)
                data.view.setSelection(0)
                data.callback.userInput(
                    InputData(
                        name = data.form.controlText,
                        key = data.form.serviceParamID,
                        value = adapter.getItem(0)!!.accountID,
                        encrypted = data.form.isEncrypted,
                        mandatory = data.form.isMandatory
                    )
                )
            }

            data.view.onItemClickListener = AdapterView.OnItemClickListener { _, _, p2, _ ->
                data.callback.userInput(
                    InputData(
                        name = data.form.controlText,
                        key = data.form.serviceParamID,
                        value = adapter.getItem(p2)!!.accountID,
                        encrypted = data.form.isEncrypted,
                        mandatory = data.form.isMandatory
                    )
                )
            }
        }

        else -> {
            val staticData =
                data.storage.staticData.value!!.filter { a ->
                    BaseClass.nonCaps(a?.id) == BaseClass.nonCaps(
                        data.form.dataSourceID
                    )
                }
            val adapter = AutoTextArrayAdapter(context, 1, staticData)
            data.view.setAdapter(adapter)
            if (staticData.isNotEmpty()) {
                data.view.setText(adapter.getItem(0)?.description, false)
                data.view.setSelection(0)
                data.callback.userInput(
                    InputData(
                        name = data.form.controlText,
                        key = data.form.serviceParamID,
                        value = adapter.getItem(0)!!.subCodeID,
                        encrypted = data.form.isEncrypted,
                        mandatory = data.form.isMandatory
                    )
                )
                if (data.child != null)
                    childDropDownLinking(
                        data.child,
                        adapter.getItem(0)!!,
                        context
                    )
            }

            data.view.onItemClickListener = AdapterView.OnItemClickListener { _, _, p2, _ ->
                data.callback.userInput(
                    InputData(
                        name = data.form.controlText,
                        key = data.form.serviceParamID,
                        value = adapter.getItem(p2)!!.subCodeID,
                        encrypted = data.form.isEncrypted,
                        mandatory = data.form.isMandatory
                    )
                )
                if (data.child != null)
                    childDropDownLinking(
                        data.child,
                        adapter.getItem(p2)!!,
                        context
                    )
            }

        }
    }


}

fun childDropDownLinking(child: DropDownData, item: StaticDataDetails, context: Context) {
    child.view.setText(String())
    setDefaultValue(formControl = child.form, callbacks = child.callback)
    val value = mutableListOf<NameBaseData>()
    when (BaseClass.nonCaps(child.form.controlFormat)) {
        BaseClass.nonCaps(ControlFormatEnum.BENEFICIARY.type) -> {
            val data = child.storage.beneficiary.value?.filter { b ->
                b?.merchantID == item.subCodeID
            }?.map { it?.accountAlias }
            data?.forEach {
                value.add(
                    NameBaseData(
                        text = it,
                        id = it
                    )
                )
            }

            val adapter = NameBaseAdapter(context, 1, value)
            child.view.setAdapter(adapter)
            child.view.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    child.callback.userInput(
                        InputData(
                            name = child.form.controlText,
                            key = child.form.serviceParamID,
                            value = adapter.getItem(p2)?.id,
                            encrypted = child.form.isEncrypted,
                            mandatory = child.form.isMandatory
                        )
                    )
                }

        }

        BaseClass.nonCaps(ControlFormatEnum.ACCOUNT_BANK.type) -> {
            val data =
                child.storage.accounts.value!!
            data.forEach {
                value.add(
                    NameBaseData(
                        text = if (it!!.aliasName.isNullOrEmpty())
                            it.id else it.aliasName,
                        id = it.id
                    )
                )
            }
            val adapter = NameBaseAdapter(context, 1, value)
            child.view.setAdapter(adapter)
            child.view.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    child.callback.userInput(
                        InputData(
                            name = child.form.controlText,
                            key = child.form.serviceParamID,
                            value = adapter.getItem(p2)?.id,
                            encrypted = child.form.isEncrypted,
                            mandatory = child.form.isMandatory
                        )
                    )
                }
        }

        else -> {
            val staticData =
                child.storage.staticData.value!!.filter { a ->
                    BaseClass.nonCaps(a?.relationID) == BaseClass.nonCaps(item.subCodeID)
                }
            val adapter = AutoTextArrayAdapter(context, 1, staticData)
            child.view.setAdapter(adapter)
//            if (staticData.isNotEmpty()) {
//                child.view.setText(adapter.getItem(0)?.description, false)
//                child.view.setSelection(0)
//                child.callback.userInput(
//                    InputData(
//                        name = child.form.controlText,
//                        key = child.form.serviceParamID,
//                        value = adapter.getItem(0)!!.subCodeID,
//                        encrypted = child.form.isEncrypted,
//                        mandatory = child.form.isMandatory
//                    )
//                )
//            }
            child.view.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    child.callback.userInput(
                        InputData(
                            name = child.form.controlText,
                            key = child.form.serviceParamID,
                            value = adapter.getItem(p2)!!.subCodeID,
                            encrypted = child.form.isEncrypted,
                            mandatory = child.form.isMandatory
                        )
                    )

                }
        }

    }

}
