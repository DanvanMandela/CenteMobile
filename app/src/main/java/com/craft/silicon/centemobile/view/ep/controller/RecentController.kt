package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.recentPaymentItemLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.craft.silicon.centemobile.view.ep.data.ResultDataList

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