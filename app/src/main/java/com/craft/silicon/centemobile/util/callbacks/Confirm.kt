package com.craft.silicon.centemobile.util.callbacks

import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.Modules
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


}