package com.elmacentemobile.util

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@SuppressLint("HardwareIds")
@Singleton
class SimUtil @Inject constructor(@ApplicationContext private val context: Context) : SimData {
    val phoneManger =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    override val countryISo: String?
        get() = phoneManger.simCountryIso
    override val operator: String?
        get() = phoneManger.simOperator
    override val operatorName: String?
        get() = phoneManger.simOperatorName
    override val serialNumber: String?
        get() = phoneManger.simSerialNumber
    override val state: Int
        get() = phoneManger.simState
    override val subscribeID: String?
        get() = phoneManger.subscriberId




}

interface SimData {
    val countryISo: String?
    val operator: String?
    val operatorName: String?
    val serialNumber: String?
    val state: Int?
    val subscribeID: String?
    fun lineOne(): String? {
        throw Exception("Not implemented")
    }

    fun lineTwo(): String? {
        throw Exception("Not implemented")
    }
}