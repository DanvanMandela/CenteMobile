package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.data.model.user.FrequentModules
import com.elmacentemobile.frequentModuleItem
import com.elmacentemobile.noFrequentLayout
import com.elmacentemobile.util.callbacks.AppCallbacks

class FrequentController(val callbacks: AppCallbacks) :
    TypedEpoxyController<MutableList<FrequentModules>>() {
    override fun buildModels(data: MutableList<FrequentModules>?) {
        if (data != null) {
            if (data.isNotEmpty()) data.forEach {
                frequentModuleItem {
                    id(it.moduleId)
                    data(it)
                    callback(this@FrequentController.callbacks)
                }
            } else noFrequentLayout {
                id("no_frequent")
            }
        } else {
            noFrequentLayout {
                id("no_frequent")
            }
        }
    }
}