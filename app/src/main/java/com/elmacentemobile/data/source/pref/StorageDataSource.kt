package com.elmacentemobile.data.source.pref

import android.content.SharedPreferences
import com.elmacentemobile.data.model.CarouselData
import com.elmacentemobile.data.model.DeviceData
import com.elmacentemobile.data.model.converter.IVData
import com.elmacentemobile.data.model.static_data.OnlineAccountProduct
import com.elmacentemobile.data.model.static_data.StaticDataDetails
import com.elmacentemobile.data.model.user.*
import com.elmacentemobile.data.source.sync.SyncData
import com.elmacentemobile.view.dialog.DayTipData
import com.elmacentemobile.view.fragment.go.steps.*
import com.elmacentemobile.view.fragment.map.MapData
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
    fun removeHiddenModule()

    fun setDisableModule(value: List<ModuleDisable?>)
    val disableModule: StateFlow<List<ModuleDisable?>?>
    fun removeDisableModule()

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


    fun setTimeout(value: Long?)
    val timeout: StateFlow<Long?>
    fun deleteTimeout()

    fun setSync(value: SyncData?)
    val sync: StateFlow<SyncData?>
    fun deleteSync()

    fun setFeedbackTimer(value: Int?)
    val feedbackTimer: StateFlow<Int?>

    fun setFeedbackTimerMax(value: Int?)
    val feedbackTimerMax: StateFlow<Int?>


    val customerID: StateFlow<String?>


    val phoneCustomer: StateFlow<String?>

    fun setInactivity(value: Boolean?)
    val inActivity: StateFlow<Boolean?>

    fun activity(value: String?)
    val activity: StateFlow<String?>

    fun passwordType(value: String?)
    val passwordType: StateFlow<String?>


    fun forceData(value: Boolean?)
    val forceData: StateFlow<Boolean?>

    fun carouselData(value: List<CarouselData>?)
    val carouselData: StateFlow<List<CarouselData>?>

    fun dayTipData(value: List<DayTipData>?)
    val dayTipData: StateFlow<List<DayTipData>?>

}