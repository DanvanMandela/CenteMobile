package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.homeBodyLayout
import com.elmacentemobile.homeHeaderLayout
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.AccountData
import com.elmacentemobile.view.ep.data.AppData
import com.elmacentemobile.view.ep.data.BodyData

class AppController(val callbacks: AppCallbacks) :
    TypedEpoxyController<MutableList<AppData>>() {
    override fun buildModels(data: MutableList<AppData>?) {
        for (d in data!!) {
            when (d) {
                is AccountData -> homeHeaderLayout {
                    id("header")
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