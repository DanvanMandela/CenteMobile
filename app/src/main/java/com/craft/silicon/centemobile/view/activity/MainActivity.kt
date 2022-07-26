package com.craft.silicon.centemobile.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.ImageDecoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
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
import com.craft.silicon.centemobile.data.source.constants.Constants
import com.craft.silicon.centemobile.data.source.remote.helper.NetworkIsh
import com.craft.silicon.centemobile.databinding.ActivityMainBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.MyActivityResult
import com.craft.silicon.centemobile.util.ScreenHelper.fullScreen
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.image.compressImage
import com.craft.silicon.centemobile.view.fragment.home.HomeFragment
import com.craft.silicon.centemobile.view.fragment.map.MapData
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppCallbacks,
    NavController.OnDestinationChangedListener, NetworkIsh {
    private val activityLauncher: MyActivityResult<Intent, ActivityResult> =
        MyActivityResult.registerActivityForResult(this)

    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null
    private val workViewModel: WorkerViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private var callbacks: AppCallbacks? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setViewModel()
        setNavigation()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

    }

    private fun listenToConnection() {
        val animationDuration =
            resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            widgetViewModel.networkDataSource.connection(this)
            widgetViewModel.networkDataSource.enable()
        }, animationDuration.toLong())
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
                    if (TextUtils.isEmpty(version))
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
            else -> {
                fullScreen(this, false, false)
                if (destination.id == R.id.landingPageFragment) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        checkLocationPermission()
                    }, 600)
                }
            }
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

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Permission Needed")
        builder.setMessage(
            "This app needs  Location permission, " +
                    "please accept to use location functionality"
        )
        builder.setPositiveButton(
            "Ok"
        ) { dialog, _ ->
            requestLocationPermission()
            dialog.cancel()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun showCameraSettingsDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, _ ->
            dialog.cancel()
            openCameraSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun openCameraSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        activityLauncher.launch(intent)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            REQUEST_LOCATION
        )
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

        when (requestCode) {
            REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        getCurrentLocation()
                    }

                } else {

                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
            REQUEST_LOCATION_BACKGROUND -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, "back ground location granted", Toast.LENGTH_LONG)
                            .show()

                    }
                } else {
                    Toast.makeText(this, "location permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
            REQUEST_READ_CONTACTS_PERMISSION -> onContactPicker()
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest: LocationRequest = LocationRequest.create().apply {
                interval = Constants.MapsConstants.interval
                fastestInterval = Constants.MapsConstants.fastestInterval
                priority = Priority.PRIORITY_HIGH_ACCURACY
                maxWaitTime = Constants.MapsConstants.maxWaitTime
            }
            val locationCallback: LocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val locationList = locationResult.locations
                    if (locationList.isNotEmpty()) {
                        val location = locationList.last()
                        AppLogger.instance.appLog(
                            "USER:LOCATION",
                            "lat:${location.latitude} lng:${location.longitude}"
                        )
                        widgetViewModel.storageDataSource.setLatLng(
                            MapData(LatLng(location.latitude, location.longitude))
                        )
                    }
                }
            }
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }


    }

    @SuppressLint("ServiceCast")
    fun checkLocationPermission() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGpsAlert(this)
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                showSettingsDialog()
            } else {
                requestLocationPermission()
            }
        } else {
            getCurrentLocation()
        }
    }

    private fun showGpsAlert(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(getString(R.string.location_required))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }


    companion object {
        private const val REQUEST_READ_CONTACTS_PERMISSION = 0
        private const val REQUEST_LOCATION = 100
        private const val REQUEST_LOCATION_BACKGROUND = 101
    }

    override fun onNetwork(boolean: Boolean) {
        AppLogger.instance.appLog("Connection:is:", "$boolean")
        if (!boolean) {
            runOnUiThread {
                if (provideNavigationGraph().currentDestination?.id != R.id.connectionFragment)
                    provideNavigationGraph().navigate(
                        widgetViewModel.navigation().navigateConnection()
                    )
            }
        } else runOnUiThread {
            if (provideNavigationGraph().currentDestination?.id == R.id.connectionFragment)
                provideNavigationGraph().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()
        listenToConnection()
    }

    override fun onStart() {
        super.onStart()
        listenToConnection()
    }

    override fun onStop() {
        super.onStop()
        widgetViewModel.networkDataSource.disable()
    }

    fun onImagePicker(callbacks: AppCallbacks) {
        this.callbacks = callbacks
        ImagePicker.clearCache(this)
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


    private fun launchCameraIntent() {
        val intent = Intent(this@MainActivity, ImagePicker::class.java)
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_IMAGE_CAPTURE)

        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1)
        intent.putExtra(ImagePicker.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePicker.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePicker.INTENT_BITMAP_MAX_HEIGHT, 1000)
        activityLauncher.launch(intent) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data!!.getParcelableExtra<Uri>("path")
                try {
                    uri?.let {
                        if (Build.VERSION.SDK_INT < 28) {
                            val bitmap =
                                MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                            callbacks!!.onImage(compressImage(bitmap))

                        } else {
                            val source = ImageDecoder.createSource(this.contentResolver, uri)
                            val mutableBitmap = ImageDecoder.decodeBitmap(
                                source
                            ) { decoder, _, _ ->
                                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                                decoder.isMutableRequired = true
                            }

                            callbacks!!.onImage(compressImage(mutableBitmap))
                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    }


    private fun launchGalleryIntent() {
        val intent = Intent(this@MainActivity, ImagePicker::class.java)
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_GALLERY_IMAGE)
        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1)

        activityLauncher.launch(intent) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data!!.getParcelableExtra<Uri>("path")
                try {
                    uri?.let {
                        if (Build.VERSION.SDK_INT < 28) {
                            val bitmap =
                                MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                            callbacks!!.onImage(compressImage(bitmap))

                        } else {
                            val source = ImageDecoder.createSource(this.contentResolver, uri)
                            // val bitmap = ImageDecoder.decodeBitmap(source)
                            val mutableBitmap = ImageDecoder.decodeBitmap(
                                source
                            ) { decoder, _, _ ->
                                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                                decoder.isMutableRequired = true
                            }
                            callbacks!!.onImage(compressImage(mutableBitmap))
                        }
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showImagePickerOptions() {
        ImagePicker.showImagePickerOptions(this, object : ImagePicker.PickerOptionListener {
            override fun onTakeCameraSelected() {
                launchCameraIntent()
            }

            override fun onChooseGallerySelected() {
                launchGalleryIntent()
            }
        })
    }

}
