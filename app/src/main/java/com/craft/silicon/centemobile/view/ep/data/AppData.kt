package com.craft.silicon.centemobile.view.ep.data

import com.craft.silicon.centemobile.data.model.module.Modules

open class AppData

data class HeaderData(
    val cardData: MutableList<CardData>
) : AppData()

data class CardData(var title: String)

data class BodyData(val body: MutableList<Body>) : AppData()

sealed class Body

data class ModuleData(var list: MutableList<Modules>) : Body()




