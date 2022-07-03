package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.displayItemLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.craft.silicon.centemobile.view.ep.data.DisplayList

class DisplayController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AppData>() {
    override fun buildModels(data: AppData?) {
        when (data) {
            is DisplayList -> setDisplay(data)
        }
    }

    private fun setDisplay(data: DisplayList) {
        for (d in data.list) {
            displayItemLayout {
                id(d.key)
                data(d)
            }
        }
    }
}