package com.craft.silicon.centemobile.data.source.pref

import android.content.SharedPreferences
import com.craft.silicon.centemobile.data.model.DeviceData
import com.craft.silicon.centemobile.data.model.static_data.OnlineAccountProduct
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.data.model.user.Accounts
import com.craft.silicon.centemobile.data.model.user.ActivationData
import com.craft.silicon.centemobile.data.model.user.AlertServices
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.craft.silicon.centemobile.view.fragment.map.MapData
import com.google.android.gms.maps.model.LatLng
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


    fun setActivationData(value: ActivationData)
    val activationData: StateFlow<ActivationData?>

    fun setActivated(value: Boolean)
    val isActivated: StateFlow<Boolean?>


    fun setVersion(value: String)
    val version: StateFlow<String?>

    fun setStaticData(value: MutableList<StaticDataDetails>)
    val staticData: StateFlow<List<StaticDataDetails?>?>

    fun setBeneficiary(value: MutableList<Beneficiary>)
    val beneficiary: StateFlow<List<Beneficiary?>?>

    fun setAccounts(value: MutableList<Accounts>)
    val accounts: StateFlow<List<Accounts?>?>

    fun setAlerts(value: MutableList<AlertServices>)
    val alerts: StateFlow<List<AlertServices?>?>


    fun setLatLng(value: MapData)
    val latLng: StateFlow<MapData?>

    fun setUniqueID(value: String)
    val uniqueID: StateFlow<String?>

    fun setProductAccountData(value: MutableList<OnlineAccountProduct>)
    val productAccountData: StateFlow<List<OnlineAccountProduct?>?>


    fun setBranchData(value: MutableList<OnlineAccountProduct>)
    val branchData: StateFlow<List<OnlineAccountProduct?>?>
}