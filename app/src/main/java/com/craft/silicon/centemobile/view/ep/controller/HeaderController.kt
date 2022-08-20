package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.headerItemCardLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AccountData

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

