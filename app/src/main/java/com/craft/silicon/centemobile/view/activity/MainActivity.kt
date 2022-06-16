package com.craft.silicon.centemobile.view.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.DeviceData
import com.craft.silicon.centemobile.data.model.DeviceDataTypeConverter
import com.craft.silicon.centemobile.data.source.remote.callback.RequestData
import com.craft.silicon.centemobile.databinding.ActivityMainBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.model.DynamicViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppCallbacks, NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: DynamicViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setViewModel()
        setNavigation()
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
    }

    fun provideNavigationGraph(): NavController {
        return navController!!
    }

    override fun setViewModel() {
        val jsonObject = JSONObject()
        jsonObject.put("FORMID", "O-GetLatestVersion")
        jsonObject.put("UNIQUEID", "wtgsgfsfsfsfsffsspo")
        jsonObject.put("CUSTOMERID", "16")
        jsonObject.put("VersionNumber", "28")
        jsonObject.put("IMEI", BaseClass.encrypt("45687555"))
        jsonObject.put("IMSI", BaseClass.encrypt("45687555"))
        jsonObject.put("TRXSOURCE", "APP")
        jsonObject.put("APPNAME", "CENTE")
        jsonObject.put("CODEBASE", "ANDROID")
        jsonObject.put(
            "LATLON",
            "0.0" + "," + "0.0"
        )
        val newRequest = jsonObject.toString()

        jsonObject.put("rashi", BaseClass.hashLatest(newRequest))
        jsonObject.put("MobileNumber", "2500116")
        jsonObject.put("CodeBase", "ANDROID")
        jsonObject.put("lat", "0.0")
        jsonObject.put("long", "0.0")
        jsonObject.put("UniqueId", "eqrwfdgdgdhdhd")
        jsonObject.put("Appname", "CENTE")

        subscribe.add(
            viewModel.requestBase(
                RequestData(
                    uniqueId = "87654321",
                    mobileNumber = "25400116",
                    device = "12345678",
                    codeBase = "ANDROID",
                    lat = "0.0",
                    long = "0.0",
                    rashi = BaseClass.hashLatest(newRequest), appName = "CENTE"
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.respCode == "000") {
                        var keys = ""
                        val device = it.payload?.device
                        val vice: CharArray = device!!.toCharArray()

                        for (i in it.data!!.toIntArray()) {
                            keys += vice[i]
                        }

                        val data = DeviceDataTypeConverter().to(
                            BaseClass.decryptLatest(
                                it.payload?.Routes,
                                keys, false, it.payload?.ran
                            )
                        )
                        data?.token = it.token!!
                        viewModel.saveDeviceData(data)
                    }
                },
                    { Log.e(this@MainActivity.javaClass.simpleName, it.localizedMessage!!) })
        )
    }

    private fun setNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment?
        if (navHostFragment != null) {
            navController = navHostFragment.navController
            navController?.addOnDestinationChangedListener(this)
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {

    }
}