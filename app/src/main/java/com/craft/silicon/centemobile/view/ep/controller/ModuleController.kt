package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.moduleItemLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.Body
import com.craft.silicon.centemobile.view.ep.data.ModuleData

class ModuleController(val callbacks: AppCallbacks) :
    TypedEpoxyController<Body>() {
    override fun buildModels(data: Body) {
        when (data) {
            is ModuleData -> setModuleData(data)
        }
    }

    private fun setModuleData(data: ModuleData) {
        data.list.forEach { modules ->
            moduleItemLayout {
                id(modules.ModuleID)
                data(modules)
                callback(this@ModuleController.callbacks)
            }
        }
    }
}