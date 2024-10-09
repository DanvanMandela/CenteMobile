package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.recentPaymentItemLayout
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.data.AppData
import com.elmacentemobile.view.ep.data.ResultDataList

class RecentController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AppData>() {
    override fun buildModels(data: AppData?) {
        when (data) {
            is ResultDataList -> setRecentList(data)
        }
    }

    private fun setRecentList(data: ResultDataList) {
        for (d in data.list) {
            recentPaymentItemLayout {
                id(d.refToken)
                data(d)
            }
        }
    }
}