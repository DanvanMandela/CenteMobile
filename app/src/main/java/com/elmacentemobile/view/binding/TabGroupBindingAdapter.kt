package com.elmacentemobile.view.binding

import androidx.databinding.BindingAdapter
import com.elmacentemobile.data.model.control.FormControl
import com.google.android.material.tabs.TabItem

@BindingAdapter("tabItem")
fun TabItem.setTabItem(data: FormControl?) {
    if (data != null) {
        this.tag = data.controlID
    }
}