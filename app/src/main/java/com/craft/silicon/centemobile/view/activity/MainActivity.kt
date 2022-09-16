package com.craft.silicon.centemobile.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.ImageDecoder
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.GetBaseURL
import com.craft.silicon.centemobile.data.model.user.ActivationData
import com.craft.silicon.centemobile.data.source.constants.Constants
import com.craft.silicon.centemobile.data.source.pref.CryptoManager
import com.craft.silicon.centemobile.data.source.remote.helper.ConnectionObserver
import com.craft.silicon.centemobile.databinding.ActivityMainBinding
import com.craft.silicon.centemobile.util.*
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.image.compressImage
import com.craft.silicon.centemobile.view.fragment.auth.bio.BioInterface
import com.craft.silicon.centemobile.view.fragment.auth.bio.util.BiometricAuthListener
import com.craft.silicon.centemobile.view.fragment.auth.bio.util.BiometricUtil
import com.craft.silicon.centemobile.view.fragment.go.steps.OTP
import com.craft.silicon.centemobile.view.fragment.go.steps.OTPCountDownTimer
import com.craft.silicon.centemobile.view.fragment.map.MapData
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
import com.craft.silicon.centemobile.view.qr.QRContent
import com.craft.silicon.centemobile.view.qr.QRResult
import com.craft.silicon.centemobile.view.qr.ScanQRCode
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppCallbacks,
    NavController.OnDestinationChangedListener, BiometricAuthListener, OTP {
    private val activityLauncher: MyActivityResult<Intent, ActivityResult> =
        MyActivityResult.registerActivityForResult(this)
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null
    private val workViewModel: WorkerViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private var callbacks: AppCallbacks? = null
    private var cryptographyManager = CryptoManager()
    private var bioInterface: BioInterface? = null
    private var countDownTimer: CountDownTimer? = null

    private val scanQrCode = registerForActivityResult(ScanQRCode(), ::scanSuccess)
    private var startTime = (24 * 1000).toLong()
    private val interval = (1 * 1000).toLong()

    private val qrcode = MutableLiveData<String>()


    fun initSMSBroadCast() {
        startSMSListener()
    }


    override fun onStart() {
        super.onStart()
        widgetViewModel.storageDataSource.deleteOtp()
        checkLocationPermission()
    }

    override fun onStop() {
        super.onStop()
        widgetViewModel.storageDataSource.deleteOtp()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        appSignature()
        setViewModel()
        setNavigation()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        requestPermissions()
        listenToConnection()
        subscribePush()
        updateTimeout()
        setMigration()//TODO TO BE REMOVED IN FUTURE

    }

    private fun testBase() {
        val base="https://app.craftsilicon.com/CentemobileWebValidateDynamic/api/elma/validate"

        AppLogger.instance.appLog("PATH", GetBaseURL(base).path)
        AppLogger.instance.appLog("URL", GetBaseURL(base).base)
    }

    private fun setMigration() {
        val mobile = baseViewModel.dataSource.phoneCustomer.value
        val customerID = baseViewModel.dataSource.customerID.value
        if (!mobile.isNullOrBlank() && !customerID.isNullOrBlank())
            baseViewModel.dataSource.setActivationData(
                ActivationData(id = customerID, mobile = mobile)
            )
    }

    private fun updateTimeout() {
        val timeout = baseViewModel.dataSource.timeout.asLiveData()
        timeout.observe(this) { result ->
            if (result != null) {
                startTime = result
            }
        }
        setTimer()
    }

    private fun appSignature() {
        AppLogger.instance.appLog(
            "${MainActivity::class.java.simpleName} :Signature",
            AppSignatureHelper(this).appSignatures[0]
        )
    }


    override fun onUserInteraction() {
        super.onUserInteraction()
        timerControl(false)
        setTimer()
    }


    private fun subscribePush() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    task.exception?.printStackTrace()
                    return@OnCompleteListener
                }
                val token = task.result

                widgetViewModel.storageDataSource.setNotificationToken(token)
                AppLogger.instance.appLog(
                    "Token",
                    token
                )
            })


    }


    override fun onResume() {
        super.onResume()
        val inActivity = baseViewModel.dataSource.inActivity.value
        if (inActivity == null) {
            timerControl(false)
            setTimer()
        } else {
            if (inActivity) {
                runOnUiThread {
                    done(true)
                }
            } else {
                timerControl(false)
                setTimer()
            }
        }
    }


    private fun processData(cryptoObject: BiometricPrompt.CryptoObject?) {
        val s = widgetViewModel.storageDataSource.iv.value
        val data = cryptographyManager.decryptData(s!!.cipherText, cryptoObject?.cipher!!)
        bioInterface?.onPin(data)
    }

    fun authenticateTo(bioInterface: BioInterface) {
        this.bioInterface = bioInterface
        showBiometricLoginOption()
    }


    private fun listenToConnection() {
        val animationDuration =
            resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            val status = widgetViewModel.connectionObserver.observe().asLiveData()

            status.observe(this) { result ->
                if (result != null)
                    when (result) {
                        ConnectionObserver.ConnectionEnum.Capable,
                        ConnectionObserver.ConnectionEnum.Available -> {
                            if (provideNavigationGraph().currentDestination?.id == R.id.connectionFragment)
                                provideNavigationGraph().navigateUp()
                        }
                        ConnectionObserver.ConnectionEnum.UnCapable,
                        ConnectionObserver.ConnectionEnum.UnAvailable,
                        ConnectionObserver.ConnectionEnum.Losing,
                        ConnectionObserver.ConnectionEnum.Lost -> {
                            runOnUiThread {
                                if (provideNavigationGraph().currentDestination?.id != R.id.connectionFragment)
                                    provideNavigationGraph().navigate(
                                        widgetViewModel.navigation().navigateConnection()
                                    )
                            }
                        }

                    }
            }

        }, animationDuration.toLong())
    }


    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
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
                    if (TextUtils.isEmpty(version)) {
                        widgetViewModel.storageDataSource.setVersion("1")
                        workViewModel.onWidgetData(this@MainActivity, null)
                    }
                }
            }

            override fun progress(p: Int) {
                AppLogger.instance.appLog("DATA:Progress", "$p")
            }
        })
    }

    private fun setLoading(b: Boolean) {
        if (b) runOnUiThread {
            if (provideNavigationGraph().currentDestination?.id != R.id.loadingFragment)
                provideNavigationGraph().navigate(
                    widgetViewModel.navigation().navigateToLoading()
                )
        } else if (provideNavigationGraph().currentDestination?.id == R.id.loadingFragment)
            provideNavigationGraph().navigateUp()
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
                //fullScreen(this, true, true)
            }
            else -> {
                // fullScreen(this, false, false)
                if (destination.id == R.id.landingPageFragment) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        requestPermissions()
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

    fun requestLocationPermission() {
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

            } else {

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


    fun onImagePicker(callbacks: AppCallbacks, x: Int, y: Int) {
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
                        showImagePickerOptions(x, y)
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


    private fun launchCameraIntent(x: Int, y: Int) {
        val intent = Intent(this@MainActivity, ImagePicker::class.java)
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_IMAGE_CAPTURE)

        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, x) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, y)
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


    private fun launchGalleryIntent(x: Int, y: Int) {
        val intent = Intent(this@MainActivity, ImagePicker::class.java)
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_GALLERY_IMAGE)
        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, x) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, y)

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

    private fun showImagePickerOptions(x: Int, y: Int) {
        ImagePicker.showImagePickerOptions(this, object : ImagePicker.PickerOptionListener {
            override fun onTakeCameraSelected() {
                launchCameraIntent(x, y)
            }

            override fun onChooseGallerySelected() {
                launchGalleryIntent(x, y)
            }
        })
    }

    @SuppressLint("ObsoleteSdkInt")
    fun isBiometric(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT < 29) {
                val keyguardManager: KeyguardManager =
                    applicationContext.getSystemService(KEYGUARD_SERVICE)
                            as KeyguardManager
                val packageManager: PackageManager =
                    applicationContext.packageManager
                packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
                keyguardManager.isKeyguardSecure
            } else {
                BiometricManager
                    .from(applicationContext)
                    .canAuthenticate(
                        BiometricManager
                            .Authenticators.BIOMETRIC_WEAK
                    ) == BiometricManager.BIOMETRIC_SUCCESS
            }
        } else true
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        processData(result.cryptoObject)
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) {
        ShowToast(this, errorMessage)
    }

    private fun showBiometricLoginOption() {
        val data = widgetViewModel.storageDataSource.iv.value
        val cipher = cryptographyManager.getInitializedCipherForDecryption(
            getString(R.string.secret_key_name),
            data!!.iv
        )

        BiometricUtil.showBiometricPrompt(
            "${getString(R.string.app_name)} ${getString(R.string.auth_)}",
            getString(R.string.use_password),
            getString(R.string.auth_finger),
            this,
            this,
            BiometricPrompt.CryptoObject(cipher)
        )
    }


    private fun requestPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        AppLogger.instance.appLog("PERMISSIONS", "permissions are Denied")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                AppLogger.instance.appLog("PERMISSIONS", it.name)
            }
            .onSameThread().check()
    }

    private fun startSMSListener() {
        val client = SmsRetriever.getClient(this)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener {

            AppLogger.instance.appLog("${MainActivity::class.java.simpleName}: SMS", "SUCCESS")

        }
        task.addOnFailureListener { }
    }

    override fun timer(str: String) {
        // AppLogger.instance.appLog("${MainActivity::class.simpleName}:Timeout", str)
    }

    private fun timerControl(startTimer: Boolean) {
        if (startTimer) {
            countDownTimer!!.start()
        } else {
            countDownTimer!!.cancel()
        }
    }

    override fun done(boolean: Boolean) {
        if (boolean) {
            AppLogger.instance.appLog("Timeout:", "oops!")
            when (provideNavigationGraph().currentDestination?.id) {
                R.id.homeFragment,
                R.id.levelOneFragment,
                R.id.levelTwoFragment,
                R.id.miniStatementFragment,
                R.id.transactionCenterFragment
                -> {
                    lifecycleScope.launch {
                        provideNavigationGraph().navigate(
                            widgetViewModel.navigation().navigateLanding()
                        )
                    }
                }
                else -> {
                    AppLogger.instance.appLog("Timeout:", "Not here")
                }
            }
            timerControl(false)
        }
    }

    private fun setTimer() {
        countDownTimer = OTPCountDownTimer(startTime = startTime, interval = interval, this)
        timerControl(true)
        done(false)
    }


    private fun decodeString() {
        val str = getString(R.string.decode_str)
        val dec = BaseClass.decompressStaticData(str)
        AppLogger.instance.appLog("${MainActivity::class.simpleName}:Data", dec)
    }


    private fun scanSuccess(result: QRResult?) {
        AppLogger.instance.appLog("TAG", "YEs")
        val text = when (result) {
            is QRResult.QRSuccess -> result.content.rawValue
            QRResult.QRUserCanceled -> getString(R.string.scan_cancel)
            QRResult.QRMissingPermission -> getString(R.string.permision_camera)
            is QRResult.QRError -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
            else -> {
                throw Exception("Not implemented")
            }
        }
        if (result is QRResult.QRSuccess && result.content is QRContent.Url) {
            openUrl(result.content.rawValue)
        } else if (result is QRResult.QRSuccess) {
            qrcode.value = text
        } else {
            AppLogger.instance.appLog("SCAN:QR", text)
        }
    }

    fun startQRCode(callbacks: AppCallbacks) {
        this.callbacks = callbacks
        scanQrCode.launch(null)

        qrcode.observe(this) { qr ->
            if (!qr.isNullOrEmpty()) {
                ShowToast(this, qr)
            }
        }

    }

}

