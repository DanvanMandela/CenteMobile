package com.elmacentemobile.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.elmacentemobile.R
import com.elmacentemobile.data.model.user.ActivationData
import com.elmacentemobile.data.service.otp.SMSAutoReadFactory
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.source.constants.Constants.Data.TEST_PROD
import com.elmacentemobile.data.source.constants.Keys
import com.elmacentemobile.data.source.pref.CryptoManager
import com.elmacentemobile.data.source.remote.helper.ConnectionObserver
import com.elmacentemobile.databinding.ActivityMainBinding
import com.elmacentemobile.util.*
import com.elmacentemobile.util.BaseClass.decode64
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.image.compressImage
import com.elmacentemobile.util.image.getImageFromStorage
import com.elmacentemobile.view.activity.test.users
import com.elmacentemobile.view.fragment.auth.bio.BioInterface
import com.elmacentemobile.view.fragment.auth.bio.util.BiometricAuthListener
import com.elmacentemobile.view.fragment.auth.bio.util.BiometricUtil
import com.elmacentemobile.view.fragment.go.steps.OTP
import com.elmacentemobile.view.fragment.map.MapData
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.IpStackViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.elmacentemobile.view.model.WorkStatus
import com.elmacentemobile.view.model.WorkerViewModel
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


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
    private val ipsViewModel: IpStackViewModel by viewModels()
    private var callbacks: AppCallbacks? = null
    private var cryptographyManager = CryptoManager()
    private var bioInterface: BioInterface? = null
    private val envChecks = BitSet()
    private val dispose = CompositeDisposable()

    private var appUpdateManager: AppUpdateManager? = null
    private var updateListener: InstallStateUpdatedListener? = null


    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(this)
            val image = getImageFromStorage(uriFilePath!!)
            callbacks?.onImage(compressImage(image!!))
        } else {
            val exception = result.error
            AppLogger.instance.appLog("COPPER:ERROR", "${exception?.printStackTrace()}")
        }
    }

    fun initSMSBroadCast() {
        startSMSListener()
    }


    private fun phoneSelection() {
        val request: GetPhoneNumberHintIntentRequest =
            GetPhoneNumberHintIntentRequest.builder().build()
        Identity.getSignInClient(this)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener {
                phoneNumberHintIntentResultLauncher.launch(
                    IntentSenderRequest.Builder(it.intentSender).build()
                )
            }
            .addOnFailureListener {
                AppLogger.instance.appLog("PHONE:SELECT", it.localizedMessage)
            }
    }


    override fun onStart() {
        // phoneSelection()
        super.onStart()
        widgetViewModel.storageDataSource.deleteOtp()
        checkLocationPermission()
    }

    override fun onStop() {
        super.onStop()
        widgetViewModel.storageDataSource.deleteOtp()
        if (appUpdateManager != null) appUpdateManager?.unregisterListener(updateListener!!)
    }

    private fun listenToInActivity() {
        val state = baseViewModel.dataSource.inActivity.asLiveData()
        state.observe(this) {
            if (it != null) {
                if (it) done(true)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // doProbe(this)
        setBinding()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ipStack()
//        }
        appSignature()
        setNavigation()
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        requestPermissions()
        listenToConnection()
        subscribePush()
        setMigration()//TODO TO BE REMOVED IN FUTURE
        securityCheck()
        onUserInteraction()
        listenToInActivity()


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            checkNewVersion()
        }

        setViewModel()
    }


    private fun ipStack() {
        workViewModel.ipBackground()
    }

    private val phoneNumberHintIntentResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            try {
                val phoneNumber =
                    Identity.getSignInClient(this).getPhoneNumberFromIntent(result.data)
                callbacks?.telephone(phoneNumber)
            } catch (e: Exception) {
                AppLogger.instance.appLog(
                    SMSAutoReadFactory::class.java.simpleName,
                    "${e.message}"
                )
            }
        }

    fun requestHint(callbacks: AppCallbacks) {
        this@MainActivity.callbacks = callbacks
        val request: GetPhoneNumberHintIntentRequest =
            GetPhoneNumberHintIntentRequest.builder().build()
        Identity.getSignInClient(this)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener {
                phoneNumberHintIntentResultLauncher.launch(
                    IntentSenderRequest.Builder(it.intentSender).build()
                )
            }
            .addOnFailureListener {
                AppLogger.instance.appLog(
                    MainActivity::class.java.simpleName,
                    "${it.message}"
                )
            }
    }

    private fun checkNewVersion() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        setUpdateListener()
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                startUpdate(AppUpdateType.IMMEDIATE, appUpdateInfo)
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                startUpdate(AppUpdateType.FLEXIBLE, appUpdateInfo)
            }
        }
    }

    private fun startUpdate(type: Int, appUpdateInfo: AppUpdateInfo) {
        appUpdateManager?.startUpdateFlowForResult(
            appUpdateInfo,
            this,
            AppUpdateOptions.newBuilder(type)
                .setAllowAssetPackDeletion(true)
                .build(),
            AUTO_UPDATE
        )
    }

    @SuppressLint("SwitchIntDef")
    private fun setUpdateListener() {
        updateListener = InstallStateUpdatedListener { state ->
            when (state.installStatus()) {
                InstallStatus.DOWNLOADING -> {
                    val bytesDownloaded = state.bytesDownloaded()
                    val totalBytesToDownload = state.totalBytesToDownload()
                }

                InstallStatus.DOWNLOADED -> {
                    appUpdateManager?.completeUpdate()
                }

                InstallStatus.INSTALLED -> {
                    if (appUpdateManager != null && updateListener != null)
                        appUpdateManager?.unregisterListener(updateListener!!)
                }


            }
        }
        appUpdateManager?.registerListener(updateListener!!)


    }


//    override fun onResume() {
//        super.onResume()
//        try {
//            appUpdateManager
//                ?.appUpdateInfo
//                ?.addOnSuccessListener { appUpdateInfo ->
//                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
//                        appUpdateManager?.completeUpdate()
//                    }
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == AUTO_UPDATE) {
//            if (resultCode != RESULT_OK) {
//
//            }
//        }
//    }

    private fun update() {
        Snackbar.make(
            binding.root.rootView,
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager?.completeUpdate() }
            setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.app_blue_light))
            show()
        }
    }


    private fun securityCheck() {
        if (envChecks.get(EnvEnum.DEBUG.value)) {
            Toast.makeText(this@MainActivity, "Debug detected.", Toast.LENGTH_LONG).show()
            finish()
        } else if (rooted()) {
            Toast.makeText(this@MainActivity, "SU detected.", Toast.LENGTH_LONG).show()
            finish()
        } else if (envChecks.get(EnvEnum.EMULATOR.value)) {
            Toast.makeText(this@MainActivity, "Emulator detected.", Toast.LENGTH_LONG).show()
            finish()
        } else if (envChecks.get(EnvEnum.XPOSED.value)) {
            Toast.makeText(this@MainActivity, "XPOSED detected.", Toast.LENGTH_LONG).show()
            finish()
        } else if (envChecks.get(EnvEnum.CUSTOM_FIRMWARE.value)) {
            Toast.makeText(this@MainActivity, "Custom firmware detected.", Toast.LENGTH_LONG).show()
            finish()
        } else if (envChecks.get(EnvEnum.INTEGRITY.value)) {
            Toast.makeText(this@MainActivity, "Integrity failed.", Toast.LENGTH_LONG).show()
            finish()
        } else if (envChecks.get(EnvEnum.WIRELESS_SECURITY.value)) {
            Toast.makeText(this@MainActivity, "Wireless security check failed.", Toast.LENGTH_LONG)
                .show()
            finish()
        } else if (envChecks.get(EnvEnum.ROOT.value)) {
            Toast.makeText(
                this@MainActivity,
                "CenteMobile can not run on a rooted device.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun setMigration() {
        if (Constants.Data.ACTIVATED) {
            val user = if (TEST_PROD) users.last() else users[1]
            baseViewModel.dataSource.setActivated(true)
            baseViewModel.dataSource.setActivationData(
                ActivationData(
                    id = user.customerID,
                    mobile = user.mobile
                )
            )
        } else {
            val mobile = baseViewModel.dataSource.phoneCustomer.value
            val customerID = baseViewModel.dataSource.customerID.value
            if (!mobile.isNullOrBlank() && !customerID.isNullOrBlank()) {
                baseViewModel.dataSource.setActivated(true)
                baseViewModel.dataSource.setActivationData(
                    ActivationData(
                        id = BaseClass.decryptCustomer(customerID),
                        mobile = BaseClass.decryptCustomer(mobile)
                    )
                )
            }
        }

    }


    private fun appSignature() {
        AppLogger.instance.appLog(
            "${MainActivity::class.java.simpleName} :Signature",
            AppSignatureHelper(this).appSignatures[0]
        )
    }


    override fun onUserInteraction() {
        super.onUserInteraction()
        baseViewModel.interactionDataSource.onUserInteracted()
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

    override fun share(intent: Intent?) {
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun setViewModel() {
        val version = widgetViewModel.storageDataSource.version.value
        val forceData = widgetViewModel.storageDataSource.forceData.value
        workViewModel.routeData(this, object : WorkStatus {
            override fun workDone(b: Boolean) {
                if (b) {
                    workViewModel.tipOfDayData()
                    AppLogger.instance.appLog("DATA:Version", version!!)
                    if (TextUtils.isEmpty(version)) {
                        widgetViewModel.storageDataSource.setVersion("R")
                        widgetViewModel.storageDataSource.forceData(false)
                        workViewModel.onWidgetData(this@MainActivity, null)
                    } else {
                        if (forceData == true) {
                            AppLogger.instance.appLog("DATA:Force", "$forceData")
                            workViewModel.onWidgetData(this@MainActivity, object : WorkStatus {
                                override fun progress(p: Int) {
                                    if (p == 100)
                                        widgetViewModel.storageDataSource.forceData(false)
                                }
                            })
                        }
                    }
                }
            }

            override fun progress(p: Int) {
                AppLogger.instance.appLog("DATA:Progress", "$p")
            }
        })

        val sync = widgetViewModel.storageDataSource.sync.asLiveData()
        sync.observe(this) {
            AppLogger.instance.appLog(
                "${this::class.java.simpleName}:Data:Progress",
                Gson().toJson(it)
            )
        }

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
            R.id.landingPageFragment -> {}
            else -> {}
        }

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
//            REQUEST_LOCATION_BACKGROUND -> {
//
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ContextCompat.checkSelfPermission(
//                            this,
//                            Manifest.permission.ACCESS_FINE_LOCATION
//                        ) == PackageManager.PERMISSION_GRANTED
//                    ) {
//                        Toast.makeText(this, "back ground location granted", Toast.LENGTH_LONG)
//                            .show()
//
//                    }
//                } else {
//                    Toast.makeText(this, "location permission denied", Toast.LENGTH_LONG).show()
//                }
//                return
//
//            }
            REQUEST_READ_CONTACTS_PERMISSION -> onContactPicker()
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationHelper(context = this,
                Constants.MapsConstants.interval,
                2f,
                object : MyLocation {
                    override fun result(result: LocationResult) {
                        val locationList = result.locations
                        val location = locationList.last()
                        AppLogger.instance.appLog(
                            "USER:LOCATION",
                            "lat:${location.latitude} lng:${location.longitude}"
                        )
                        widgetViewModel.storageDataSource.setLatLng(
                            MapData(LatLng(location.latitude, location.longitude))
                        )
                    }
                }).startLocationTracking()
        } else {
            widgetViewModel.storageDataSource.setLatLng(
                MapData(LatLng(0.0, 0.0))
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
        private const val REQUEST_CAMERA_PERMISSION = 405
        private const val AUTO_UPDATE = 404

    }

    private fun showSettingsCameraDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Camera Permission Needed")
        builder.setMessage(
            "This app needs  Camera permission, " +
                    "please accept to use camera functionality"
        )
        builder.setPositiveButton(
            "Ok"
        ) { dialog, _ ->
            requestCameraPermission()
            dialog.cancel()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
            ),
            REQUEST_CAMERA_PERMISSION
        )
    }


    fun onImagePicker(callbacks: AppCallbacks, x: Int, y: Int) {
        this.callbacks = callbacks
        ImagePicker.clearCache(this)
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions(x, y)
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsCameraDialog()
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


    private fun showImagePickerOptions(x: Int, y: Int) {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setImageSource(includeGallery = Constants.Data.TEST, includeCamera = true)
                setOutputCompressQuality(50)
            }
        )
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
            decode64(Keys().secretKey()),
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
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissions.addAll(
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        Dexter.withContext(this)
            .withPermissions(permissions)
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
        task.addOnFailureListener {
            AppLogger.instance.appLog("${MainActivity::class.java.simpleName}: SMS", "FAIL")
        }
    }


    override fun timer(str: String) {
        // AppLogger.instance.appLog("${MainActivity::class.simpleName}:Timeout", str)
    }


    override fun done(boolean: Boolean) {
        if (boolean) {
            when (provideNavigationGraph().currentDestination?.id) {
                R.id.homeFragment,
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
        }
    }


    private fun rooted(): Boolean {
        val paths = arrayOf(
            "/system/su",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su-backup",
            "/system/xbin/mu*/",
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }


}

internal enum class EnvEnum(val value: Int) {
    ROOT(0), DEBUG(1), EMULATOR(2),
    XPOSED(3), CUSTOM_FIRMWARE(4),
    INTEGRITY(5), WIRELESS_SECURITY(6);
}



