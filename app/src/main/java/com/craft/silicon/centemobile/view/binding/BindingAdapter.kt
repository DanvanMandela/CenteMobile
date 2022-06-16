package com.craft.silicon.centemobile.view.binding

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import com.airbnb.epoxy.EpoxyRecyclerView
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.controller.HeaderController
import com.craft.silicon.centemobile.view.ep.data.HeaderData
import com.google.android.material.appbar.MaterialToolbar

@BindingAdapter("callback", "controller")
fun EpoxyRecyclerView.setHeader(callbacks: AppCallbacks, data: HeaderData) {
    val controller = HeaderController(callbacks)
    controller.setData(data)
    this.setController(controller)
}

@BindingAdapter("textSet")
fun TextView.textSet(@StringRes text: String?) {
    if (text != null) {
        this.text = text
    }
}

@BindingAdapter("callback")
fun MaterialToolbar.setToolbar(callbacks: AppCallbacks) {


}






