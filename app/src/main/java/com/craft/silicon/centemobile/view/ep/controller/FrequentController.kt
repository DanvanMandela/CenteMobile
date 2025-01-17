package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.data.model.user.FrequentModules
import com.craft.silicon.centemobile.frequentModuleItem
import com.craft.silicon.centemobile.noFrequentLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks

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