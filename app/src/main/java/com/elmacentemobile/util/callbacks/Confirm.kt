package com.elmacentemobile.util.callbacks

import com.elmacentemobile.data.model.action.ActionControls
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.model.module.Modules
import org.json.JSONObject

interface Confirm {
    fun onPay(data: MutableList<InputData>) {
        throw Exception("Not implemented")
    }

    fun onPay(
        json: JSONObject?,
        encrypted: JSONObject?,
        inputList: MutableList<InputData>,
        module: Modules?,
        action: ActionControls?,
        formControl: FormControl?
    ) {
        throw Exception("Not implemented")
    }

    fun onCancel() {
        throw Exception("Not implemented")
    }

    fun onConfirm() {
        throw Exception("Not implemented")
    }




}