package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.labelLayout
import com.craft.silicon.centemobile.loadingStateLayout
import com.craft.silicon.centemobile.nothingLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.craft.silicon.centemobile.view.ep.data.Nothing
import com.craft.silicon.centemobile.view.ep.model.mainDisplayLay

class MainDisplayController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AppData>() {
    override fun buildModels(data: AppData?) {
        if (data != null)
            when (data) {
                is DisplayData -> displayState(data)
                is Nothing -> nothingLayout { id("nothing") }
                is LoadingState -> loadingStateLayout { id("Loading") }
                is LabelData -> labelLayout {
                    id("label")
                    value(data.value)
                }
            }
    }

    private fun displayState(data: DisplayData?) {
        for (s in data?.display!!) {
            mainDisplayLay(s, data.form, data.modules, this@MainDisplayController.callbacks)
        }
    }

}


data class DisplayData(
    val display: MutableList<HashMap<String, String>>?,
    val form: FormControl?,
    val modules: Modules?
) : AppData()

data class DisplayState(val id: String, val data: DisplayData?)

open class LoadingState : AppData()


open class LabelData(val value: String?) : AppData()


