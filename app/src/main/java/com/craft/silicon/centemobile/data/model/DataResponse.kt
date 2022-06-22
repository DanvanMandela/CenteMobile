package com.craft.silicon.centemobile.data.model


import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class DataResponse(
    @field:SerializedName("Status")
    @field:Expose
    var status: String,

    @field:SerializedName("Message")
    @field:Expose
    var message: String,

    @field:SerializedName("Modules")
    @field:Expose
    var modules: MutableList<Modules>?,

    @field:SerializedName("FormControls")
    @field:Expose
    var formControls: MutableList<FormControl>?,

    @field:SerializedName("ActionControls")
    @field:Expose
    var actionControls: MutableList<ActionControls>?
) : Serializable