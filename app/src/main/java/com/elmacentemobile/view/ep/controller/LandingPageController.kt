package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.landingPageItem
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.GroupLanding

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