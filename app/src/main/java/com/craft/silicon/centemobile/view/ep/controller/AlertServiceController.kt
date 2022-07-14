package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.alertServiceLayout
import com.craft.silicon.centemobile.data.model.user.AlertServices
import com.craft.silicon.centemobile.util.BaseClass.generateAlphaNumericString
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AppData

class AlertServiceController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AppData>() {
    override fun buildModels(data: AppData?) {
        when (data) {
            is AlertList -> setList(data)
        }


    }

    private fun setList(data: AlertList) {
        for (s in data.list) {
            alertServiceLayout {
                id(generateAlphaNumericString(4))
                data(s)
                callback(this@AlertServiceController.callbacks)
            }
        }
    }
}


data class AlertList(val list: MutableList<AlertServices>) : AppData()