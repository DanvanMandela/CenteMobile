package com.craft.silicon.centemobile.view.ep.data

import android.os.Parcelable
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.Accounts
import com.craft.silicon.centemobile.data.model.user.FrequentModules
import kotlinx.parcelize.Parcelize

open class AppData

data class HeaderData(
    val account: MutableList<Accounts>
) : AppData()


data class BodyData(
    val module: MutableList<Modules>,
    val frequentList: MutableList<FrequentModules>
) : AppData()


@Parcelize
open class DynamicData : Parcelable

data class GroupModule(val parent: Modules, val module: MutableList<Modules>) : DynamicData()
data class GroupForm(val module: Modules, val form: MutableList<FormControl>) : DynamicData()





