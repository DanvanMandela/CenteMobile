package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.atmBranchItemLayout
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.fragment.map.BranchATMList

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