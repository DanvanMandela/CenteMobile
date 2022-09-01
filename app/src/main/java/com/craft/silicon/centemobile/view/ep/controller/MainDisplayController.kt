package com.craft.silicon.centemobile.view.ep.controller

import android.os.Parcelable
import android.text.TextUtils
import androidx.databinding.BindingAdapter
import androidx.room.TypeConverter
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.craft.silicon.centemobile.*
import com.craft.silicon.centemobile.data.model.StandingOrderList
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.craft.silicon.centemobile.data.model.user.DisplayHash
import com.craft.silicon.centemobile.data.model.user.PendingTransactionList
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.AppData
import com.craft.silicon.centemobile.view.ep.data.DisplayContent
import com.craft.silicon.centemobile.view.ep.data.Nothing
import com.craft.silicon.centemobile.view.ep.model.mainDisplayLay
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.util.*

class MainDisplayController(val callbacks: AppCallbacks) :
    TypedEpoxyController<AppData>() {
    override fun buildModels(data: AppData?) {
        if (data != null)
            when (data) {
                is DisplayData -> displayState(data)
                is Nothing -> noDataLayout { id(BaseClass.generateAlphaNumericString(10)) }
                is LoadingState -> loadingStateLayout { id(BaseClass.generateAlphaNumericString(10)) }
                is LabelData -> labelLayout {
                    id(BaseClass.generateAlphaNumericString(10))
                    value(data.value)
                }
                is StandingOrderList -> standingOrder(data)
                is BeneficiaryList -> setBeneficiary(data)
                is PendingTransactionList -> setPendingTransactions(data)
                is DisplayHash -> setDisplayHash(data)

            }
    }

    private fun setDisplayHash(data: DisplayHash) {
        for (e in data.list.entries) {
            if (!TextUtils.isEmpty(e.value) && BaseClass.nonCaps(e.key)
                != BaseClass.nonCaps("PendingUniqueID")
            ) {
                displayItemLayout {
                    id(BaseClass.generateAlphaNumericString(5))
                    data(DisplayContent(key = e.key, value = e.value))
                }
            }
        }
    }

    private fun setPendingTransactions(data: PendingTransactionList) {
        data.list?.forEach {
            pendingTransactionLayout {
                id("P-${BaseClass.generateAlphaNumericString(5)}")
                data(it)
                callback(this@MainDisplayController.callbacks)
            }
        }
    }

    private fun setBeneficiary(data: BeneficiaryList) {
        for (s in data.list) {
            beneficiaryItemLayout {
                id(BaseClass.generateAlphaNumericString(10))
                data(s)
                callback(this@MainDisplayController.callbacks)
                module(data.module)
            }
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


@Parcelize
data class BeneficiaryList(
    @field:SerializedName("list")
    @field:Expose
    val list: MutableList<Beneficiary?>,
    @field:SerializedName("module")
    @field:Expose
    val module: Modules?
) : AppData(), Parcelable


class HashTypeConverter {
    @TypeConverter
    fun from(data: String?): List<HashMap<String, String>>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<HashMap<String, String>>?>() {}.type
        return gsonBuilder.fromJson<List<HashMap<String, String>>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<HashMap<String, String>>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}

@BindingAdapter("pending", "callback")
fun EpoxyRecyclerView.setPending(appData: AppData?, callbacks: AppCallbacks) {
    val controller = MainDisplayController(callbacks)
    controller.setData(appData)
    this.setController(controller)
}



