package com.craft.silicon.centemobile.data.source.pref

import android.content.SharedPreferences
import com.craft.silicon.centemobile.data.model.DeviceData
import com.craft.silicon.centemobile.data.model.converter.IVData
import com.craft.silicon.centemobile.data.model.static_data.OnlineAccountProduct
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.data.model.user.*
import com.craft.silicon.centemobile.data.source.sync.SyncData
import com.craft.silicon.centemobile.view.fragment.go.steps.*
import com.craft.silicon.centemobile.view.fragment.map.MapData
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

    fun setLoginTime(value: Long)
    val loginTime: StateFlow<Long?>

    fun setDeviceData(value: DeviceData)
    val deviceData: StateFlow<DeviceData?>

    fun setHiddenModule(value: List<ModuleHide?>)
    val hiddenModule: StateFlow<List<ModuleHide?>?>

    fun setDisableModule(value: List<ModuleDisable?>)
    val disableModule: StateFlow<List<ModuleDisable?>?>

    fun clearDevice()


    fun setActivationData(value: ActivationData)
    val activationData: StateFlow<ActivationData?>
    fun deleteActivationData()

    fun setActivated(value: Boolean)
    val isActivated: StateFlow<Boolean?>


    fun setVersion(value: String)
    val version: StateFlow<String?>

    fun setStaticData(value: MutableList<StaticDataDetails>)
    val staticData: StateFlow<List<StaticDataDetails?>?>
    fun deleteStaticData()

    fun setBeneficiary(value: MutableList<Beneficiary>)
    val beneficiary: StateFlow<List<Beneficiary?>?>
    fun deleteBeneficiary()

    fun setAccounts(value: MutableList<Accounts>)
    val accounts: StateFlow<List<Accounts?>?>
    fun deleteAccounts()

    fun setAlerts(value: MutableList<AlertServices>)
    val alerts: StateFlow<List<AlertServices?>?>
    fun deleteAlerts()


    fun setLatLng(value: MapData)
    val latLng: StateFlow<MapData?>

    fun setUniqueID(value: String)
    val uniqueID: StateFlow<String?>

    fun setProductAccountData(value: MutableList<OnlineAccountProduct>)
    val productAccountData: StateFlow<List<OnlineAccountProduct?>?>


    fun setBranchData(value: MutableList<OnlineAccountProduct>)
    val branchData: StateFlow<List<OnlineAccountProduct?>?>

    fun setRecommendState(value: HearAboutState?)
    val recommendState: StateFlow<HearAboutState?>
    fun deleteRecommendState()


    fun setAddressState(value: AddressState?)
    val addressState: StateFlow<AddressState?>
    fun deleteAddress()


    fun setCurrentPosition(value: Int?)
    val currentPosition: StateFlow<Int?>
    fun deletePosition()

    fun setIDDetails(value: IDDetails?)
    val onIDDetails: StateFlow<IDDetails?>
    fun deleteIDDetails()

    fun setParentDetails(value: ParentDetails?)
    val parentDetails: StateFlow<ParentDetails?>
    fun deleteParentDetails()

    fun setCustomerProduct(value: CustomerProduct?)
    val customerProduct: StateFlow<CustomerProduct?>
    fun deleteCustomerProduct()

    fun setIncomeSource(value: IncomeData?)
    val incomeSource: StateFlow<IncomeData?>
    fun deleteIncomeSource()

    fun setOtherServices(value: OtherServiceData?)
    val otherServices: StateFlow<OtherServiceData?>
    fun deleteOtherServices()


    fun setNKData(value: NextKinData?)
    val nextOfKin: StateFlow<NextKinData?>
    fun deleteNOK()


    fun setOTPState(value: Long?)
    val otpState: StateFlow<Long?>
    fun deleteOTPState()

    fun setBio(value: Boolean?)
    val bio: StateFlow<Boolean?>
    fun deleteBio()

    fun setIv(value: IVData?)
    val iv: StateFlow<IVData?>


    fun setNotificationToken(value: String)
    val notificationToken: StateFlow<String?>


    fun setOtp(value: String?)
    val otp: StateFlow<String?>
    fun deleteOtp()


    fun setTimeout(value: Int?)
    val timeout: StateFlow<Int?>
    fun deleteTimeout()

    fun setSync(value: SyncData?)
    val sync: StateFlow<SyncData?>
    fun deleteSync()


}