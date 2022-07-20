package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.atmBranchItemLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.fragment.map.BranchATMList

class ATMBranchController(val callbacks: AppCallbacks) :
    TypedEpoxyController<BranchATMList>() {
    override fun buildModels(data: BranchATMList?) {
        if (data != null) {
            for (d in data.list) {
                atmBranchItemLayout {
                    id(d.data?.id)
                    data(d)
                    callback(this@ATMBranchController.callbacks)
                }
            }
        }
    }
}