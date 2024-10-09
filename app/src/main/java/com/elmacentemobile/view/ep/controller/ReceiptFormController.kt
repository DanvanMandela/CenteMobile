package com.elmacentemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.displayItemLayout
import com.elmacentemobile.view.ep.data.DisplayContent
import com.elmacentemobile.view.ep.data.DynamicData
import com.elmacentemobile.view.ep.data.ReceiptList

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