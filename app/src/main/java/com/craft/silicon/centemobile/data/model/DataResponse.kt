package com.craft.silicon.centemobile.data.model


import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import java.io.Serializable


data class DataResponse(
    var Status: String,
    var Message: String,
    var Modules: MutableList<Modules>,
    var FormControls: MutableList<FormControl>,
    var ActionControls: MutableList<ActionControls>
) : Serializable