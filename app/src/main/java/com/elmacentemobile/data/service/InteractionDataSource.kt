package com.elmacentemobile.data.service

interface InteractionDataSource {
    fun onUserInteracted() {
        throw Exception("Not implemented")
    }

    fun setTimer() {
        throw Exception("Not implemented")
    }
}