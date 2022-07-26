package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.otherServicesLayout
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.fragment.go.steps.OtherService

class OtherServicesController(val callbacks: PagerData) :
    TypedEpoxyController<MutableList<OtherService>>() {
    override fun buildModels(data: MutableList<OtherService>?) {
        data?.forEach { it ->
            otherServicesLayout {
                id(it.title)
                data(it)
                callback(this@OtherServicesController.callbacks)
            }
        }
    }
}