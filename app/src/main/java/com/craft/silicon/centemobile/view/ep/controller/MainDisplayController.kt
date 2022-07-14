package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.model.mainDisplayLay

class MainDisplayController(val callbacks: AppCallbacks) :
    TypedEpoxyController<DisplayData>() {
    override fun buildModels(data: DisplayData?) {
        for (s in data?.display!!) {
            mainDisplayLay(s, this@MainDisplayController.callbacks)
        }
    }

}


data class DisplayData(val display: MutableList<HashMap<String, String>>?)
