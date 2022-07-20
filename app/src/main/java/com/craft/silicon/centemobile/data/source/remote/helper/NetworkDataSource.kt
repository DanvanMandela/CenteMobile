package com.craft.silicon.centemobile.data.source.remote.helper

interface NetworkDataSource {
    fun connection(networkIsh: NetworkIsh)
    fun enable()
    fun disable()
    fun activeNetwork()
}