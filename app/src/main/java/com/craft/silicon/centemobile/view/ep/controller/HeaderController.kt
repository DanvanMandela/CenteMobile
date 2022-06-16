package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.headerItemCardLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.HeaderData

class HeaderController(val callbacks: AppCallbacks) :
    TypedEpoxyController<HeaderData>() {

    override fun buildModels(data: HeaderData?) {
        if (data!!.cardData.isNotEmpty())
            for (d in data.cardData) headerItemCardLayout {
                id(d.title)
                data(d)
            }
    }
}