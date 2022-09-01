package com.craft.silicon.centemobile.data.source.pref

import android.content.Context
import android.content.SharedPreferences
import com.craft.silicon.centemobile.data.model.DeviceData
import com.craft.silicon.centemobile.data.model.DeviceDataTypeConverter
import com.craft.silicon.centemobile.data.model.converter.*
import com.craft.silicon.centemobile.data.model.static_data.OnlineAccountProduct
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.data.model.user.*
import com.craft.silicon.centemobile.data.source.sync.SyncData
import com.craft.silicon.centemobile.data.source.sync.SyncDataTypeConverter
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.view.fragment.go.OnTheGoConverter
import com.craft.silicon.centemobile.view.fragment.go.steps.*
import com.craft.silicon.centemobile.view.fragment.map.MapData
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


    override fun clearDevice() {
        setActivated(false)
        deleteActivationData()
        deleteStaticData()
        deleteNOK()
        deleteAddress()
        deleteCustomerProduct()
        deleteIDDetails()
        deleteParentDetails()
        deleteIncomeSource()
        deleteCustomerProduct()
        deleteOtherServices()
        deleteRecommendState()
        deleteBeneficiary()
        deleteAccounts()
        deleteAlerts()
    }

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
            BaseClass.decrypt(
                sharedPreferences.getString(
                    TAG_DEVICE_DATA, ""
                )
            )
        )
    )
    override val deviceData: StateFlow<DeviceData?>
        get() = _deviceData


    override fun setDeviceData(value: DeviceData) {
        _deviceData.value = value
        with(sharedPreferences.edit()) {
            putString(TAG_DEVICE_DATA, BaseClass.encrypt(DeviceDataTypeConverter().from(value)))
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
            putString(
                TAG_ACTIVATION_DATA,
                BaseClass.encrypt(ActivationDataTypeConverter().from(value))
            )
            apply()
        }
    }

    override fun deleteActivationData() {
        _activationData.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_ACTIVATION_DATA)
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


    private val _version = MutableStateFlow(sharedPreferences.getString(TAG_VERSION, ""))
    override val version: StateFlow<String?>
        get() = _version

    override fun setVersion(value: String) {
        _version.value = value
        with(sharedPreferences.edit()) {
            putString(TAG_VERSION, value)
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

    private val _staticData =
        MutableStateFlow(
            StaticDataDetailTypeConverter().from(
                sharedPreferences.getString(
                    STATIC_DATA,
                    ""
                )
            )
        )
    override val staticData: MutableStateFlow<List<StaticDataDetails?>?>
        get() = _staticData

    override fun setStaticData(value: MutableList<StaticDataDetails>) {
        _staticData.value = value.toMutableList()
        with(sharedPreferences.edit()) {
            putString(STATIC_DATA, StaticDataDetailTypeConverter().to(value))
            apply()
        }
    }

    override fun deleteStaticData() {
        _staticData.value = null
        with(sharedPreferences.edit()) {
            remove(STATIC_DATA)
        }
    }

    private val _beneficiary =
        MutableStateFlow(
            BeneficiaryTypeConverter().from(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        BENEFICIARY_DATA,
                        ""
                    )
                )
            )
        )
    override val beneficiary: StateFlow<List<Beneficiary?>?>
        get() = _beneficiary

    override fun setBeneficiary(value: MutableList<Beneficiary>) {
        _beneficiary.value = value.toMutableList()
        with(sharedPreferences.edit()) {
            putString(BENEFICIARY_DATA, BaseClass.encrypt(BeneficiaryTypeConverter().to(value)))
            apply()
        }
    }

    override fun deleteBeneficiary() {
        _beneficiary.value = null
        with(sharedPreferences.edit()) {
            remove(BENEFICIARY_DATA)
        }
    }

    private val _accounts =
        MutableStateFlow(
            AccountsTypeConverter().from(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        ACCOUNTS_DATA,
                        ""
                    )
                )
            )
        )

    override val accounts: StateFlow<List<Accounts?>?>
        get() = _accounts

    override fun setAccounts(value: MutableList<Accounts>) {
        _accounts.value = value.toMutableList()
        with(sharedPreferences.edit()) {
            putString(ACCOUNTS_DATA, BaseClass.encrypt(AccountsTypeConverter().to(value)))
            apply()
        }
    }

    override fun deleteAccounts() {
        _accounts.value = null
        with(sharedPreferences.edit()) {
            remove(ACCOUNTS_DATA)
        }
    }

    private val _alerts =
        MutableStateFlow(
            AlertsTypeConverter().from(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        ALERTS_DATA,
                        ""
                    )
                )
            )
        )
    override val alerts: StateFlow<List<AlertServices?>?>
        get() = _alerts

    override fun setAlerts(value: MutableList<AlertServices>) {
        _alerts.value = value.toMutableList()
        with(sharedPreferences.edit()) {
            putString(ALERTS_DATA, BaseClass.encrypt(AlertsTypeConverter().to(value)))
            apply()
        }
    }

    override fun deleteAlerts() {
        _alerts.value = null
        with(sharedPreferences.edit()) {
            remove(ALERTS_DATA)
        }
    }

    private val _latLng =
        MutableStateFlow(
            MapDataTypeConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        LAT_LNG_DATA,
                        "latitude:0.0," + "longitude:0.0"
                    )
                )
            )
        )

    override val latLng: StateFlow<MapData?>
        get() = _latLng

    override fun setLatLng(value: MapData) {
        _latLng.value = value
        with(sharedPreferences.edit()) {
            putString(LAT_LNG_DATA, BaseClass.encrypt(MapDataTypeConverter().from(value)))
            apply()
        }
    }

    private val _uniqueID =
        MutableStateFlow(
            sharedPreferences.getString(
                TAG_UNIQUE_ID,
                ""
            )
        )

    override val uniqueID: StateFlow<String?>
        get() = _uniqueID

    override fun setUniqueID(value: String) {
        _uniqueID.value = value
        with(sharedPreferences.edit()) {
            putString(TAG_UNIQUE_ID, value)
            apply()
        }
    }

    private val _accountProduct =
        MutableStateFlow(
            AccountProductTypeConverter().from(
                sharedPreferences.getString(
                    TAG_ACCOUNT_PRODUCT,
                    ""
                )
            )
        )

    override val productAccountData: StateFlow<List<OnlineAccountProduct?>?>
        get() = _accountProduct

    override fun setProductAccountData(value: MutableList<OnlineAccountProduct>) {
        _accountProduct.value = value
        with(sharedPreferences.edit()) {
            putString(TAG_ACCOUNT_PRODUCT, AccountProductTypeConverter().to(value))
            apply()
        }
    }

    private val _branchData =
        MutableStateFlow(
            AccountProductTypeConverter().from(
                sharedPreferences.getString(
                    TAG_BRANCH,
                    ""
                )
            )
        )

    override val branchData: StateFlow<List<OnlineAccountProduct?>?>
        get() = _branchData

    override fun setBranchData(value: MutableList<OnlineAccountProduct>) {
        _branchData.value = value
        with(sharedPreferences.edit()) {
            putString(TAG_BRANCH, AccountProductTypeConverter().to(value))
            apply()
        }
    }

    private val _recommend =
        MutableStateFlow(
            HearAboutStateConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        TAG_RECOMMEND_STATE,
                        ""
                    )
                )
            )
        )
    override val recommendState: StateFlow<HearAboutState?>
        get() = _recommend

    override fun setRecommendState(value: HearAboutState?) {
        _recommend.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_RECOMMEND_STATE,
                BaseClass.encrypt(HearAboutStateConverter().from(value))
            )
            apply()
        }
    }

    override fun deleteRecommendState() {
        _recommend.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_RECOMMEND_STATE)
        }
    }


    private val _onTheGoData =
        MutableStateFlow(
            OnTheGoConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        TAG_ON_THE_GO,
                        ""
                    )
                )
            )
        )


    private val _currentPosition =
        MutableStateFlow(
            sharedPreferences.getInt(
                TAG_ON_THE_GO_POSITION, 0
            )
        )

    override val currentPosition: StateFlow<Int?>
        get() = _currentPosition

    override fun deletePosition() {
        _currentPosition.value = 0
        with(sharedPreferences.edit()) {
            remove(TAG_ON_THE_GO_POSITION)
        }
    }

    override fun setCurrentPosition(value: Int?) {
        _currentPosition.value = value!!
        with(sharedPreferences.edit()) {
            putInt(TAG_ON_THE_GO_POSITION, value)
            apply()
        }
    }

    private val _addressState =
        MutableStateFlow(
            AddressStateTypeConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        TAG_ADDRESS_STATE,
                        ""
                    )
                )
            )
        )

    override val addressState: StateFlow<AddressState?>
        get() = _addressState

    override fun setAddressState(value: AddressState?) {
        _addressState.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_ADDRESS_STATE,
                BaseClass.encrypt(AddressStateTypeConverter().from(value))
            )
            apply()
        }
    }

    override fun deleteAddress() {
        _addressState.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_ADDRESS_STATE)
        }
    }

    private val _idDetails =
        MutableStateFlow(
            IDDetailsConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        TAG_ID_DETAILS,
                        ""
                    )
                )
            )
        )
    override val onIDDetails: StateFlow<IDDetails?>
        get() = _idDetails

    override fun setIDDetails(value: IDDetails?) {
        _idDetails.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_ID_DETAILS,
                BaseClass.encrypt(IDDetailsConverter().from(value))
            )
            apply()
        }
    }

    override fun deleteIDDetails() {
        _idDetails.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_ID_DETAILS)
        }
    }

    private val _parentDetails =
        MutableStateFlow(
            ParentDetailsConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        TAG_PARENT_DETAILS,
                        ""
                    )
                )
            )
        )
    override val parentDetails: StateFlow<ParentDetails?>
        get() = _parentDetails

    override fun setParentDetails(value: ParentDetails?) {
        _parentDetails.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_PARENT_DETAILS,
                BaseClass.encrypt(ParentDetailsConverter().from(value))
            )
            apply()
        }
    }

    override fun deleteParentDetails() {
        _parentDetails.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_PARENT_DETAILS)
        }
    }

    private val _customerProduct =
        MutableStateFlow(
            CustomerProductConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        TAG_PRODUCT,
                        ""
                    )
                )
            )
        )

    override fun setCustomerProduct(value: CustomerProduct?) {
        _customerProduct.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_PRODUCT,
                BaseClass.encrypt(CustomerProductConverter().from(value))
            )
            apply()
        }
    }

    override val customerProduct: StateFlow<CustomerProduct?>
        get() = _customerProduct

    override fun deleteCustomerProduct() {
        _customerProduct.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_PRODUCT)
        }
    }

    private val _incomeSource =
        MutableStateFlow(
            IncomeDataConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        TAG_INCOME_SOURCE,
                        ""
                    )
                )
            )
        )
    override val incomeSource: StateFlow<IncomeData?>
        get() = _incomeSource

    override fun setIncomeSource(value: IncomeData?) {
        _incomeSource.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_INCOME_SOURCE,
                BaseClass.encrypt(IncomeDataConverter().from(value))
            )
            apply()
        }
    }

    override fun deleteIncomeSource() {
        _incomeSource.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_INCOME_SOURCE)
        }
    }

    private val _nextKinData =
        MutableStateFlow(
            NextKinDataConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        TAG_NOK,
                        ""
                    )
                )
            )
        )
    override val nextOfKin: StateFlow<NextKinData?>
        get() = _nextKinData

    override fun setNKData(value: NextKinData?) {
        _nextKinData.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_NOK,
                BaseClass.encrypt(NextKinDataConverter().from(value))
            )
            apply()
        }
    }

    override fun deleteNOK() {
        _nextKinData.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_NOK)
        }
    }

    private val _otherServices =
        MutableStateFlow(
            OtherServiceConverter().to(
                BaseClass.decrypt(
                    sharedPreferences.getString(
                        TAG_OTHER_SERVICE,
                        ""
                    )
                )
            )
        )

    override val otherServices: StateFlow<OtherServiceData?>
        get() = _otherServices

    override fun setOtherServices(value: OtherServiceData?) {
        _otherServices.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_OTHER_SERVICE,
                BaseClass.encrypt(OtherServiceConverter().from(value))
            )
            apply()
        }
    }

    override fun deleteOtherServices() {
        _otherServices.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_OTHER_SERVICE)
        }
    }

    private val _otpState =
        MutableStateFlow(
            sharedPreferences.getLong(
                TAG_OTP_STATE,
                0L
            )
        )
    override val otpState: StateFlow<Long?>
        get() = _otpState

    override fun setOTPState(value: Long?) {
        _otpState.value = value!!
        with(sharedPreferences.edit()) {
            putLong(
                TAG_OTP_STATE,
                value
            )
            apply()
        }
    }

    override fun deleteOTPState() {
        _otpState.value = 0L
        with(sharedPreferences.edit()) {
            remove(TAG_OTP_STATE)
        }
    }

    private val _bio =
        MutableStateFlow(
            sharedPreferences.getBoolean(
                TAG_BIO,
                false
            )
        )
    override val bio: StateFlow<Boolean?>
        get() = _bio

    override fun setBio(value: Boolean?) {
        _bio.value = value!!
        with(sharedPreferences.edit()) {
            putBoolean(
                TAG_BIO,
                value
            )
            apply()
        }
    }

    override fun deleteBio() {
        _bio.value = false
        with(sharedPreferences.edit()) {
            remove(TAG_BIO)
        }
    }

    private val _iv =
        MutableStateFlow(
            IVTypeConverter().to(
                sharedPreferences.getString(
                    TAG_IV,
                    ""
                )
            )
        )

    override fun setIv(value: IVData?) {
        _iv.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_IV,
                IVTypeConverter().from(value)
            )
            apply()
        }
    }

    override val iv: StateFlow<IVData?>
        get() = _iv

    private val _hiddenModule = MutableStateFlow(
        HiddenModules().from(
            sharedPreferences.getString(
                TAG_HIDDEN_MODULE,
                ""
            )
        )
    )
    override val hiddenModule: StateFlow<List<ModuleHide?>?>
        get() = _hiddenModule

    override fun setHiddenModule(value: List<ModuleHide?>) {
        _hiddenModule.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_HIDDEN_MODULE,
                HiddenModules().to(value)
            )
            apply()
        }
    }

    private val _disableModule = MutableStateFlow(
        DisableModulesConverter().from(
            sharedPreferences.getString(
                TAG_DISABLED_MODULE,
                ""
            )
        )
    )
    override val disableModule: StateFlow<List<ModuleDisable?>?>
        get() = _disableModule

    override fun setDisableModule(value: List<ModuleDisable?>) {
        _disableModule.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_DISABLED_MODULE,
                DisableModulesConverter().to(value)
            )
            apply()
        }
    }

    override fun setNotificationToken(value: String) {
        _pushToken.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_NOTIFICATION_TOKEN,
                value
            )
            apply()
        }
    }

    private val _pushToken = MutableStateFlow(
        sharedPreferences.getString(
            TAG_NOTIFICATION_TOKEN,
            ""
        )
    )
    override val notificationToken: StateFlow<String?>
        get() = _pushToken


    private val _otp = MutableStateFlow(
        sharedPreferences.getString(
            TAG_OTP,
            ""
        )
    )
    override val otp: StateFlow<String?>
        get() = _otp

    override fun setOtp(value: String?) {
        _otp.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_OTP,
                value
            )
            apply()
        }
    }

    override fun deleteOtp() {
        _otp.value = ""
        with(sharedPreferences.edit()) {
            remove(TAG_OTP)
        }
    }

    private val _timeout = MutableStateFlow(
        sharedPreferences.getLong(
            TAG_TIME_OUT,
            120000
        )
    )
    override val timeout: StateFlow<Long?>
        get() = _timeout

    override fun setTimeout(value: Long?) {
        _timeout.value = value!!
        with(sharedPreferences.edit()) {
            putLong(
                TAG_TIME_OUT,
                value
            )
            apply()
        }
    }

    override fun deleteTimeout() {
        _timeout.value = 0
        with(sharedPreferences.edit()) {
            remove(TAG_TIME_OUT)
        }
    }

    private val _loginTime = MutableStateFlow(
        sharedPreferences.getLong(
            TAG_LOGIN_TIME,
            0
        )
    )
    override val loginTime: StateFlow<Long?>
        get() = _loginTime

    override fun setLoginTime(value: Long) {
        _loginTime.value = value
        with(sharedPreferences.edit()) {
            putLong(
                TAG_LOGIN_TIME,
                value
            )
            apply()
        }
    }

    private val _sync = MutableStateFlow(
        SyncDataTypeConverter().to(
            sharedPreferences.getString(
                TAG_SYNC,
                ""
            )
        )
    )

    override val sync: StateFlow<SyncData?>
        get() = _sync

    override fun setSync(value: SyncData?) {
        _sync.value = value
        with(sharedPreferences.edit()) {
            putString(
                TAG_SYNC,
                SyncDataTypeConverter().from(value)
            )
            apply()
        }
    }

    override fun deleteSync() {
        _sync.value = null
        with(sharedPreferences.edit()) {
            remove(TAG_SYNC)
        }
    }

    private val _feedbackTimer = MutableStateFlow(
        sharedPreferences.getInt(
            TAG_FEED_BACK_TIMER,
            0
        )
    )
    override val feedbackTimer: StateFlow<Int?>
        get() = _feedbackTimer

    override fun setFeedbackTimer(value: Int?) {
        _feedbackTimer.value = value!!
        with(sharedPreferences.edit()) {
            putInt(
                TAG_FEED_BACK_TIMER,
                value
            )
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
        private const val TAG_VERSION = "version"
        private const val STATIC_DATA = "staticSyncData"
        private const val BENEFICIARY_DATA = "beneficiary"
        private const val ACCOUNTS_DATA = "accounts"
        private const val ALERTS_DATA = "alerts"
        private const val LAT_LNG_DATA = "latLng"
        private const val TAG_UNIQUE_ID = "uniqueID"
        private const val TAG_ACCOUNT_PRODUCT = "accountProduct"
        private const val TAG_BRANCH = "branch"

        private const val TAG_RECOMMEND_STATE = "recommend"
        private const val TAG_ON_THE_GO = "onTheGo"
        private const val TAG_ON_THE_GO_POSITION = "onTheGoPos"
        private const val TAG_ADDRESS_STATE = "addressState"

        private const val TAG_ID_DETAILS = "idDetails"

        private const val TAG_PARENT_DETAILS = "parentDetails"

        private const val TAG_PRODUCT = "customerProduct"

        private const val TAG_INCOME_SOURCE = "incomeData"

        private const val TAG_NOK = "nextKinData"

        private const val TAG_OTHER_SERVICE = "otherService"

        private const val TAG_OTP_STATE = "otpState"

        private const val TAG_BIO = "bio"

        private const val TAG_IV = "iv"

        private const val TAG_HIDDEN_MODULE = "hiddenModule"

        private const val TAG_NOTIFICATION_TOKEN = "pushToken"

        private const val TAG_DISABLED_MODULE = "hiddenModule"

        private const val TAG_OTP = "otpTag"

        private const val TAG_TIME_OUT = "timeout"

        private const val TAG_SYNC = "sync"

        private const val TAG_LOGIN_TIME = "login_time"

        private const val TAG_FEED_BACK_TIMER = "feed_back_timer"
    }
}