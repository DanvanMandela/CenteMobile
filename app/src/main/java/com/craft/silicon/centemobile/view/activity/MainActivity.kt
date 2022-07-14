package com.craft.silicon.centemobile.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.DeviceDataTypeConverter
import com.craft.silicon.centemobile.databinding.ActivityMainBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.MyActivityResult
import com.craft.silicon.centemobile.util.ScreenHelper
import com.craft.silicon.centemobile.util.ScreenHelper.fullScreen
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.fragment.home.HomeFragment
import com.craft.silicon.centemobile.view.model.DynamicViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


private const val REQUEST_READ_CONTACTS_PERMISSION = 0

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppCallbacks, NavController.OnDestinationChangedListener {
    private val activityLauncher: MyActivityResult<Intent, ActivityResult> =
        MyActivityResult.registerActivityForResult(this)


    private lateinit var binding: ActivityMainBinding
    private val viewModel: DynamicViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private var navController: NavController? = null
    private val workViewModel: WorkerViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private var callbacks: AppCallbacks? = null


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
        val version = widgetViewModel.storageDataSource.version.value
        workViewModel.routeData(this, object : WorkStatus {
            override fun workDone(b: Boolean) {
                if (b) {
                    AppLogger.instance.appLog("DATA:Version", version!!)
                    if (!TextUtils.isEmpty(version))
                        workViewModel.onWidgetData()
                }
            }
        })
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

        when (destination.id) {
            R.id.landingPageFragment -> {
                fullScreen(this, true, true)
            }
            else -> fullScreen(this, false, false)
        }

    }


    private fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED
    }


    fun requestContactsPermission(callbacks: AppCallbacks) {
        this.callbacks = callbacks
        if (!hasContactsPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS_PERMISSION
            )
        } else {
            onContactPicker()
        }
    }

    private fun onContactPicker() {
        val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        activityLauncher.launch(pickContact) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val contactUri: Uri? = data?.data
                val cursor: Cursor? = this.contentResolver
                    .query(
                        contactUri!!,
                        null,
                        null,
                        null,
                        null
                    )
                try {
                    cursor.use { s ->
                        s?.moveToFirst()
                        val id = s?.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                            ?.let { s.getString(it) }

                        val hasPhone =
                            s?.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                                ?.let { s.getString(it) }
                        if (BaseClass.nonCaps(hasPhone) == "1") {
                            val phones = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract
                                    .CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                null,
                                null
                            )
                            phones.use { p ->
                                p?.moveToFirst()
                                val number = p?.getColumnIndex("data1")?.let { p.getString(it) }
                                callbacks?.setContact(number)
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_READ_CONTACTS_PERMISSION && grantResults.isNotEmpty()) {
            onContactPicker()
        }
    }

}
