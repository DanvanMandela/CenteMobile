package com.craft.silicon.centemobile.data.source.pref

import android.content.Context
import android.content.SharedPreferences
import com.craft.silicon.centemobile.data.model.DeviceData
import com.craft.silicon.centemobile.data.model.DeviceDataTypeConverter
import com.craft.silicon.centemobile.data.model.converter.*
import com.craft.silicon.centemobile.data.model.static_data.OnlineAccountProduct
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.data.model.user.Accounts
import com.craft.silicon.centemobile.data.model.user.ActivationData
import com.craft.silicon.centemobile.data.model.user.AlertServices
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.craft.silicon.centemobile.util.BaseClass
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
        private const val STATIC_DATA = "staticData"
        private const val BENEFICIARY_DATA = "beneficiary"
        private const val ACCOUNTS_DATA = "accounts"
        private const val ALERTS_DATA = "alerts"
        private const val LAT_LNG_DATA = "latLng"
        private const val TAG_UNIQUE_ID = "uniqueID"
        private const val TAG_ACCOUNT_PRODUCT = "accountProduct"
        private const val TAG_BRANCH = "branch"
    }
}