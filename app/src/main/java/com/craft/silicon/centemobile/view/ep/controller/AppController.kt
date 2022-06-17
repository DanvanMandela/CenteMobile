package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.homeBodyLayout
import com.craft.silicon.centemobile.homeHeaderLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.craft.silicon.centemobile.view.ep.data.BodyData
import com.craft.silicon.centemobile.view.ep.data.HeaderData

class AppController(val callbacks: AppCallbacks) :
    TypedEpoxyController<MutableList<AppData>>() {
    override fun buildModels(data: MutableList<AppData>?) {
        for (d in data!!) {
            when (d) {
                is HeaderData -> homeHeaderLayout {
                    id("header")
                    data(d)
                    callback(this@AppController.callbacks)
                }
                is BodyData -> homeBodyLayout {
                    id("body")
                    data(d)
                    callback(this@AppController.callbacks)
                }
            }
        }
    }

}