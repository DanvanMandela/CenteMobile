package com.craft.silicon.centemobile.view.ep.data

import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.Accounts
import com.craft.silicon.centemobile.data.model.user.FrequentModules

open class AppData

data class HeaderData(
    val account: MutableList<Accounts>
) : AppData()


data class BodyData(
    val module: MutableList<Modules>,
    val frequentList: MutableList<FrequentModules>
) : AppData()


sealed class Body





