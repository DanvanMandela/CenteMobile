package com.craft.silicon.centemobile.view.ep.data

open class AppData

data class HeaderData(
    val cardData: MutableList<CardData>
) : AppData()

data class CardData(var title: String)


