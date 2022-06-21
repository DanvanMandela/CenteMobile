package com.craft.silicon.centemobile.data.source.pref

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.craft.silicon.centemobile.data.model.DeviceData
import com.craft.silicon.centemobile.data.model.DeviceDataTypeConverter
import com.craft.silicon.centemobile.data.model.converter.ActivationDataTypeConverter
import com.craft.silicon.centemobile.data.model.user.ActivationData
import com.craft.silicon.centemobile.util.BaseClass
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesStorage @Inject constructor(@ApplicationContext context: Context) :
    StorageDataSource {

    override val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    private val _login = MutableStateFlow(sharedPreferences.getBoolean(TAG_LOGIN, false))
    override val login: StateFlow<Boolean>
        get() = _login

    override fun setLogin(value: Boolean) {
        _login.value = value
        with(sharedPreferences.edit()) {
            putBoolean(TAG_LOGIN, value)
            apply()
        }
    }

    private val _token = MutableStateFlow(sharedPreferences.getString(TAG_TOKEN, ""))
    override val token: StateFlow<String?>
        get() = _token

    override fun setToken(value: String) {
        _token.value = value
        with(sharedPreferences.edit()) {
            putString(TAG_TOKEN, value)
            apply()
        }
    }

    private val _firstRun = MutableStateFlow(sharedPreferences.getBoolean(TAG_FIRST_RUN, true))
    override val firstRun: StateFlow<Boolean?>
        get() = _firstRun

    override fun setFirstRun(value: Boolean) {
        _firstRun.value = value
        with(sharedPreferences.edit()) {
            putBoolean(TAG_FIRST_RUN, value)
            apply()
        }
    }

    private val _deviceData = MutableStateFlow(
        DeviceDataTypeConverter().to(
            sharedPreferences.getString(
                TAG_DEVICE_DATA, ""
            )
        )
    )
    override val deviceData: StateFlow<DeviceData?>
        get() = _deviceData


    override fun setDeviceData(value: DeviceData) {

        _deviceData.value = value
        with(sharedPreferences.edit()) {
            putString(TAG_DEVICE_DATA, DeviceDataTypeConverter().from(value))
            apply()
        }
    }

    private val _activationData = MutableStateFlow(
        ActivationDataTypeConverter().to(
            BaseClass.decrypt(
                sharedPreferences.getString(
                    TAG_ACTIVATION_DATA, ""
                )
            )
        )
    )

    override fun setActivationData(value: ActivationData) {
        _activationData.value = value
        with(sharedPreferences.edit()) {
            putString(TAG_DEVICE_DATA, BaseClass.encrypt(ActivationDataTypeConverter().from(value)))
            apply()
        }
    }

    override val activationData: StateFlow<ActivationData?>
        get() = _activationData


    private val _isActivated = MutableStateFlow(sharedPreferences.getBoolean(TAG_ACTIVATED, false))
    override val isActivated: StateFlow<Boolean?>
        get() = _isActivated

    override fun setActivated(value: Boolean) {
        _isActivated.value = value
        with(sharedPreferences.edit()) {
            putBoolean(TAG_ACTIVATED, value)
            apply()
        }
    }


    private val _userId = MutableStateFlow(sharedPreferences.getString(TAG_USER_ID, ""))
    override val userId: StateFlow<String?>
        get() = _userId

    override fun setUserId(value: String) {
        _userId.value = value
        with(sharedPreferences.edit()) {
            putString(TAG_USER_ID, value)
            apply()
        }
    }


    companion object {
        private const val SHARED_PREF_NAME = "pref"
        private const val TAG_LOGIN = "auth"
        private const val TAG_TOKEN = "token"
        private const val TAG_USER_ID = "userId"
        private const val TAG_FIRST_RUN = "firstRun"
        private const val TAG_DEVICE_DATA = "deviceData"
        private const val TAG_ACTIVATION_DATA = "activeData"
        private const val TAG_ACTIVATED = "activated"
    }
}