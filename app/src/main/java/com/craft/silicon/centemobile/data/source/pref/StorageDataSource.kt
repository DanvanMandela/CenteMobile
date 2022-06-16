package com.craft.silicon.centemobile.data.source.pref

import android.content.SharedPreferences
import com.craft.silicon.centemobile.data.model.DeviceData
import kotlinx.coroutines.flow.StateFlow

interface StorageDataSource {
    val sharedPreferences: SharedPreferences
    fun setLogin(value: Boolean)
    val login: StateFlow<Boolean>
    fun setToken(value: String)
    val token: StateFlow<String?>
    fun setUserId(value: String)
    val userId: StateFlow<String?>
    fun setFirstRun(value: Boolean)
    val firstRun: StateFlow<Boolean?>

    fun setDeviceData(value: DeviceData)
    val deviceData: StateFlow<DeviceData?>

}