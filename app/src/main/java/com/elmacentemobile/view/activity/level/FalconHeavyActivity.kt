package com.elmacentemobile.view.activity.level

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
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
import com.airbnb.epoxy.EpoxyRecyclerView
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.elmacentemobile.R
import com.elmacentemobile.data.model.LabelDataTypeConverter
import com.elmacentemobile.data.model.StandingOrder
import com.elmacentemobile.data.model.StandingOrderList
import com.elmacentemobile.data.model.StandingResponseTypeConverter
import com.elmacentemobile.data.model.action.ActionControls
import com.elmacentemobile.data.model.control.ControlFormatEnum
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.control.FormNavigation
import com.elmacentemobile.data.model.converter.DynamicAPIResponseConverter
import com.elmacentemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.elmacentemobile.data.model.converter.GlobalResponseTypeConverter
import com.elmacentemobile.data.model.converter.InsuranceTypeConverter
import com.elmacentemobile.data.model.converter.LoanTypeConverter
import com.elmacentemobile.data.model.dynamic.DynamicAPIResponse
import com.elmacentemobile.data.model.dynamic.DynamicDataResponse
import com.elmacentemobile.data.model.dynamic.FormField
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.model.module.ModuleCategory
import com.elmacentemobile.data.model.module.ModuleDataTypeConverter
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.model.user.Beneficiary
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.source.constants.Keys
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.data.source.pref.CryptoManager
import com.elmacentemobile.data.source.remote.callback.DynamicResponse
import com.elmacentemobile.data.source.remote.helper.compress
import com.elmacentemobile.databinding.ActivityFalconHeavyBinding
import com.elmacentemobile.databinding.BlockCardReaderLayoutBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.MyActivityResult
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.callbacks.Confirm
import com.elmacentemobile.util.callbacks.EventCallback
import com.elmacentemobile.util.image.compressImage
import com.elmacentemobile.util.image.convert
import com.elmacentemobile.util.image.getImageFromStorage
import com.elmacentemobile.view.activity.ImagePicker
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.activity.beneficiary.BeneficiaryManageActivity
import com.elmacentemobile.view.activity.transaction.PendingTransactionActivity
import com.elmacentemobile.view.activity.transaction.TransactionCenterActivity
import com.elmacentemobile.view.binding.FieldValidationHelper
import com.elmacentemobile.view.binding.calendarToDate
import com.elmacentemobile.view.binding.hideSoftKeyboard
import com.elmacentemobile.view.binding.isOnline
import com.elmacentemobile.view.binding.setDynamic
import com.elmacentemobile.view.composable.ContactDialogCompose
import com.elmacentemobile.view.composable.keyboard.CustomKeyData
import com.elmacentemobile.view.composable.keyboard.CustomKeyboard
import com.elmacentemobile.view.composable.keyboard.KeyFunctionEnum
import com.elmacentemobile.view.composable.policy.PolicyAndPrivacyFragment
import com.elmacentemobile.view.dialog.DialogData
import com.elmacentemobile.view.dialog.InfoFragment
import com.elmacentemobile.view.dialog.MainDialogData
import com.elmacentemobile.view.dialog.NewAlertDialogFragment
import com.elmacentemobile.view.dialog.SuccessDialogFragment
import com.elmacentemobile.view.dialog.confirm.ConfirmFragment
import com.elmacentemobile.view.dialog.display.DisplayDialogFragment
import com.elmacentemobile.view.dialog.receipt.ReceiptFragment
import com.elmacentemobile.view.ep.adapter.InsuranceAdapterItem
import com.elmacentemobile.view.ep.adapter.LoanAdapterItem
import com.elmacentemobile.view.ep.controller.DisplayData
import com.elmacentemobile.view.ep.controller.DisplayState
import com.elmacentemobile.view.ep.controller.HashTypeConverter
import com.elmacentemobile.view.ep.controller.LabelData
import com.elmacentemobile.view.ep.controller.LoadingState
import com.elmacentemobile.view.ep.controller.MainDisplayController
import com.elmacentemobile.view.ep.data.AppData
import com.elmacentemobile.view.ep.data.BusData
import com.elmacentemobile.view.ep.data.ContactData
import com.elmacentemobile.view.ep.data.GroupForm
import com.elmacentemobile.view.ep.data.GroupModule
import com.elmacentemobile.view.ep.data.Nothing
import com.elmacentemobile.view.ep.data.ReceiptList
import com.elmacentemobile.view.fragment.auth.bio.BioInterface
import com.elmacentemobile.view.fragment.auth.bio.BiometricFragment
import com.elmacentemobile.view.fragment.auth.bio.util.BiometricAuthListener
import com.elmacentemobile.view.fragment.auth.bio.util.BiometricUtil
import com.elmacentemobile.view.fragment.dynamic.DynamicDialogFragment
import com.elmacentemobile.view.fragment.dynamic.RecentFragment
import com.elmacentemobile.view.fragment.global.GlobalOTPFragment
import com.elmacentemobile.view.fragment.go.ocr.OCRResultActivity
import com.elmacentemobile.view.fragment.go.steps.OCRData
import com.elmacentemobile.view.fragment.landing.LogoutFeedback
import com.elmacentemobile.view.fragment.transaction.StandingOrderDetailsFragment
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.elmacentemobile.view.qr.QRContent
import com.elmacentemobile.view.qr.QRResult
import com.elmacentemobile.view.qr.ScanQRCode
import com.example.icebergocr.IcebergSDK
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Stack
import java.util.TimeZone
import java.util.logging.Logger

@AndroidEntryPoint
class FalconHeavyActivity : AppCompatActivity(), AppCallbacks, Confirm, BiometricAuthListener,
    EventCallback {
    private val activityLauncher: MyActivityResult<Intent, ActivityResult> =
        MyActivityResult.registerActivityForResult(this)

    private val LOGGER = Logger.getLogger(FalconHeavyActivity::class.java.simpleName)


    private val listState = mutableListOf<DisplayState>()
    private lateinit var pinStack: Stack<String>
    private lateinit var binding: ActivityFalconHeavyBinding

    private val baseViewModel: BaseViewModel by viewModels()
    private val inputList = mutableListOf<InputData>()
    private var moduleDataRes = MutableLiveData<DynamicDataResponse?>()
    private var serverResponse = MutableLiveData<DynamicAPIResponse?>()

    private val widgetViewModel: WidgetViewModel by viewModels()

    private val subscribe = CompositeDisposable()
    private lateinit var standingController: MainDisplayController

    private lateinit var scanControl: FormControl
    private lateinit var modules: Modules
    private val scanQrCode = registerForActivityResult(ScanQRCode(), ::scanSuccess)

    private lateinit var busData: BusData

    private var cryptographyManager = CryptoManager()
    private var bioInterface: BioInterface? = null

    private var callback: AppCallbacks? = null

    private var imageView: ImageView? = null

    private var currenct: String = ""

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uriFilePath = result.getUriFilePath(this)
            val image = getImageFromStorage(uriFilePath!!)
            AppLogger.instance.logTXTTwo(
                s = convert(compressImage(image!!)!!),
                context = this,
                "Image.json"
            )
            callback?.onImage(compressImage(image))
        } else {
            val exception = result.error
            AppLogger.instance.appLog("COPPER:ERROR", "${exception?.printStackTrace()}")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerEvent()
        setBinding()
        setController()
        onUserInteraction()
        listenToInActivity()
    }


    private fun listenToInActivity() {
        val state = baseViewModel.dataSource.inActivity.asLiveData()
        state.observe(this) {
            if (it != null) {
                if (it) onCancel()
            }
        }
    }


    override fun setController() {
        try {
            busData = EventBus.getDefault().getStickyEvent(BusData::class.java)
            val storage = baseViewModel.dataSource
            storage.baseVewModel(widgetViewModel)
            setDynamicInputs(busData.inputs)
            binding.lifecycleOwner = this
            binding.callback = this
            binding.data = busData.data
            AppLogger.instance.appLog("BUS", Gson().toJson(busData))
            Handler(Looper.getMainLooper()).postDelayed({
                stopShimmer()
                binding.container.setDynamic(
                    callbacks = this,
                    dynamic = busData.data,
                    storage = storage
                )
            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEvent(bus: BusData) {
        AppLogger.instance.appLog("EVENT", Gson().toJson(busData))
    }


    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_falcon_heavy)
        binding.lifecycleOwner = this
    }


    private fun setDynamicInputs(inputData: MutableList<InputData>?) {
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName}:Dynamic:Inputs", Gson().toJson(
                inputData
            )
        )
        inputList.clear()
        if (inputData != null)
            if (inputData.isNotEmpty()) {
                for (filter in inputData) {
                    userInput(
                        InputData(
                            name = filter.name,
                            key = filter.key,
                            value = filter.value,
                            encrypted = filter.encrypted,
                            mandatory = filter.mandatory,
                            linked = filter.linked
                        )
                    )
                }
            }
    }


    private fun filterModules(f: List<Modules>, modules: Modules) {
        EventBus.getDefault().removeStickyEvent(BusData::class.java)
        EventBus.getDefault().postSticky(
            BusData(
                data = GroupModule(modules, f.toMutableList()),
                inputs = inputList
            )
        )
        val i = Intent(this, FalconHeavyActivity::class.java)

        startActivity(i)
    }

    override fun onServerValue(formControl: FormControl?, view: TextInputEditText?) {
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName} ServerForm:Field",
            Gson().toJson(formControl)
        )
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName} Form|Field",
            Gson().toJson(busData.res)
        )
        try {
            if (busData.res != null) {
                if (!busData.res?.resultsData.isNullOrEmpty())
                    busData.res?.resultsData!!.forEach {
                        if (BaseClass.nonCaps(it.controlID) == BaseClass.nonCaps(formControl?.controlID)) {
                            view?.setText(it.controlValue)
                            userInput(
                                InputData(
                                    name = formControl?.controlText,
                                    key = formControl?.serviceParamID,
                                    value = it.controlValue,
                                    encrypted = formControl?.isEncrypted!!,
                                    mandatory = formControl.isMandatory,
                                    linked = !formControl.linkedToControl.isNullOrBlank()
                                )
                            )
                            AppLogger.instance.appLog(
                                "${FalconHeavyActivity::class.java.simpleName}Field",
                                "Field${it.controlID} value:${it.controlValue}"
                            )
                        }

                    }
                if (!busData.res?.formField.isNullOrEmpty())
                    busData.res?.formField!!.forEach {
                        if (BaseClass.nonCaps(it.controlID) == BaseClass.nonCaps(formControl?.controlID)) {
                            view?.setText(it.controlValue)
                            userInput(
                                InputData(
                                    name = formControl?.controlText,
                                    key = formControl?.serviceParamID,
                                    value = it.controlValue,
                                    encrypted = formControl?.isEncrypted!!,
                                    mandatory = formControl.isMandatory,
                                    linked = !formControl.linkedToControl.isNullOrBlank()
                                )
                            )
                            AppLogger.instance.appLog(
                                "${FalconHeavyActivity::class.java.simpleName}Field",
                                "Field${formControl.controlID} value:${it.controlValue}"
                            )
                        }

                    }

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onServerValue(formControl: FormControl?, view: TextView?) {
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName} ServerForm",
            Gson().toJson(formControl)
        )
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName} Form",
            Gson().toJson(busData.res)
        )
        try {
            if (busData.res != null) {
                if (!busData.res?.formField.isNullOrEmpty())
                    busData.res?.formField!!.forEach {
                        if (BaseClass.nonCaps(it.controlID) == BaseClass.nonCaps(formControl?.controlID)) {
                            //TODO REVIEW IN FUTURE
                            val value: String? = try {
                                val labelData = LabelDataTypeConverter().to(it.controlValue)
                                userInput(
                                    InputData(
                                        name = formControl?.controlText,
                                        key = formControl?.serviceParamID,
                                        value = labelData?.id,
                                        encrypted = formControl?.isEncrypted!!,
                                        mandatory = formControl.isMandatory,
                                        linked = !formControl.linkedToControl.isNullOrBlank()
                                    )
                                )
                                labelData?.question
                            } catch (e: Exception) {
                                it.controlValue
                            }
                            view?.text = value
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun parameters(obj: Any): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        for (field in obj.javaClass.declaredFields) {
            field.isAccessible = true
            try {
                map[field.name] = field[obj] as Any
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return map
    }

    @SuppressLint("NewApi")
    override fun userInput(inputData: InputData?) {

//        LoggerUtil(this@FalconHeavyActivity).logInfo(
//            Base64.encodeToString(
//                compress("Hello word come to this world"),
//                Base64.DEFAULT
//            )
//        )

        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName}:Aliens",
            Gson().toJson(inputData)
        )
        inputList.removeIf { a -> a.key == inputData?.key }
        inputList.add(inputData!!)
    }

    override fun onDynamicDropDown(
        view: AutoCompleteTextView?,
        formControl: FormControl?,
        editTextList: MutableList<TextInputEditText>?
    ) {
        try {

            editTextList?.forEach {
                AppLogger.instance.appLog("DYNAMIC:EDIT:TAGS", it.tag.toString())
            }

            if (busData.res?.formField != null) {
                val data =
                    busData.res?.formField?.single { a -> a.controlID == formControl?.controlID }

                if (BaseClass.nonCaps(data?.controlID) == BaseClass.nonCaps("Packages")) {
                    val packages = InsuranceTypeConverter().from(data?.controlValue)
                    val adapter =
                        InsuranceAdapterItem(
                            this,
                            0, packages!!.toMutableList()
                        )
                    view?.setAdapter(adapter)
                    view?.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, p2, _ ->
                            userInput(
                                InputData(
                                    name = formControl?.controlText,
                                    key = formControl?.serviceParamID,
                                    value = adapter.getItem(p2)?.policyTerm,
                                    encrypted = formControl?.isEncrypted!!,
                                    mandatory = formControl.isMandatory,
                                    linked = !formControl.linkedToControl.isNullOrBlank()
                                )
                            )
                            val map = parameters(adapter.getItem(p2)!!)

                            if (editTextList!!.isNotEmpty())
                                for (s in map.entries) {
                                    editTextList.filter { a ->
                                        BaseClass.nonCaps(a.tag.toString()) == BaseClass.nonCaps(s.key)
                                    }.map { it.setText(s.value.toString()) }
                                }
                        }
                } else if (BaseClass.nonCaps(data?.controlID) == BaseClass.nonCaps("LOANS")) {
                    val packages = LoanTypeConverter().from(data?.controlValue)
                    val adapter =
                        LoanAdapterItem(
                            this,
                            0, packages!!.toMutableList()
                        )
                    view?.setAdapter(adapter)
                    view?.onItemClickListener =
                        AdapterView.OnItemClickListener { _, _, p2, _ ->
                            userInput(
                                InputData(
                                    name = formControl?.controlText,
                                    key = formControl?.serviceParamID,
                                    value = adapter.getItem(p2)?.productId,
                                    encrypted = formControl?.isEncrypted!!,
                                    mandatory = formControl.isMandatory,
                                    linked = !formControl.linkedToControl.isNullOrBlank()
                                )
                            )
                            val map = parameters(adapter.getItem(p2)!!)
                            if (editTextList!!.isNotEmpty())
                                for (s in map.entries) {
                                    editTextList.filter { a ->
                                        BaseClass.nonCaps(a.tag.toString()) == BaseClass.nonCaps(s.key)
                                    }.map { it.setText(s.value.toString()) }
                                }
                        }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showImagePickerOptions(x: Int, y: Int, callbacks: AppCallbacks) {
        this.callback = callbacks
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setImageSource(includeGallery = Constants.Data.TEST, includeCamera = true)
                setOutputCompressQuality(50)
            }
        )
    }


    override fun clearInputData() {
        if (inputList.isNotEmpty()) {
            inputList.removeIf { it.linked }
        }
    }

    private fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactsPermission(callbacks: AppCallbacks) {
        this.callback = callbacks
        if (!hasContactsPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS_PERMISSION
            )
        } else {
            onContactPicker(callbacks)
        }
    }

    @SuppressLint("NewApi")
    private fun onContactPicker(callbacks: AppCallbacks) {
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
                    val numberList = mutableListOf<String>()
                    cursor.use { s ->
                        s?.moveToFirst()
                        val id = s?.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                            ?.let { s.getString(it) }

                        val hasPhone =
                            s?.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                                ?.let { s.getString(it) }

                        val name =
                            s?.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
                                ?.let { s.getString(it) }

                        val thumbnail =
                            s?.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
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
                                while (p!!.moveToNext()) {
                                    val contactNumber =
                                        p.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                            .let { p.getString(it) }
                                    contactNumber?.let {
                                        val num = it.replace(" ", "")
                                        numberList.removeIf { a -> a == num }
                                        numberList.add(num)
                                    }
                                }
                            }

                            val contactData = ContactData(
                                name = name,
                                avatar = thumbnail,
                                numbers = numberList
                            )

                            ContactDialogCompose.newInstance(
                                contactData,
                                supportFragmentManager,
                                callbacks
                            )

                        } else {
                            callbacks.setContact("")
                            ShowToast(this, "${getString(R.string.no_contacts)} $name")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }


    override fun onDateSelect(view: AutoCompleteTextView?, formControl: FormControl?) {
        try {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            picker.addOnPositiveButtonClickListener { selection: Long? ->
                val utc: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                if (selection != null) {
                    utc.timeInMillis = selection
                }
                val date: String? = calendarToDate(utc)
                view?.setText(date)
            }
            picker.show(supportFragmentManager, picker.tag)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }


    override fun onRecent(formControl: FormControl) {
        subscribe.add(
            widgetViewModel.getActionControlCID(formControl.controlID).subscribe({
                if (it.isNotEmpty()) {
                    val action = it.first { a -> a.moduleID == formControl.moduleID }
                    fetchRecent(action)
                }
            }, { it.printStackTrace() })
        )
    }

    private fun fetchRecent(action: ActionControls?) {
        subscribe.add(
            widgetViewModel.recentList(
                action?.moduleID, action?.merchantID, this
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    AppLogger.instance.appLog(
                        "${FalconHeavyActivity::class.java.simpleName}:D:RECENT",
                        BaseClass.decryptLatest(
                            it.response,
                            widgetViewModel.storageDataSource.deviceData.value!!.device,
                            true,
                            widgetViewModel.storageDataSource.deviceData.value!!.run
                        )
                    )
                    if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                        try {
                            val moduleData = DynamicDataResponseTypeConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                                    true,
                                    widgetViewModel.storageDataSource.deviceData.value!!.run
                                )
                            )
                            AppLogger.instance.appLog(
                                "${FalconHeavyActivity::class.java.simpleName}:E:RECENT",
                                Gson().toJson(moduleData)
                            )
                            if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                                moduleDataRes.value = moduleData
                                val recent: MenuItem =
                                    binding.toolbar.menu.findItem(R.id.actionRecent)
                                if (!recent.isVisible) {
                                    recent.isVisible = true
                                }
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }, { it.printStackTrace() })
        )
    }

    override fun validateModule(
        jsonObject: JSONObject,
        encrypted: JSONObject,
        modules: Modules?,
        formControl: FormControl?,
        action: ActionControls?
    ) {
        try {
            AppLogger.instance.appLog(
                "${FalconHeavyActivity::class.java.simpleName}:fields",
                Gson().toJson(jsonObject)
            )
            AppLogger.instance.appLog(
                "${FalconHeavyActivity::class.java.simpleName}:encrypted",
                Gson().toJson(encrypted)
            )
            AppLogger.instance.appLog(
                "${FalconHeavyActivity::class.java.simpleName}:${action?.actionType}:action",
                Gson().toJson(action)
            )
            AppLogger.instance.appLog(
                "${FalconHeavyActivity::class.java.simpleName}:form",
                Gson().toJson(formControl)
            )
            AppLogger.instance.appLog(
                "${FalconHeavyActivity::class.java.simpleName}:action",
                Gson().toJson(action)
            )
            action?.merchantID?.let { AppLogger.instance.appLog("MERCHANT:ID:ACTION", it) }
            modules?.merchantID?.let { AppLogger.instance.appLog("MERCHANT:ID:MODULE", it) }

            subscribe.add(
                baseViewModel.dynamicCall(
                    action,
                    jsonObject,
                    encrypted,
                    modules,
                    this,
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        AppLogger.instance.appLog(
                            "${FalconHeavyActivity::class.java.simpleName}:v:Response:E:",
                            "${it.response}"
                        )
                        try {
                            AppLogger.instance.appLog(
                                "${FalconHeavyActivity::class.java.simpleName}:v:Response",
                                BaseClass.decryptLatest(
                                    it.response,
                                    baseViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    baseViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {

                                val resData = DynamicAPIResponseConverter().to(
                                    BaseClass.decryptLatest(
                                        it.response,
                                        baseViewModel.dataSource.deviceData.value!!.device,
                                        true,
                                        baseViewModel.dataSource.deviceData.value!!.run
                                    )
                                )
                                AppLogger.instance.appLog(
                                    "${FalconHeavyActivity::class.java.simpleName}:Data",
                                    Gson().toJson(resData)
                                )
                                if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                    serverResponse.value = resData
                                    if (!resData?.formID.isNullOrBlank()) {
                                        if (BaseClass.nonCaps(resData!!.formID)
                                            == BaseClass.nonCaps("PAYMENTCONFIRMATIONFORM")
                                        ) {
                                            ReceiptFragment.show(
                                                this, ReceiptList(
                                                    receipt = resData.receipt!!
                                                        .toMutableList(),
                                                    notification = resData.notifications

                                                ), supportFragmentManager
                                            )
                                        } else if (BaseClass.nonCaps(resData.formID)
                                            == BaseClass.nonCaps("STATEMENT")
                                        ) {

                                            DisplayDialogFragment.show(
                                                data = resData.accountStatement,
                                                supportFragmentManager
                                            )

                                        } else if (BaseClass.nonCaps(resData.formID)
                                            == BaseClass.nonCaps("RELIGION") || BaseClass.nonCaps(
                                                resData.formID
                                            )
                                            == BaseClass.nonCaps("FUNEXTRAS")
                                        ) {
                                            val mData = GlobalResponseTypeConverter().to(
                                                BaseClass.decryptLatest(
                                                    it.response,
                                                    baseViewModel.dataSource
                                                        .deviceData.value!!.device,
                                                    true,
                                                    baseViewModel.dataSource
                                                        .deviceData.value!!.run
                                                )
                                            )

                                            DisplayDialogFragment.show(
                                                data = mData?.data,
                                                supportFragmentManager
                                            )


                                        } else if (BaseClass.nonCaps(resData.formID) == BaseClass.nonCaps(
                                                "ADDBENEFICIARY"
                                            ) ||
                                            BaseClass.nonCaps(resData.formID) == BaseClass.nonCaps(
                                                "ADDBENEFICIARYOTHER"
                                            )
                                        ) {
                                            val beneficiaries = resData.beneficiary
                                            if (!beneficiaries.isNullOrEmpty())
                                                baseViewModel.dataSource.setBeneficiary(
                                                    beneficiaries
                                                )
                                            setSuccess(resData.message)

                                        } else if (BaseClass.nonCaps(resData.formID) ==
                                            BaseClass.nonCaps("CHANGEPIN")
                                        ) {
                                            baseViewModel.dataSource.setBio(false)
                                            setSuccess(resData.message)
                                        } else setOnNextModule(
                                            formControl,
                                            if (resData.next.isNullOrBlank()) 0 else resData.next!!.toInt(),
                                            modules,
                                            resData.formID
                                        )
                                    } else setSuccess(resData?.message)
                                } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.OCR_SUCCESS.type) {
                                    setOnNextModule(
                                        formControl,
                                        if (resData!!.next.isNullOrBlank()) 0 else resData.next!!.toInt(),
                                        modules,
                                        resData.formID
                                    )

                                } else if (BaseClass.nonCaps(resData?.status)
                                    == StatusEnum.TOKEN.type
                                ) {
                                    InfoFragment.showDialog(supportFragmentManager, this)
                                } else if (BaseClass.nonCaps(resData?.status)
                                    == StatusEnum.DYNAMIC_FORM_DIALOG.type
                                ) {
                                    setDynamicForm(action, resData, modules)
                                } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.OTP.type) {
                                    GlobalOTPFragment.show(
                                        json = jsonObject,
                                        encrypted = encrypted,
                                        inputList = inputList,
                                        module = modules,
                                        action = action,
                                        formControl,
                                        this,
                                        supportFragmentManager
                                    )
                                } else {
                                    showError(
                                        MainDialogData(
                                            title = getString(R.string.error),
                                            message = resData?.message,
                                        )
                                    )
                                }
                            }

                        } catch (e: Exception) {
                            showError(
                                MainDialogData(
                                    title = getString(R.string.error),
                                    message = getString(R.string.something_),
                                )
                            )
                        }

                    }, {
                        showError(
                            MainDialogData(
                                title = getString(R.string.error),
                                message = getString(R.string.something_),
                            )
                        )
                        it.printStackTrace()
                    })
            )
            subscribe.add(
                baseViewModel.loading.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ setLoading(it) }, { it.printStackTrace() })
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun setDynamicForm(
        action: ActionControls?,
        resData: DynamicAPIResponse?,
        modules: Modules?
    ) {
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName}:NEXT:FORM:ID",
            resData?.formID!!
        )
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName}:NEXT:SEQUENCE",
            "${resData.next}"
        )
        subscribe.add(widgetViewModel.getFormControl(resData.formID, resData.next)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ children: List<FormControl> ->
                setDynamicChildren(action, resData, modules, children)
            }) { obj: Throwable -> obj.printStackTrace() })
    }

    private fun setDynamicChildren(
        action: ActionControls?,
        resData: DynamicAPIResponse,
        modules: Modules?,
        children: List<FormControl>
    ) {
//        EventBus.getDefault().postSticky(
//            BusData(
//                data = GroupForm(
//                    action = action,
//                    module = modules!!,
//                    form = children.toMutableList()
//                ),
//                inputs = inputList,
//                res = resData
//            )
//        )

        DynamicDialogFragment.callback(
            this, supportFragmentManager, BusData(
                data = GroupForm(
                    action = action,
                    module = modules!!,
                    form = children.toMutableList()
                ),
                inputs = inputList,
                res = resData
            )
        )
    }


    private fun setOnNextModule(
        formControl: FormControl?,
        next: Int?,
        modules: Modules?, formID: String?
    ) {
        val sequence = if (next != null) {
            if (next == 0) {
                formControl?.formSequence?.toInt()?.plus(1)
            } else next
        } else formControl?.formSequence?.toInt()?.plus(1)

        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName}:NEXT:FORM:ID",
            formControl?.formID!!
        )
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName}:NEXT:MODULE:ID",
            formID!!
        )
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName}:NEXT:SEQUENCE",
            "$sequence"
        )
        subscribe.add(widgetViewModel.getFormControl(formID, sequence.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f.toMutableList(), modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }

    private fun showError(data: MainDialogData) {
        NewAlertDialogFragment.show(data, supportFragmentManager)
    }

    private fun setSuccess(message: String?, callback: AppCallbacks? = this) {
        SuccessDialogFragment.showDialog(
            DialogData(
                title = R.string.success,
                subTitle = message,
                R.drawable.success
            ),
            supportFragmentManager, callback
        )
    }

    private fun setLoading(b: Boolean) {
        if (b) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        baseViewModel.dataSource.deleteOtp()
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
            userInput(
                InputData(
                    name = scanControl.controlText,
                    key = scanControl.serviceParamID,
                    value = text,
                    encrypted = scanControl.isEncrypted,
                    mandatory = scanControl.isMandatory,
                    linked = !scanControl.linkedToControl.isNullOrBlank()
                )
            )
            Handler(Looper.getMainLooper()).postDelayed({
                lifecycleScope.launch {
                    onForm(scanControl, modules)
                }
            }, 200)
        } else {
            AppLogger.instance.appLog("SCAN:QR", text)
        }
    }


    override fun onDisplay(formControl: FormControl?, modules: Modules?) {
        AppLogger.instance.appLog("DISPLAY:form", Gson().toJson(formControl))

        if (BaseClass.nonCaps(formControl?.controlID) == BaseClass.nonCaps("DISPLAY")) {
            AppLogger.instance.appLog("DISPLAY:DATA:Response:", Gson().toJson(formControl))
            if (!busData.res?.display.isNullOrEmpty()) {
                binding.displayContainer.visibility = View.VISIBLE
                val controller = MainDisplayController(this)
                controller.setData(DisplayData(busData.res?.display, formControl, modules))
                binding.displayContainer.setController(controller)
            }
            if (BaseClass.nonCaps(formControl?.controlFormat) == BaseClass.nonCaps("JSON")) {
                if (!busData.res?.formField.isNullOrEmpty()) {
                    try {
                        val controller = MainDisplayController(this)
                        binding.displayContainer.visibility = View.VISIBLE
                        // stopShimmer()
                        val list =
                            busData.res?.formField?.single { a -> a.controlID == formControl?.controlFormat }


                        val hashMaps = HashTypeConverter().from(list?.controlValue)

                        AppLogger.instance.appLog("DISPLAY:value", Gson().toJson(hashMaps))

                        controller.setData(
                            DisplayData(
                                hashMaps!!.toMutableList(),
                                formControl,
                                modules
                            )
                        )
                        binding.displayContainer.setController(controller)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else if (BaseClass.nonCaps(formControl?.controlID) == BaseClass.nonCaps("JSON")) {
            busData.res?.let { setJsonFormatData(it, formControl, modules) }
        }
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun startShimmer() {
        binding.shimmerContainer.startShimmer()
    }


    private fun setJsonFormatData(
        data: DynamicAPIResponse?,
        form: FormControl?,
        modules: Modules?
    ) {
        try {
            if (data != null) {
                AppLogger.instance.appLog("DYNAMIC:RESULT:DATA", Gson().toJson(data.resultsData))
                AppLogger.instance.appLog("DYNAMIC:FIELD:DATA", Gson().toJson(data.formField))

                startShimmer()
                val controller = MainDisplayController(this)
                if (!data.resultsData.isNullOrEmpty()) {
                    binding.detailsContainer.visibility = View.VISIBLE
                    stopShimmer()
                    val list = data.resultsData?.single { a -> a.controlID == form?.controlID }
                    val hashMaps = HashTypeConverter().from(list?.controlValue)

                    controller.setData(DisplayData(hashMaps!!.toMutableList(), form, modules))
                    binding.detailsContainer.setController(controller)

                } else if (!data.formField.isNullOrEmpty()) {

                    binding.detailsContainer.visibility = View.VISIBLE
                    stopShimmer()
                    // val list = data.formField?.single { a -> a.controlID == form?.controlID }
                    val newList = data.formField?.find { a -> a.controlID == form?.controlID }
                    val hashMaps = HashTypeConverter().from(newList?.controlValue)

                    controller.setData(DisplayData(hashMaps!!.toMutableList(), form, modules))
                    binding.detailsContainer.setController(controller)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setFormNavigation(forms: MutableList<FormControl>?, modules: Modules?) {
        try {
            when (BaseClass.nonCaps(modules?.moduleID)) {
                BaseClass.nonCaps(FormNavigation.FINGERPRINT.name) -> onBiometric(
                    forms,
                    modules
                )

                else -> onNavigation(forms, modules)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onBiometric(forms: MutableList<FormControl>?, modules: Modules?) {
        BiometricFragment.show(form = forms, module = modules, supportFragmentManager)
    }

    override fun globalAutoLiking(value: String?, editText: TextInputEditText?) {
        try {
            if (busData.res != null)
                if (!busData.res?.resultsData.isNullOrEmpty()) {
                    for (s in busData.res?.resultsData!!) {
                        if (s.controlID == "LOANS") {
                            val packages = LoanTypeConverter().from(s.controlValue)
                            for (p in packages!!) {
                                when (value) {
                                    "FULLINSTALMENTPAYMENT",
                                    "InstallmentAmount" -> editText?.setText(
                                        p?.installmentAmount
                                    )

                                    "TOTALINSTALMENTPAYMENT",
                                    "TotalAmount" -> editText?.setText(p?.totalAmount)

                                    else -> editText?.setText("")
                                }
                            }
                        }
                    }

                } else if (!busData.res?.formField.isNullOrEmpty()) {
                    for (s in busData.res?.formField!!) {
                        if (s.controlID == "LOANS") {
                            val packages = LoanTypeConverter().from(s.controlValue)
                            for (p in packages!!) {
                                when (value) {
                                    "FULLINSTALMENTPAYMENT" -> editText?.setText(p?.installmentAmount)
                                    "TOTALINSTALMENTPAYMENT" -> editText?.setText(p?.totalAmount)
                                    else -> editText?.setText("")
                                }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDisplay(
        formControl: FormControl?,
        modules: Modules?,
        container: EpoxyRecyclerView?
    ) {
        try {
            if (BaseClass.nonCaps(formControl?.controlID) == BaseClass.nonCaps("DISPLAY")) {
                AppLogger.instance.appLog("DISPLAY:form:Two", Gson().toJson(formControl))

                if (!busData.res?.display.isNullOrEmpty()) {
                    val controller = MainDisplayController(this)
                    controller.setData(DisplayData(busData.res?.display, formControl, modules))
                    container?.setController(controller)
                }
                if (BaseClass.nonCaps(formControl?.controlFormat) == BaseClass.nonCaps("JSON")) {
                    if (!busData.res?.formField.isNullOrEmpty()) {
                        AppLogger.instance.appLog(
                            "DISPLAY:DATA",
                            Gson().toJson(busData.res?.formField)
                        )

                        try {
                            val controller = MainDisplayController(this)
                            val list =
                                busData.res?.formField?.single { a -> a.controlID == formControl?.controlFormat }
                            val hashMaps = HashTypeConverter().from(list?.controlValue)
                            controller.setData(
                                DisplayData(
                                    hashMaps!!.toMutableList(),
                                    formControl,
                                    modules
                                )
                            )
                            container?.setController(controller)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } else if (BaseClass.nonCaps(formControl?.controlID) == BaseClass.nonCaps("JSON")) {
                val data = busData.res
                if (data != null) {

                    startShimmer()
                    val controller = MainDisplayController(this)
                    if (!data.resultsData.isNullOrEmpty()) {
                        AppLogger.instance.appLog(
                            "DYNAMIC:JSON:RESULT:DATA",
                            Gson().toJson(data.resultsData)
                        )
                        stopShimmer()
                        val list =
                            data.resultsData?.single { a -> a.controlID == formControl?.controlID }
                        val hashMaps = HashTypeConverter().from(list?.controlValue)

                        controller.setData(
                            DisplayData(
                                hashMaps!!.toMutableList(),
                                formControl,
                                modules
                            )
                        )
                        container?.setController(controller)

                    } else if (!data.formField.isNullOrEmpty()) {

                        AppLogger.instance.appLog(
                            "DYNAMIC:JSON:FIELD:DATA",
                            Gson().toJson(data.formField)
                        )
                        stopShimmer()
                        val list =
                            data.formField?.single { a -> a.controlID == formControl?.controlID }
                        val hashMaps = HashTypeConverter().from(list?.controlValue)

                        controller.setData(
                            DisplayData(
                                hashMaps!!.toMutableList(),
                                formControl,
                                modules
                            )
                        )
                        container?.setController(controller)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onForm(formControl: FormControl?, modules: Modules?) {

        lifecycleScope.launch {
            hideSoftKeyboard(binding.root)
        }
        if (BaseClass.nonCaps(formControl?.controlFormat)
            == BaseClass.nonCaps(ControlFormatEnum.END.type)
        ) finish()
        else {
            try {
                subscribe.add(
                    widgetViewModel.getActionControlCID(formControl?.controlID).subscribe({
                        AppLogger.instance.appLog(
                            "MERCHANT",
                            Gson().toJson(it)
                        )
                        if (it.isNotEmpty()) {
                            val merchantID = it.map { a -> a.merchantID }
                            AppLogger.instance.appLog(
                                "${FalconHeavyActivity::class.java.simpleName}:MERCHANT:ID",
                                Gson().toJson(merchantID)
                            )
                            val action = it.first { a -> a.moduleID == formControl?.moduleID }
                            AppLogger.instance.appLog(
                                "${FalconHeavyActivity::class.java.simpleName}:NextModuleID",
                                Gson().toJson(action.nextModuleID)
                            )
                            if (isOnline()) {
                                apiCall(action, formControl, modules)
                            } else ShowToast(
                                this,
                                getString(R.string.no_connection),
                                true
                            )
                        }
                    }, { it.printStackTrace() })
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun apiCall(
        action: ActionControls?,
        formControl: FormControl?,
        modules: Modules?
    ) {
        val params = action?.serviceParamIDs?.split(",")
        val json = JSONObject()
        val encrypted = JSONObject()
        val formData = busData.data as GroupForm
        if (FieldValidationHelper.instance.validateFields(
                inputList = inputList,
                params = params!!,
                activity = this,
                json = json,
                encrypted = encrypted,
                all = formData.allow
            )
        ) {
            when (BaseClass.nonCaps(formControl?.controlFormat)) {
                BaseClass.nonCaps(ControlFormatEnum.NEXT.type) -> {
                    setOnNextModule(
                        formControl = formControl,
                        next = if (formControl?.formSequence.isNullOrEmpty()) null
                        else formControl?.formSequence!!.toInt().plus(1),
                        modules = modules,
                        formID = if (!action.nextModuleID.isNullOrBlank()) action.nextModuleID else
                            modules!!.moduleID
                    )
                }

                else -> {
                    val destination = formControl?.formID
                    when (BaseClass.nonCaps(destination)) {
                        BaseClass.nonCaps(FormNavigation.PAYMENT.name) -> {
                            if (action.confirmationModuleID != null)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    ConfirmFragment.showDialog(
                                        manager = supportFragmentManager,
                                        json = json,
                                        encrypted = encrypted,
                                        inputList = inputList,
                                        module = modules,
                                        action = action,
                                        formControl,
                                        this,
                                    )
                                }, 200)
                            else onPay(
                                json = json, encrypted = encrypted,
                                inputList = inputList, module = modules,
                                action = action, formControl = formControl
                            )
                        }

                        else -> {
                            validateModule(
                                jsonObject = json,
                                encrypted = encrypted,
                                modules = modules,
                                formControl = formControl,
                                action = action
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onPay(
        json: JSONObject?,
        encrypted: JSONObject?,
        inputList: MutableList<InputData>,
        module: Modules?,
        action: ActionControls?,
        formControl: FormControl?
    ) {
        AppLogger.instance.appLog("PURCHASE:fields", Gson().toJson(json))
        AppLogger.instance.appLog("PURCHASE:encrypted", Gson().toJson(encrypted))
        AppLogger.instance.appLog("PURCHASE:action", Gson().toJson(action))
        AppLogger.instance.appLog("PURCHASE:form", Gson().toJson(formControl))

        action?.merchantID?.let { AppLogger.instance.appLog("MERCHANT:ID:ACTION", it) }
        module?.merchantID?.let { AppLogger.instance.appLog("MERCHANT:ID:MODULE", it) }

        subscribe.add(
            baseViewModel.dynamicCall(
                action,
                json,
                encrypted,
                module,
                this
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    try {
                        AppLogger.instance.appLog(
                            "Pay", BaseClass.decryptLatest(
                                it.response,
                                baseViewModel.dataSource.deviceData.value!!.device,
                                true,
                                baseViewModel.dataSource.deviceData.value!!.run
                            )
                        )
                        if (BaseClass.nonCaps(it.response) != "ok") {
                            val resData = DynamicDataResponseTypeConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    baseViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    baseViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                if (!resData?.formID.isNullOrBlank()) {
                                    if (BaseClass.nonCaps(resData!!.formID)
                                        == BaseClass.nonCaps("PAYMENTCONFIRMATIONFORM")
                                    ) {
                                        ReceiptFragment.show(
                                            this, ReceiptList(
                                                receipt = resData.receipt!!
                                                    .toMutableList(),
                                                notification = resData.notifications

                                            ), supportFragmentManager
                                        )

                                    } else if (BaseClass.nonCaps(resData.formID)
                                        == BaseClass.nonCaps("STATEMENT")
                                    ) {

                                        DisplayDialogFragment.showDialog(
                                            supportFragmentManager,
                                            data = resData.accountStatement,
                                            modules = modules,
                                            controller = formControl

                                        )


                                    } else if (BaseClass.nonCaps(resData.formID)
                                        == BaseClass.nonCaps("RELIGION") || BaseClass.nonCaps(
                                            resData.formID
                                        )
                                        == BaseClass.nonCaps("FUNEXTRAS")
                                    ) {
                                        val mData = GlobalResponseTypeConverter().to(
                                            BaseClass.decryptLatest(
                                                it.response,
                                                baseViewModel.dataSource
                                                    .deviceData.value!!.device,
                                                true,
                                                baseViewModel.dataSource
                                                    .deviceData.value!!.run
                                            )
                                        )
                                        DisplayDialogFragment.showDialog(
                                            supportFragmentManager,
                                            data = mData?.data,
                                            modules = modules,
                                            controller = formControl

                                        )

                                    } else if (BaseClass.nonCaps(resData.formID) == BaseClass.nonCaps(
                                            "ADDBENEFICIARY"
                                        ) ||
                                        BaseClass.nonCaps(resData.formID) == BaseClass.nonCaps("ADDBENEFICIARYOTHER")
                                    ) {
                                        val beneficiaries = resData.beneficiary
                                        val b = mutableListOf<Beneficiary>()
                                        if (!beneficiaries.isNullOrEmpty()) {
                                            beneficiaries.forEach { ben ->
                                                b.add(ben!!)
                                            }
                                            baseViewModel.dataSource.setBeneficiary(b)
                                        }
                                        setSuccess(resData.message)
                                    } else if (BaseClass.nonCaps(resData.formID) ==
                                        BaseClass.nonCaps("CHANGEPIN")
                                    ) {
                                        baseViewModel.dataSource.setBio(false)
                                        setSuccess(resData.message)
                                    } else {
                                        setOnNextModule(
                                            formControl,
                                            if (resData.next.isNullOrBlank()) 0 else resData.next!!.toInt(),
                                            module,//TODO CHECK MODULE
                                            resData.formID
                                        )
                                    }
                                } else setSuccess(resData?.message)
                            } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                InfoFragment.showDialog(supportFragmentManager, this)
                            } else if (BaseClass.nonCaps(resData?.status)
                                == StatusEnum.DYNAMIC_FORM_DIALOG.type
                            ) {
                                val mRes = DynamicAPIResponseConverter().to(
                                    BaseClass.decryptLatest(
                                        it.response,
                                        baseViewModel.dataSource.deviceData.value!!.device,
                                        true,
                                        baseViewModel.dataSource.deviceData.value!!.run
                                    )
                                )
                                setDynamicForm(action, mRes, module)
                            } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.OTP.type) {
                                startSMSListener()
                                GlobalOTPFragment.show(
                                    json = json,
                                    encrypted = encrypted,
                                    inputList = inputList,
                                    module = module,
                                    action = action,
                                    formControl,
                                    this,
                                    supportFragmentManager
                                )

                            } else {
                                AppLogger.instance.appLog("Pay", Gson().toJson(resData))
                                Handler(Looper.getMainLooper()).postDelayed({
                                    showError(
                                        MainDialogData(
                                            title = getString(R.string.error),
                                            message = resData?.message,
                                        )
                                    )
                                }, 200)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, { it.printStackTrace() })
        )
        subscribe.add(
            baseViewModel.loading.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setLoading(it) }, { it.printStackTrace() })
        )
    }

    private fun startSMSListener() {
        val client = SmsRetriever.getClient(this)
        val task: Task<Void> = client.startSmsRetriever()
        task.addOnSuccessListener {

            AppLogger.instance.appLog(
                "${FalconHeavyActivity::class.java.simpleName}: SMS",
                "SUCCESS"
            )

        }
        task.addOnFailureListener { }
    }

    override fun navigateUp() {
        onCancel()
    }


    override fun onModule(modules: Modules?) {
        val moduleDisable = baseViewModel.dataSource.disableModule.value
//        if (!moduleDisable.isNullOrEmpty()) {
//            val isDisabled = moduleDisable.find { it?.id == modules?.moduleID }
//            if (isDisabled != null) ShowToast(this, isDisabled.message)
//            else {
//                if (modules!!.moduleURLTwo != null) {
//                    if (!TextUtils.isEmpty(modules.moduleURLTwo)) {
//                        openUrl(modules.moduleURLTwo)
//                    } else navigateTo(modules)
//                } else navigateTo(modules)
//            }
//        } else navigateTo(modules)
//

        val moduleId = modules?.moduleID
        val moduleURL = modules?.moduleURLTwo

        val isDisabled = moduleDisable?.find { it?.id == moduleId }

        if (isDisabled != null) {
            ShowToast(this, isDisabled.message)
        } else {
            if (!moduleURL.isNullOrEmpty()) {
                openUrl(moduleURL)
            } else {
                navigateTo(modules)
            }
        }


    }

    private fun navigateTransaction(modules: Modules?) {
        val i = Intent(this, TransactionCenterActivity::class.java)
        i.putExtra("transaction", ModuleDataTypeConverter().from(modules))
        startActivity(i)
    }

    private fun navigateTo(modules: Modules?) {
        AppLogger.instance.appLog("Module", Gson().toJson(modules))
        if (BaseClass.nonCaps(BaseClass.nonCaps(modules?.moduleID))
            == BaseClass.nonCaps(StaticModuleEnum.TRANSACTIONS_CENTER.type)
        )
            navigateTransaction(modules)
        else if (BaseClass.nonCaps(modules?.moduleID) == BaseClass.nonCaps(
                StaticModuleEnum.PENDING_TRANSACTIONS.type
            )
        )
            navigateToPendingTransaction(modules)
        if (BaseClass.nonCaps(modules?.moduleID) == BaseClass.nonCaps(
                StaticModuleEnum.PRIVACY_POLICY.type
            )
        )
            navigateToPrivacyPolicy(modules)
        else if (BaseClass.nonCaps(BaseClass.nonCaps(modules?.moduleID)) == BaseClass.nonCaps(
                StaticModuleEnum.VIEW_BENEFICIARY.type
            )
        )
            navigateToBeneficiary(modules)
        else if (modules!!.ModuleCategory == ModuleCategory.BLOCK.type) {
            subscribe.add(widgetViewModel.getModules(modules.moduleID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ f: List<Modules> ->
                    filterModules(f, modules)
                }) { obj: Throwable -> obj.printStackTrace() })
        } else getFormControl(modules)
    }

    private fun navigateToPrivacyPolicy(modules: Modules?) {
        PolicyAndPrivacyFragment.show(supportFragmentManager, module = modules!!)
    }

    private fun navigateToBeneficiary(modules: Modules?) {
        val i = Intent(this, BeneficiaryManageActivity::class.java)
        i.putExtra("beneficiary", ModuleDataTypeConverter().from(modules))
        startActivity(i)
    }

    private fun navigateToPendingTransaction(modules: Modules?) {
        val i = Intent(this, PendingTransactionActivity::class.java)
        i.putExtra("pending", ModuleDataTypeConverter().from(modules))
        startActivity(i)
    }

    private fun getFormControl(modules: Modules) {
        subscribe.add(widgetViewModel.getFormControl(modules.moduleID, "1")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f.toMutableList(), modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }

    private fun onNavigation(form: List<FormControl>?, modules: Modules?) {
        EventBus.getDefault().removeStickyEvent(BusData::class.java)
        EventBus.getDefault().postSticky(
            BusData(
                data = GroupForm(
                    module = modules!!,
                    form = form?.toMutableList()
                ),
                inputs = inputList, res = serverResponse.value
            )
        )
        val i = Intent(this, FalconHeavyActivity::class.java)
        startActivity(i)
    }

    private fun fetchLabelListData(
        action: ActionControls,
        modules: Modules?,
        controller: MainDisplayController,
        formControl: FormControl
    ) {
        controller.setData(LoadingState())
        val json = JSONObject()
        val encrypted = JSONObject()
        subscribe.add(
            baseViewModel.dynamicCall(
                action,
                json,
                encrypted,
                modules, this
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    try {
                        AppLogger.instance.appLog(
                            "LIST:Label", BaseClass.decryptLatest(
                                it.response,
                                widgetViewModel.storageDataSource.deviceData.value!!.device,
                                true,
                                widgetViewModel.storageDataSource.deviceData.value!!.run
                            )
                        )
                        if (it.response == StatusEnum.ERROR.type) {
                            showError(
                                MainDialogData(
                                    title = getString(R.string.error),
                                    message = getString(R.string.something_),
                                )
                            )

                        } else
                            if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                                try {
                                    val moduleData = GlobalResponseTypeConverter().to(
                                        BaseClass.decryptLatest(
                                            it.response,
                                            widgetViewModel.storageDataSource.deviceData.value!!.device,
                                            true,
                                            widgetViewModel.storageDataSource.deviceData.value!!.run
                                        )
                                    )
                                    AppLogger.instance.appLog(
                                        "LIST:Label",
                                        Gson().toJson(moduleData)
                                    )
                                    if (BaseClass.nonCaps(moduleData?.status)
                                        == StatusEnum.SUCCESS.type
                                    ) {
                                        val values = formControl.controlValue?.split(",")
                                        val data = moduleData?.data?.get(0)
                                        controller.setData(LoadingState())
                                        controller.setData(LabelData(data?.get(values!![1])))
                                        userInput(
                                            InputData(
                                                name = formControl.controlText,
                                                key = formControl.serviceParamID,
                                                value = data!![values!![0]],
                                                encrypted = formControl.isEncrypted,
                                                mandatory = formControl.isMandatory,
                                                linked = !formControl.linkedToControl.isNullOrBlank()
                                            )
                                        )

                                    } else if (BaseClass.nonCaps(moduleData?.status)
                                        == StatusEnum.TOKEN.type
                                    ) {
                                        InfoFragment.showDialog(supportFragmentManager, this)
                                    } else {
                                        showError(
                                            MainDialogData(
                                                title = getString(R.string.error),
                                                message = moduleData?.message,
                                            )
                                        )
                                    }
                                } catch (e: Exception) {
                                    controller.setData(Nothing())
                                    showError(
                                        MainDialogData(
                                            title = getString(R.string.error),
                                            message = getString(R.string.something_),
                                        )
                                    )
                                    e.printStackTrace()
                                }
                            }
                    } catch (e: Exception) {
                        controller.setData(Nothing())
                        showError(
                            MainDialogData(
                                title = getString(R.string.error),
                                message = getString(R.string.something_),
                            )
                        )
                        e.printStackTrace()
                    }
                }, { it.printStackTrace() })
        )
    }

    override fun onLabelList(
        view: EpoxyRecyclerView?,
        formControl: FormControl?,
        modules: Modules?
    ) {
        val controller = MainDisplayController(this)
        view?.setController(controller)
        subscribe.add(
            widgetViewModel.getActionControlCID(formControl!!.controlID).subscribe({
                if (it.isNotEmpty()) {
                    val action = it.first { a -> a.moduleID == formControl.moduleID }
                    if (action != null)
                        lifecycleScope.launch(Dispatchers.Main) {
                            fetchLabelListData(action, modules, controller, formControl)
                        }
                    else controller.setData(null)
                }
            }, { it.printStackTrace() })
        )
    }

    override fun onQRCode(
        preview: BlockCardReaderLayoutBinding?,
        formControl: FormControl?,
        modules: Modules?
    ) {
        scanControl = formControl!!
        this.modules = modules!!
        preview?.imageButton?.setOnClickListener {
            scanQrCode.launch(null)
        }

    }

    override fun onMenuItem() {
        RecentFragment.showDialog(supportFragmentManager, moduleDataRes.value!!)
    }

    override fun openUrl(url: String?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun onList(
        formControl: FormControl?,
        layout: EpoxyRecyclerView?, modules: Modules?
    ) {
        val controller = MainDisplayController(this)
        layout?.setController(controller)

        try {
            Handler(Looper.getMainLooper()).postDelayed({
                if (listState.isNotEmpty()) {
                    try {
                        val s = listState.single { it.id == formControl?.actionID }
                        controller.setData(s.data)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        getListAction(formControl, modules, controller)
                    }

                } else getListAction(formControl, modules, controller)
            }, 150)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getListAction(
        control: FormControl?,
        modules: Modules?,
        controller: MainDisplayController
    ) {
        subscribe.add(
            widgetViewModel.getActionControlCID(control!!.controlID).subscribe({
                if (it.isNotEmpty()) {
                    val action = it.first { a -> a.moduleID == control.moduleID }
                    if (action != null)
                        lifecycleScope.launch(Dispatchers.Main) {
                            fetchList(action, modules, controller, control)
                        }
                    else controller.setData(null)
                }
            }, { it.printStackTrace() })
        )
    }

    private fun fetchList(
        action: ActionControls?,
        modules: Modules?,
        layout: MainDisplayController,
        control: FormControl
    ) {
        try {
            layout.setData(LoadingState())
            val json = JSONObject()
            val encrypted = JSONObject()
            subscribe.add(
                baseViewModel.dynamicCall(
                    action,
                    json,
                    encrypted,
                    modules, this
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        AppLogger.instance.appLog(
                            "LIST", BaseClass.decryptLatest(
                                it.response,
                                widgetViewModel.storageDataSource.deviceData.value!!.device,
                                true,
                                widgetViewModel.storageDataSource.deviceData.value!!.run
                            )
                        )
                        if (it.response == StatusEnum.ERROR.type) {
                            showError(
                                MainDialogData(
                                    title = getString(R.string.error),
                                    message = getString(R.string.something_),
                                )
                            )
                        } else
                            if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                                try {
                                    val moduleData = GlobalResponseTypeConverter().to(
                                        BaseClass.decryptLatest(
                                            it.response,
                                            widgetViewModel.storageDataSource.deviceData.value!!.device,
                                            true,
                                            widgetViewModel.storageDataSource.deviceData.value!!.run
                                        )
                                    )
                                    AppLogger.instance.appLog("LIST", Gson().toJson(moduleData))
                                    if (BaseClass.nonCaps(moduleData?.status)
                                        == StatusEnum.SUCCESS.type
                                    ) {

                                        val s =
                                            DisplayData(
                                                moduleData?.data!!.toMutableList(),
                                                control,
                                                modules
                                            )
                                        layout.setData(s)
                                        listState.add(
                                            DisplayState(
                                                id = action?.actionID!!,
                                                data = s
                                            )
                                        )
                                    } else if (BaseClass.nonCaps(moduleData?.status)
                                        == StatusEnum.TOKEN.type
                                    ) {
                                        InfoFragment.showDialog(supportFragmentManager, this)

                                    } else {
                                        showError(
                                            MainDialogData(
                                                title = getString(R.string.error),
                                                message = moduleData?.message,
                                            )
                                        )
                                    }
                                } catch (e: Exception) {
                                    layout.setData(Nothing())
                                    showError(
                                        MainDialogData(
                                            title = getString(R.string.error),
                                            message = getString(R.string.something_),
                                        )
                                    )
                                    e.printStackTrace()
                                }
                            }
                    }, { it.printStackTrace() })
            )
        } catch (e: Exception) {
            layout.setData(Nothing())
            showError(
                MainDialogData(
                    title = getString(R.string.error),
                    message = getString(R.string.something_),
                )
            )
            e.printStackTrace()
        }
    }

    override fun bioPayment(view: TextInputEditText?) {
        if (isBiometric()) {
            authenticateTo(object : BioInterface {
                override fun onPin(pin: String) {
                    view?.setText(pin)
                }
            })
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

    private fun showBiometricLoginOption() {
        val data = widgetViewModel.storageDataSource.iv.value
        val cipher = cryptographyManager.getInitializedCipherForDecryption(
            BaseClass.decode64(Keys().secretKey()),
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

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        processData(result.cryptoObject)
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) {

    }

    override fun listDataServer(
        epoxy: EpoxyRecyclerView?,
        formControl: FormControl?,
        modules: Modules?
    ) {
        standingController = MainDisplayController(this)
        epoxy?.setController(standingController)
        subscribe.add(
            widgetViewModel.getActionControlCID(formControl!!.controlID).subscribe({
                if (it.isNotEmpty()) {
                    val action = it.first { a -> a.moduleID == formControl.moduleID }
                    if (action != null)
                        lifecycleScope.launch(Dispatchers.Main) {
                            fetchServerList(action, modules, standingController, formControl)
                        }
                    else standingController.setData(null)
                }
            }, { it.printStackTrace() })
        )
    }

    private fun fetchServerList(
        action: ActionControls,
        modules: Modules?,
        controller: MainDisplayController?,
        formControl: FormControl
    ) {
        controller?.setData(LoadingState())
        val json = JSONObject()
        val encrypted = JSONObject()
        subscribe.add(
            baseViewModel.dynamicCall(
                action,
                json,
                encrypted,
                modules, this
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    try {
                        AppLogger.instance.appLog(
                            "LIST", BaseClass.decryptLatest(
                                it.response,
                                widgetViewModel.storageDataSource.deviceData.value!!.device,
                                true,
                                widgetViewModel.storageDataSource.deviceData.value!!.run
                            )
                        )
                        if (it.response == StatusEnum.ERROR.type) {
                            showError(
                                MainDialogData(
                                    title = getString(R.string.error),
                                    message = getString(R.string.something_),
                                )
                            )
                        } else
                            if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                                try {
                                    val moduleData = StandingResponseTypeConverter().to(
                                        BaseClass.decryptLatest(
                                            it.response,
                                            widgetViewModel.storageDataSource.deviceData.value!!.device,
                                            true,
                                            widgetViewModel.storageDataSource.deviceData.value!!.run
                                        )
                                    )
                                    AppLogger.instance.appLog("LIST", Gson().toJson(moduleData))
                                    if (BaseClass.nonCaps(moduleData?.status)
                                        == StatusEnum.SUCCESS.type
                                    ) {


                                        AppLogger.instance.appLog(
                                            "LIST:Data",
                                            Gson().toJson(moduleData?.data)
                                        )


                                        controller?.setData(
                                            StandingOrderList(
                                                list = moduleData?.data,
                                                module = modules, formControl = formControl
                                            ) as AppData
                                        )


                                    } else if (BaseClass.nonCaps(moduleData?.status)
                                        == StatusEnum.TOKEN.type
                                    ) {
                                        InfoFragment.showDialog(supportFragmentManager, this)

                                    } else {
                                        controller?.setData(Nothing())
                                        showError(
                                            MainDialogData(
                                                title = getString(R.string.error),
                                                message = moduleData?.message,
                                            )
                                        )
                                    }
                                } catch (e: Exception) {
                                    controller?.setData(Nothing())
                                    showError(
                                        MainDialogData(
                                            title = getString(R.string.error),
                                            message = getString(R.string.something_),
                                        )
                                    )
                                    e.printStackTrace()
                                }
                            }
                    } catch (e: Exception) {
                        controller?.setData(Nothing())
                        showError(
                            MainDialogData(
                                title = getString(R.string.error),
                                message = getString(R.string.something_),
                            )
                        )
                        e.printStackTrace()
                    }
                }, { it.printStackTrace() })
        )
    }

    override fun deleteStandingOrder(
        formControl: FormControl?,
        modules: Modules?,
        standingOrder: StandingOrder?
    ) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_standing))
            .setMessage(getString(R.string.delete_s_message))
            .setPositiveButton(
                getString(R.string.delete)
            ) { _, i ->
                subscribe.add(
                    widgetViewModel.getActionControlCID("DELETE").subscribe({
                        if (it.isNotEmpty()) {
                            val action = it.first { a -> a.moduleID == formControl!!.moduleID }
                            lifecycleScope.launch(Dispatchers.Main) {
                                onDeleteStandingOrder(modules, action, formControl!!, standingOrder)
                            }
                        }
                    }, { it.printStackTrace() })
                )
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { _, i -> }
            .show()
    }

    private fun onDeleteStandingOrder(
        modules: Modules?,
        action: ActionControls?,
        formControl: FormControl,
        standingOrder: StandingOrder?
    ) {
        val json = JSONObject()
        setLoading(true)
        json.put("INFOFIELD1", standingOrder?.SOID)
        json.put("INFOFIELD2", standingOrder?.serviceID)
        subscribe.add(
            baseViewModel.dynamicCall(
                action,
                json,
                JSONObject(),
                modules,
                this,
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onStandingDeleteSuccess(it, modules, formControl)
                }, {
                    setLoading(false)
                    showError(
                        MainDialogData(
                            title = getString(R.string.error),
                            message = getString(R.string.unable_to_delete)
                        )
                    )
                    it.printStackTrace()
                })
        )

    }

    override fun timeOut() {
        onCancel()
    }

    private fun onStandingDeleteSuccess(
        it: DynamicResponse?,
        modules: Modules?,
        formControl: FormControl
    ) {
        try {
            setLoading(false)
            AppLogger.instance.appLog(
                "Delete", BaseClass.decryptLatest(
                    it?.response,
                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                    true,
                    widgetViewModel.storageDataSource.deviceData.value!!.run
                )
            )
            if (it?.response == StatusEnum.ERROR.type) {
                showError(
                    MainDialogData(
                        title = getString(R.string.error),
                        message = getString(R.string.something_)
                    )
                )
            } else
                if (BaseClass.nonCaps(it?.response) != StatusEnum.ERROR.type) {
                    try {
                        val moduleData = StandingResponseTypeConverter().to(
                            BaseClass.decryptLatest(
                                it?.response,
                                widgetViewModel.storageDataSource.deviceData.value!!.device,
                                true,
                                widgetViewModel.storageDataSource.deviceData.value!!.run
                            )
                        )
                        AppLogger.instance.appLog("Standing", Gson().toJson(moduleData))
                        if (BaseClass.nonCaps(moduleData?.status)
                            == StatusEnum.SUCCESS.type
                        ) {
                            if (moduleData!!.data.isNullOrEmpty()) {
                                standingController.setData(Nothing())
                                setSuccess(moduleData.message)
                            } else {

                                setSuccess(moduleData.message!!, null)
                                standingController.setData(
                                    StandingOrderList(
                                        list = moduleData.data,
                                        module = modules, formControl = formControl
                                    ) as AppData
                                )

                            }
                        } else if (BaseClass.nonCaps(moduleData?.status)
                            == StatusEnum.TOKEN.type
                        ) {
                            InfoFragment.showDialog(supportFragmentManager, this)
                        } else {
                            showError(
                                MainDialogData(
                                    title = getString(R.string.error),
                                    message = moduleData?.message
                                )
                            )
                        }
                    } catch (e: Exception) {
                        showError(
                            MainDialogData(
                                title = getString(R.string.error),
                                message = getString(R.string.something_)
                            )
                        )
                        e.printStackTrace()
                    }
                }
        } catch (e: Exception) {
            showError(
                MainDialogData(
                    title = getString(R.string.error),
                    message = getString(R.string.something_)
                )
            )
            e.printStackTrace()
        }
    }


    override fun onDisplay(
        formControl: FormControl?,
        modules: Modules?,
        data: HashMap<String, String>?
    ) {
        AppLogger.instance.appLog(
            "${FalconHeavyActivity::class.java.simpleName}:Display:click",
            Gson().toJson(formControl)
        )
        if (BaseClass.nonCaps(modules?.moduleID) == BaseClass.nonCaps("Religion")
            && BaseClass.nonCaps(formControl!!.linkedToControl) == BaseClass.nonCaps("ONLINEBIBLETAB")
        ) {
            val values = formControl.controlValue?.split(",")
            val form = mutableListOf<FormControl>()
            form.add(
                FormControl(
                    moduleID = formControl.moduleID,
                    controlID = "Display",
                    controlText = getString(R.string.verse),
                    controlType = ControlTypeEnum.TEXTVIEW.type,
                    controlFormat = ControlFormatEnum.JSON.type,
                    displayOrder = formControl.displayOrder!!.toDouble().minus(1),
                    actionType = formControl.actionType,
                    serviceParamID = formControl.serviceParamID,
                    displayControl = formControl.displayControl,
                    isEncrypted = formControl.isEncrypted,
                    isMandatory = formControl.isMandatory,
                    language = baseViewModel.dataSource.language.value
                )
            )
            userInput(
                InputData(
                    name = formControl.controlText,
                    key = values?.get(1),
                    value = data!![values!![0]],
                    encrypted = false,
                    mandatory = true,
                    linked = !formControl.linkedToControl.isNullOrBlank()
                )
            )
            serverResponse.value = DynamicAPIResponse(
                formField = mutableListOf(
                    FormField(
                        controlID = "JSON",
                        controlValue = Gson().toJson(mutableListOf(data))
                    )
                )
            )
            val sequence = formControl.formSequence?.toInt()?.plus(1).toString()
            subscribe.add(widgetViewModel.getFormControl(modules?.moduleID, sequence)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ f: List<FormControl> ->
                    form.addAll(f)
                    setFormNavigation(form, modules)
                }) { obj: Throwable -> obj.printStackTrace() })
        }
    }

    override fun onCancel() {
        if (currenct == "PAYMENTCONFIRMATIONFORM")
            LogoutFeedback.show(object : AppCallbacks {
                override fun onDialog() {
                    val openMainActivity =
                        Intent(this@FalconHeavyActivity, MainActivity::class.java)
                    openMainActivity.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
                    startActivityIfNeeded(openMainActivity, 0)
                    finish()
                }
            }, supportFragmentManager)
        else {
            val openMainActivity = Intent(this, MainActivity::class.java)
            openMainActivity.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivityIfNeeded(openMainActivity, 0)
            finish()
        }
    }


    override fun onContactSelect(view: AutoCompleteTextView?) {
        lifecycleScope.launch {
            requestContactsPermission(object : AppCallbacks {
                override fun setContact(contact: String?) {
                    view?.setText(contact)
                }
            })
        }
    }


    override fun onImageSelect(imageView: ImageView?, data: FormControl?) {
        widgetViewModel.storageDataSource.deleteIDDetails()
        if (data?.controlID == "IDFRONT") {

            widgetViewModel.storageDataSource.onIDDetails.asLiveData()
                .observe(this@FalconHeavyActivity) {
                    if (it != null) {
                        Glide.with(this@FalconHeavyActivity)
                            .load(convert(it.id!!.image))
                            .into(imageView!!)
                        this@FalconHeavyActivity.userInput(
                            InputData(
                                name = data.controlText,
                                key = data.serviceParamID,
                                value = Base64.encodeToString(
                                    compress(it.id.image),
                                    Base64.DEFAULT
                                ),
                                encrypted = data.isEncrypted,
                                mandatory = data.isMandatory,
                                linked = !data.linkedToControl.isNullOrBlank()
                            )
                        )

                        this@FalconHeavyActivity.userInput(
                            InputData(
                                name = "Android",
                                key = "INFOFIELD1",
                                value = "ANDROID-OCR",
                                encrypted = data.isEncrypted,
                                mandatory = data.isMandatory,
                                linked = !data.linkedToControl.isNullOrBlank()
                            )
                        )


                        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        val outputFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        val date: Date? = it.data?.dob?.let { it1 -> inputFormat.parse(it1) }
                        val formattedDate = date?.let { it1 -> outputFormat.format(it1) }

                        val map = hashMapOf<String, String?>()
                        map["FirstName"] = it.data?.names
                        map["LastName"] = it.data?.otherName
                        map["SurName"] = it.data?.surname
                        map["GivenName"] = it.data?.names
                        map["Gender"] = if (it.data?.gender == "MALE") "M" else "F"
                        map["DateOfBirth"] = formattedDate
                        map["NIN"] = it.data?.idNo

                        setOCRFields(it.data)


                        serverResponse.value = DynamicAPIResponse(
                            formField = mutableListOf(
                                FormField(
                                    controlID = "JSON",
                                    controlValue = Gson().toJson(mutableListOf(map))
                                )
                            )
                        )
                    }
                }
            IcebergSDK.Builder(this@FalconHeavyActivity)
                .ActionType("idFront")
                .Country("UGANDA")
                .ScanDoneClass(OCRResultActivity::class.java)
                .AppName(Constants.Data.APP_NAME)
                .init()
        } else onImagePicker(object : AppCallbacks {
            override fun onImage(bitmap: Bitmap?) {
                this@FalconHeavyActivity.userInput(
                    InputData(
                        name = data?.controlText,
                        key = data?.serviceParamID,
                        value = Base64.encodeToString(
                            compress(convert(bitmap!!)),
                            Base64.DEFAULT
                        ),
                        encrypted = data?.isEncrypted!!,
                        mandatory = data.isMandatory,
                        linked = !data.linkedToControl.isNullOrBlank()
                    )
                )
                imageView?.setImageBitmap(bitmap)
            }
        }, 1, 1)
    }

    private fun setOCRFields(map: OCRData?) {

        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date: Date? = map?.dob?.let { it1 -> inputFormat.parse(it1) }
        val formattedDate = date?.let { it1 -> outputFormat.format(it1) }

        this@FalconHeavyActivity.userInput(
            InputData(
                name = "SurName",
                key = "INFOFIELD7",
                value = map?.surname,
                encrypted = false,
                mandatory = true,
                linked = false
            )
        )

        this@FalconHeavyActivity.userInput(
            InputData(
                name = "GivenName",
                key = "INFOFIELD8",
                value = "${map?.names} ${map?.otherName}",
                encrypted = false,
                mandatory = true,
                linked = false
            )
        )

        this@FalconHeavyActivity.userInput(
            InputData(
                name = "DateOfBirth",
                key = "INFOFIELD32",
                value = formattedDate,
                encrypted = false,
                mandatory = true,
                linked = false
            )
        )

        this@FalconHeavyActivity.userInput(
            InputData(
                name = "DateOfBirth",
                key = "INFOFIELD10",
                value = formattedDate,
                encrypted = false,
                mandatory = true,
                linked = false
            )
        )



        this@FalconHeavyActivity.userInput(
            InputData(
                name = "NIN",
                key = "INFOFIELD6",
                value = map?.idNo,
                encrypted = false,
                mandatory = true,
                linked = false
            )
        )

        this@FalconHeavyActivity.userInput(
            InputData(
                name = "DOE",
                key = "INFOFIELD12",
                value = map?.expires,
                encrypted = false,
                mandatory = true,
                linked = false
            )
        )

        val gender = if (map?.gender == "MALE") "M" else "F"
        this@FalconHeavyActivity.userInput(
            InputData(
                name = "Gender",
                key = "INFOFIELD9",
                value = gender,
                encrypted = false,
                mandatory = true,
                linked = false
            )
        )

        val title = if (gender == "M") "Mr." else "Ms."
        this@FalconHeavyActivity.userInput(
            InputData(
                name = "TITLE",
                key = "INFOFIELD11",
                value = title,
                encrypted = false,
                mandatory = true,
                linked = false
            )
        )

    }

    private fun onImagePicker(callbacks: AppCallbacks, x: Int, y: Int) {
        ImagePicker.clearCache(this)
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions(x, y, callbacks)
                    } else if (report.isAnyPermissionPermanentlyDenied) {
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
            onCancel()
        }
        builder.show()
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
            ),
            REQUEST_CAMERA_PERMISSION
        )
    }

    companion object {
        private const val REQUEST_READ_CONTACTS_PERMISSION = 0
        private const val REQUEST_CAMERA_PERMISSION = 405
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        baseViewModel.interactionDataSource.onUserInteracted()
    }


    override fun viewStandingOrder(standingOrder: StandingOrder?) {
        StandingOrderDetailsFragment.newInstance(standingOrder, supportFragmentManager)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_READ_CONTACTS_PERMISSION -> onContactPicker(callback!!)
        }
    }

    override fun onCustom(pin: TextInputEditText?, max: Int) {
        baseViewModel.pin.observe(this) {
            val builder = StringBuilder()
            pinStack = it
            for (s in pinStack) {
                if (builder.length <= max)
                    builder.append(s)
            }
            pin?.setText(builder)
        }
        pin?.setOnClickListener {
            CustomKeyboard.instance(
                supportFragmentManager,
                this@FalconHeavyActivity
            )
        }
    }

    override fun onType(data: CustomKeyData?) {
        when (data?.type) {
            KeyFunctionEnum.Push -> {
                pinStack.push(data.str)
                baseViewModel.pin.value = pinStack
            }

            KeyFunctionEnum.Pop -> {
                if (pinStack.isNotEmpty()) {
                    pinStack.pop()
                    baseViewModel.pin.value = pinStack
                }

            }

            KeyFunctionEnum.Clear -> {
                if (pinStack.isNotEmpty()) {
                    pinStack.clear()
                    baseViewModel.pin.value = pinStack
                }
            }

            else -> {
                AppLogger.instance.appLog(
                    FalconHeavyActivity::class.java.simpleName,
                    "Nothing to do"
                )
            }
        }
    }

    override fun registerEvent() {
        EventBus.getDefault().register(this)
    }

    override fun unregisterEvent() {
        EventBus.getDefault().unregister(this)
    }

    override fun onStop() {
        unregisterEvent()
        super.onStop()
    }


}