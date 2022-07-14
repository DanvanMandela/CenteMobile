package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.displayItemLayout
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.craft.silicon.centemobile.view.ep.data.ConfirmData
import com.craft.silicon.centemobile.view.ep.data.DisplayContent

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