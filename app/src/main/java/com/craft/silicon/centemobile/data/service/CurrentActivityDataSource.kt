package com.craft.silicon.centemobile.data.service

import android.app.Activity
import androidx.lifecycle.LiveData

interface CurrentActivityDataSource {
    fun currentActivity(): Activity?

    fun currentLive(): LiveData<Activity?> {
        throw Exception("Not implemented")
    }

    fun saveCurrentActivity() {
        throw Exception("Not implemented")
    }

    fun removeCurrentActivity() {
        throw Exception("Not implemented")
    }
}