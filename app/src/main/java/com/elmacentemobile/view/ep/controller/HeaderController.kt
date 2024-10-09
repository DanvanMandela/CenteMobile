package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.headerItemCardLayout
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.AccountData

class HeaderController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AccountData>() {

    override fun buildModels(data: AccountData?) {
        if (data!!.account.isNotEmpty())
            for (d in data.account) headerItemCardLayout {
                id(d.id)
                data(d)
            }
    }
}

