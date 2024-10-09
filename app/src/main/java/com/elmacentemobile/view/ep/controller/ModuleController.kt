package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.moduleFormItemLayout
import com.elmacentemobile.moduleItemLayout
import com.elmacentemobile.util.callbacks.AppCallbacks

class ModuleController(val callbacks: AppCallbacks) :
    TypedEpoxyController<ModuleData>() {
    override fun buildModels(data: ModuleData?) {
        if (data!!.isMain) {
            data.list?.forEach {
                moduleItemLayout {
                    id(it.moduleID)
                    data(it)
                    callback(this@ModuleController.callbacks)
                }
            }
        } else {
            data.list?.forEach {
                moduleFormItemLayout {
                    id(it.moduleID)
                    data(it)
                    callback(this@ModuleController.callbacks)
                }
            }
        }

    }

}

data class ModuleData(val list: MutableList<Modules>?, val isMain: Boolean)