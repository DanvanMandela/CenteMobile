package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.landingPageItem
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.GroupLanding

class LandingPageController(val callbacks: AppCallbacks) :
    TypedEpoxyController<GroupLanding>() {
    override fun buildModels(data: GroupLanding?) {
        data!!.list.forEach {
            landingPageItem {
                id(it.title)
                data(it)
                callback(this@LandingPageController.callbacks)
            }
        }
    }
}