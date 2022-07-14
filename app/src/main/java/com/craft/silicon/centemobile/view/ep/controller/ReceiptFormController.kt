package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.displayItemLayout
import com.craft.silicon.centemobile.view.ep.data.DisplayContent
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.ReceiptList

class ReceiptFormController :
    TypedEpoxyController<DynamicData>() {
    override fun buildModels(data: DynamicData?) {
        when (data) {
            is ReceiptList -> {
                for (d in data.receipt) {
                    displayItemLayout {
                        id(d.title)
                        data(DisplayContent(d.title, d.value))
                    }
                }
            }
        }
    }
}