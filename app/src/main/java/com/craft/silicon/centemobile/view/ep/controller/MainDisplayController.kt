package com.craft.silicon.centemobile.view.ep.controller

import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.loadingStateLayout
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.craft.silicon.centemobile.view.ep.model.mainDisplayLay

class MainDisplayController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AppData>() {
    override fun buildModels(data: AppData?) {
        when (data) {
            is DisplayData -> displayState(data)
            is LoadingState -> loadingStateLayout { id("Loading") }
        }

    }

    private fun displayState(data: DisplayData?) {
        for (s in data?.display!!) {
            mainDisplayLay(s, this@MainDisplayController.callbacks)
        }
    }

}


data class DisplayData(val display: MutableList<HashMap<String, String>>?) : AppData()

data class DisplayState(val id: String, val data: DisplayData?)

open class LoadingState : AppData()


