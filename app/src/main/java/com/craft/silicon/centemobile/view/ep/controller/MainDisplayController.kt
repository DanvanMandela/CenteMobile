package com.craft.silicon.centemobile.view.ep.controller

import android.os.Parcelable
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.data.model.StandingOrderList
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.labelLayout
import com.craft.silicon.centemobile.loadingStateLayout
import com.craft.silicon.centemobile.nothingLayout
import com.craft.silicon.centemobile.standingOrderItemLayout
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.craft.silicon.centemobile.view.ep.data.Nothing
import com.craft.silicon.centemobile.view.ep.model.mainDisplayLay
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class MainDisplayController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AppData>() {
    override fun buildModels(data: AppData?) {
        if (data != null)
            when (data) {
                is DisplayData -> displayState(data)
                is Nothing -> nothingLayout { id("nothing") }
                is LoadingState -> loadingStateLayout { id("Loading") }
                is LabelData -> labelLayout {
                    id(BaseClass.generateAlphaNumericString(10))
                    value(data.value)
                }
                is StandingOrderList -> standingOrder(data)
            }
    }

    private fun standingOrder(data: StandingOrderList) {
        for (s in data.list!!) {
            standingOrderItemLayout {
                id(BaseClass.generateAlphaNumericString(10))
                data(s)
                form(data.formControl)
                module(data.module)
                callback(this@MainDisplayController.callbacks)
            }
        }
    }

    private fun displayState(data: DisplayData?) {
        for (s in data?.display!!) {
            mainDisplayLay(s, data.form, data.modules, this@MainDisplayController.callbacks)
        }
    }

}


@Parcelize
data class DisplayData(
    @field:SerializedName("data")
    @field:Expose
    val display: MutableList<HashMap<String, String>>?,
    @field:SerializedName("form")
    @field:Expose
    val form: FormControl?,
    @field:SerializedName("module")
    @field:Expose
    val modules: Modules?
) : AppData(), Parcelable

data class DisplayState(val id: String, val data: DisplayData?)

open class LoadingState : AppData()


open class LabelData(val value: String?) : AppData()


