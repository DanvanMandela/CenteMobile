package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.moduleItemLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks

class ModuleController(val callbacks: AppCallbacks) :
    TypedEpoxyController<MutableList<Modules>>() {
    override fun buildModels(data: MutableList<Modules>?) {
        data?.forEach {
            moduleItemLayout {
                id(it.ModuleID)
                data(it)
                callback(this@ModuleController.callbacks)
            }
        }
    }

}