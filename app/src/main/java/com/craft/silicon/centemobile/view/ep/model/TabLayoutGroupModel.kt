package com.craft.silicon.centemobile.view.ep.model

import android.os.Handler
import android.os.Looper
import androidx.databinding.ViewDataBinding
import com.airbnb.epoxy.*
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.source.pref.StorageDataSource
import com.craft.silicon.centemobile.databinding.BlockTabGroupLayoutBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.setChildren
import com.craft.silicon.centemobile.view.ep.controller.LinkedVault
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson


@EpoxyModelClass
open class TabLayoutGroupModel : DataBindingEpoxyModel() {

    @EpoxyAttribute
    lateinit var data: LinkedVault

    @EpoxyAttribute
    lateinit var storage: StorageDataSource

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var callbacks: AppCallbacks
    override fun getDefaultLayout(): Int = R.layout.block_tab_group_layout

    override fun setDataBindingVariables(binding: ViewDataBinding?) {
        (binding as? BlockTabGroupLayoutBinding)?.apply {
            addChildren(this)
        }
    }

    private fun addChildren(binding: BlockTabGroupLayoutBinding) {
        binding.parent.removeAllTabs()
        val tab = binding.parent
        onSelectItem(tab, binding.childContainer)
        data.children.forEachIndexed { index, formControl ->
            val item = tab.newTab().setText(formControl.controlText)
            item.tag = "${formControl.controlID},${formControl.serviceParamID}"
            item.id = index
            tab.addTab(item)

            if (formControl == data.children.first()) {
                item.select()
            }
        }


    }


    private fun onSelectItem(tab: TabLayout, binding: EpoxyRecyclerView) {
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                callbacks.clearInputData()
                val tag = tab?.tag.toString().split(",")
                val linked =
                    data.mainData.forms.form?.filter { a ->
                        a.linkedToControl == tag[0]
                    }
                AppLogger.instance.appLog("TAB:Data", Gson().toJson(data))
                AppLogger.instance.appLog("TAB:Linked", Gson().toJson(linked))
                AppLogger.instance.appLog("TAB:Tag", Gson().toJson(tag))
                AppLogger.instance.appLog("TAB:Storage", Gson().toJson(storage))

                callbacks.userInput(
                    InputData(
                        name = "tab",
                        key = tag[1],
                        value = tag[0],
                        encrypted = false,
                        mandatory = true
                    )
                )


                if (linked!!.isNotEmpty())
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.setChildren(
                            callbacks = callbacks,
                            dynamic = GroupForm(
                                module = data.mainData.forms.module,
                                form = linked.toMutableList()
                            ),
                            storage = storage
                        )
                    }, 40)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

}

fun TypedEpoxyController<*>.tabLayoutGroup(
    vault: LinkedVault,
    appCallbacks: AppCallbacks,
    storage: StorageDataSource
) {
    tabLayoutGroup {
        id(vault.container.controlID)
        data(vault)
        callbacks(appCallbacks)
        storage(storage)
    }
}