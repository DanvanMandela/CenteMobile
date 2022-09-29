package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.alertServiceLayout

import com.elmacentemobile.data.model.user.AlertServices
import com.elmacentemobile.noServiceAlertLayout
import com.elmacentemobile.util.BaseClass.generateAlphaNumericString
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.AppData

class AlertServiceController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AppData>() {
    override fun buildModels(data: AppData?) {
        if (data == null) noServiceAlertLayout { id("no") }
        else
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