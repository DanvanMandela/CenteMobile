package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.displayItemLayout
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.AppData
import com.elmacentemobile.view.ep.data.ConfirmData
import com.elmacentemobile.view.ep.data.DisplayContent

class ConfirmController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AppData>() {

    override fun buildModels(data: AppData?) {
        when (data) {
            is ConfirmData -> setConfirmData(data)
        }
    }

    private fun setConfirmData(data: ConfirmData) {
        for (s in data.list) {
            if (BaseClass.nonCaps(s.controlType)
                == BaseClass.nonCaps(ControlTypeEnum.TEXT.type)
            ) {
                val m = DisplayContent(
                    key = s.controlID,
                    value = data.hashMap.hashMap?.get(s.controlID)!!
                )
                displayItemLayout {
                    id(s.controlID)
                    data(m)
                }
            }
        }
    }
}