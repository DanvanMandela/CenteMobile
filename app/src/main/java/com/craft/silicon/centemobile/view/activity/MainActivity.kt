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
import com.craft.silicon.centemobile.data.model.DeviceDataTypeConverter
import com.craft.silicon.centemobile.databinding.ActivityMainBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.fragment.home.HomeFragment
import com.craft.silicon.centemobile.view.model.DynamicViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.craft.silicon.centemobile.view.model.WorkerViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppCallbacks, NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: DynamicViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private var navController: NavController? = null
    private val workViewModel: WorkerViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setViewModel()
        setNavigation()
    }


    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        HomeFragment.newInstance(this)
    }

    fun provideNavigationGraph(): NavController {
        return navController!!
    }

    override fun setViewModel() {
        subscribe.add(
            viewModel.getBaseData(
                this
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
                        data?.run = it.payload!!.ran
                        data?.device = keys
                        viewModel.saveDeviceData(data)
                        workViewModel.onWidgetData()
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
