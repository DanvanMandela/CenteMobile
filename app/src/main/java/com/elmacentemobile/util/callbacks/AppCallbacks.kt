package com.elmacentemobile.util.callbacks

import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.view.composable.keyboard.CustomKeyboardCallback
import com.elmacentemobile.view.dialog.DialogCallback
import com.elmacentemobile.view.ep.data.BusData

interface AppCallbacks : BaseView,
    DialogCallback,
    ModuleCallback,
    FormCallback,
    NavigationCallback,
    CustomKeyboardCallback

interface EventCallback {
    fun onEvent(modules: MutableList<Modules>) {
        throw Exception("Not implemented")
    }

    fun onEvent(modules: Modules) {
        throw Exception("Not implemented")
    }

    fun onEvent(bus: BusData) {
        throw Exception("Not implemented")
    }

    fun registerEvent()
    fun unregisterEvent()
}