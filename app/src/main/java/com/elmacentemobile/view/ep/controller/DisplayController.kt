package com.elmacentemobile.view.ep.controller

import android.os.Parcelable
import com.airbnb.epoxy.TypedEpoxyController
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.ep.model.displayModel
import kotlinx.parcelize.Parcelize

class DisplayController(val callbacks: AppCallbacks) :
    TypedEpoxyController<DisplayVaultData>() {

    override fun buildModels(data: DisplayVaultData?) {
        if (data != null) {
            for (s in data.data!!) {
                displayModel(
                    vault = s,
                    appCallbacks = this@DisplayController.callbacks
                )
            }
        }
    }
}


@Parcelize
data class DisplayVault(
    var parent: FormControl,
    var children: MutableList<HashMap<String, String>>?
) : Parcelable


data class DisplayVaultData(
    val data: MutableList<DisplayVault>?
)

