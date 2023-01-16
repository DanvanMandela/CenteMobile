package com.elmacentemobile.view.fragment.go.steps

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.TextUtils
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.room.TypeConverter
import com.elmacentemobile.R
import com.elmacentemobile.data.model.converter.DynamicAPIResponseConverter
import com.elmacentemobile.data.model.ocr.ImageRequestData
import com.elmacentemobile.data.model.ocr.ImageResponseData
import com.elmacentemobile.data.model.ocr.ImageResponseTypeConverter
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.databinding.FragmentIDBinding
import com.elmacentemobile.util.*
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.image.convert
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.dialog.AlertDialogFragment
import com.elmacentemobile.view.dialog.DialogData
import com.elmacentemobile.view.dialog.LoadingFragment
import com.elmacentemobile.view.ep.adapter.NameBaseAdapter
import com.elmacentemobile.view.ep.data.NameBaseData
import com.elmacentemobile.view.fragment.go.PagerData
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.elmacentemobile.view.model.WorkStatus
import com.elmacentemobile.view.model.WorkerViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.koushikdutta.ion.Ion
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IDFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class IDFragment : Fragment(), AppCallbacks, View.OnClickListener, OnAlertDialog {

    private lateinit var binding: FragmentIDBinding
    private var imageLive: ImageSelector? = null
    private var selfie: ImageData? = null
    private var id: ImageData? = null
    private var signature: ImageData? = null

    private val subscribe = CompositeDisposable()
    private val baseViewModel: BaseViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val workViewModel: WorkerViewModel by viewModels()

    private val composite = CompositeDisposable()

    private var isNSSF = false

    private lateinit var stateData: IDDetails
    private var ocrData: OCRData? = null


    private lateinit var product: CustomerProduct


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                ShowAlertDialog().showDialog(
                    requireContext(),
                    getString(R.string.exit_registration),
                    getString(R.string.proceed_registration),
                    this
                )
                true
            } else false
        }
    }

    private fun requestPermissions() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.CAMERA
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


    override fun onPositive() {
        saveState()
        pagerData?.currentPosition()
        Handler(Looper.getMainLooper()).postDelayed({
            (requireActivity() as MainActivity).provideNavigationGraph()
                .navigate(widgetViewModel.navigation().navigateLanding())
        }, 100)
    }

    override fun onNegative() {
        pagerData?.clearState()
        (requireActivity() as MainActivity).provideNavigationGraph()
            .navigate(widgetViewModel.navigation().navigateLanding())
    }

    private fun saveState() {
        statePersist()
        baseViewModel.dataSource.setIDDetails(stateData)
    }

    private fun statePersist() {
        stateData = IDDetails(
            id = id,
            selfie = selfie,
            signature = signature,
            data = ocrData,
            title = binding.titleLay.autoEdit.editableText.toString()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIDBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        setTitle()
        setToolbar()
        requestPermissions()
        return binding.root.rootView
    }


    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            ShowAlertDialog().showDialog(
                requireContext(),
                getString(R.string.exit_registration),
                getString(R.string.proceed_registration),
                this
            )
        }
    }

    private fun setTitle() {
        val titles = mutableListOf<NameBaseData>()
        val sData = requireContext().resources.getStringArray(R.array.title)
        sData.forEach {
            titles.add(
                NameBaseData(
                    text = it,
                    id = it
                )
            )
        }

        val adapter = NameBaseAdapter(requireContext(), 0, titles.toMutableList())
        binding.titleLay.autoEdit.setAdapter(adapter)
        binding.titleLay.autoEdit.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
                if (product.product?.value == "32112" && p2 == 0) {
                    showError("CenteSupaWoman Account ${getString(R.string.reserved_for_women)}")
                    binding.titleLay.autoEdit.setText("")
                }
            }
    }

    override fun setOnClick() {
        binding.idLay.avatar.setOnClickListener {
            imageSelector(ImageSelector.ID, 3, 2)
        }
        binding.signatureLay.avatar.setOnClickListener {
            imageSelector(ImageSelector.SIGNATURE, 3, 2)
        }
        binding.selfieLay.avatar.setOnClickListener {
            imageSelector(ImageSelector.SELFIE, 1, 1)
        }
        binding.buttonNext.setOnClickListener(this)
        binding.buttonBack.setOnClickListener(this)
    }

    override fun setBinding() {
        baseViewModel.dataSource.customerProduct.asLiveData().observe(viewLifecycleOwner) {
            if (it != null)
                product = it
        }
        binding.lifecycleOwner = viewLifecycleOwner
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.mainLay.visibility = VISIBLE
            stopShimmer()
            setStep()
            setState()
        }, animationDuration.toLong())
    }

    private fun setState() {
        val sData = baseViewModel.dataSource.onIDDetails.value
        if (sData != null) {
            if (sData.selfie != null) sateSelfie(sData.selfie)
            if (sData.id != null) stateId(sData.id)
            if (sData.signature != null) stateSignature(sData.signature)
            if (sData.title != null) stateTitle(sData.title)
        }
    }

    private fun stateTitle(s: String) {
        binding.titleLay.autoEdit.setText(s, false)
    }

    private fun stateSignature(data: ImageData) {
        signature = data
        binding.signatureLay.avatar.setImageBitmap(convert(data.image))
    }

    private fun stateId(data: ImageData) {
        id = data
        binding.idLay.avatar.setImageBitmap(convert(data.image))
    }

    private fun sateSelfie(data: ImageData) {
        selfie = data
        binding.selfieLay.avatar.setImageBitmap(convert(data.image))
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(20, true)
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = GONE
    }

    override fun onImage(bitmap: Bitmap?) {
        if (imageLive != null) {
            when (imageLive) {
                ImageSelector.SELFIE -> {
                    selfie = ImageData(image = convert(bitmap!!))
                    binding.selfieLay.avatar.setImageBitmap(bitmap)
                }
                ImageSelector.ID -> {
                    id = ImageData(image = convert(bitmap!!))
                    binding.idLay.avatar.setImageBitmap(bitmap)
//                    if (product.product?.value != "32217")
//                        uploadIdImage(bitmap)//out of order
                }
                ImageSelector.SIGNATURE -> {
                    signature = ImageData(image = convert(bitmap!!))
                    binding.signatureLay.avatar.setImageBitmap(bitmap)
                }
                else -> {}
            }
        }
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.titleLay.autoEdit.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_title), true)
            false
        } else if (id == null) {
            ShowToast(requireContext(), getString(R.string.id_required), true)
            false
        } else if (selfie == null) {
            ShowToast(requireContext(), getString(R.string.selfie_required), true)
            false
        } else if (signature == null) {
            ShowToast(requireContext(), getString(R.string.signature_required), true)
            false
        } else true
    }


    override fun imageSelector(selector: ImageSelector?, x: Int, y: Int) {
        imageLive = selector
        (requireActivity() as MainActivity).onImagePicker(this, x, y)
    }

    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IDFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IDFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = IDFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.buttonNext) {
            if (validateFields()) {
                if (product.product?.value != "32217") {
                    uploadOCR()
                } else {
                    SmartLifeFragment.showDialog(this.childFragmentManager, this)
                }
            }
        } else {
            pagerData?.onBack(2)
        }
    }

    override fun onDialog() {
        //ocrData = OCRData()//TODO ORC DATA
        saveState()
        pagerData?.onNext(4)
    }

    override fun onOCR(ocrData: OCRData?) {
        this.ocrData = ocrData
        saveState()
        pagerData?.onNext(4)
    }



    private fun uploadOCR() {
        val json = JSONObject()
        json.put("INFOFIELD3", id?.image)
        json.put("INFOFIELD4", "signature?.image")
        json.put("INFOFIELD5", "selfie?.image")

        setLoading(true)
        ShowToast(requireContext(), getString(R.string.take_few_secs))
        subscribe.add(
            baseViewModel.ocr(json, requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    AppLogger.instance.appLog(
                        "OCR:", it.response!!
                    )
                    if (it.response == null) {
                        setLoading(false)
                        AlertDialogFragment.newInstance(
                            DialogData(
                                title = R.string.error,
                                subTitle = getString(R.string.unable_to_process_id),
                                R.drawable.warning_app
                            ),
                            requireActivity().supportFragmentManager
                        )
                    } else {
                        if (BaseClass.nonCaps(it.response) == StatusEnum.ERROR.type) {
                            setLoading(false)
                            AlertDialogFragment.newInstance(
                                DialogData(
                                    title = R.string.error,
                                    subTitle = getString(R.string.adjust_id_try),
                                    R.drawable.warning_app
                                ),
                                requireActivity().supportFragmentManager
                            )
                        } else {
                            try {
                                AppLogger.instance.appLog(
                                    "OCR:Response", BaseClass.decryptLatest(
                                        it.response,
                                        baseViewModel.dataSource.deviceData.value!!.device,
                                        true,
                                        baseViewModel.dataSource.deviceData.value!!.run
                                    )
                                )
                                if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                                    setLoading(false)
                                    val resData = DynamicAPIResponseConverter().to(
                                        BaseClass.decryptLatest(
                                            it.response,
                                            baseViewModel.dataSource.deviceData.value!!.device,
                                            true,
                                            baseViewModel.dataSource.deviceData.value!!.run
                                        )
                                    )
                                    AppLogger.instance.appLog("OCR:Res", Gson().toJson(resData))
                                    if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                        setLoading(false)


                                        if (resData?.formField!!.isNotEmpty()) {
                                            val map = resData.formField!!
                                            val data = OCRData(
                                                names = map.find { a -> a.controlID == "FirstName" }?.controlValue!!,
                                                surname = map.find { a -> a.controlID == "SurName" }?.controlValue!!,
                                                dob = map.find { a -> a.controlID == "DateOfBirth" }?.controlValue!!,
                                                idNo = map.find { a -> a.controlID == "NIN" }?.controlValue!!,
                                                otherName = map.find { a -> a.controlID == "GivenName" }?.controlValue!!,
                                                gender = map.find { a -> a.controlID == "Gender" }?.controlValue!!
                                            )
                                            AppLogger.instance.appLog(
                                                "OCR:DATA",
                                                Gson().toJson(data)
                                            )
                                            ocrData = data
                                            saveState()
                                            pagerData?.onNext(4)

                                        }

                                    } else if (BaseClass.nonCaps(resData?.status)
                                        == StatusEnum.TOKEN.type
                                    ) {
                                        workViewModel.routeData(
                                            viewLifecycleOwner,
                                            object : WorkStatus {
                                                override fun workDone(b: Boolean) {
                                                    setLoading(false)
                                                    if (b) uploadOCR()
                                                }
                                            })


                                    } else {
                                        setLoading(false)
                                        AlertDialogFragment.newInstance(
                                            DialogData(
                                                title = R.string.error,
                                                subTitle = resData?.message!!,
                                                R.drawable.warning_app
                                            ),
                                            requireActivity().supportFragmentManager
                                        )
                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                setLoading(false)
                                AlertDialogFragment.newInstance(
                                    DialogData(
                                        title = R.string.error,
                                        subTitle = getString(R.string.something_),
                                        R.drawable.warning_app
                                    ),
                                    requireActivity().supportFragmentManager
                                )
                            }
                        }
                    }
                },
                    {
                        setLoading(false)
                        showError(getString(R.string.something_))
                        it.printStackTrace()
                    })
        )
    }

    private fun showError(s: String) {
        AlertDialogFragment.newInstance(
            DialogData(
                title = R.string.error,
                subTitle = s,
                R.drawable.warning_app
            ),
            this.childFragmentManager
        )
    }

    private fun setLoading(b: Boolean) {
        if (b) LoadingFragment.show(this.childFragmentManager)
        else LoadingFragment.dismiss(this.childFragmentManager)
    }

    private fun uploadIdImage(bitmap: Bitmap) {
        ShowToast(requireContext(), getString(R.string.take_few_secs))
        setLoading(true)
        val imageByte = BaseClass.getBitmapBytes(bitmap)
        val urlString = "{" + "\"FormID\":\"BANKIDFRONT\"," +
                "\"Key\":\"PORTAL-FF7B-4CCA-B884-98346D5EC385\"," +
                "\"Country\":\"" + "UGANDA" + "\"," +
                "\"FileType\":\"jpg\"," +
                "\"MobileNumber\":\"" + Constants.Data.CUSTOMER_ID + "\"," +
                "\"EMailID\":\"" + "craft@gmail.com" + "\"," +
                "\"ModuleID\":\"CREATECUSTOMER\"," +
                "\"BankID\":\"" + Constants.Data.BANK_ID + "\"}"
        AppLogger.instance.appLog("${IDDetails::class.java.simpleName}: REQ", urlString)
        try {
            val imageUrl =
                Constants.BaseUrl.IMAGE_BASE_URL + URLEncoder.encode(urlString, "UTF-8")
            Ion.with(requireContext()).load("POST", imageUrl)
                .uploadProgressHandler { downloaded: Long, total: Long ->
                    val progressLive = MutableLiveData<Int?>()
                    val progress = BaseClass.calculatePercentage(
                        downloaded.toString().toDouble(),
                        total.toString().toDouble()
                    ).roundToInt()
                    progressLive.value = progress.toFloat().roundToInt()

                }.setByteArrayBody(imageByte).asString()
                .setCallback { e: Exception?, result: String? ->
                    if (e != null) {
                        e.printStackTrace()
                        setLoading(false)
                    } else {
                        assert(result != null)
                        val responseData = ImageResponseTypeConverter().to(result)!!
                        AppLogger.instance.appLog(
                            "${IDDetails::class.java.simpleName}:ID",
                            Gson().toJson(responseData)
                        )
                        processID(responseData)
                    }
                }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            setLoading(false)
        }
    }

    private fun processID(responseData: ImageResponseData) {
        val requestData = ImageRequestData(
            Constants.Data.API_KEY,
            Constants.ImageID.NATIONALID.name,
            responseData.imageUrl, "UGANDA"
        )
        workViewModel.processID(requestData, this, object : WorkStatus {
            override fun workDone(b: Boolean) {
                if (b) setLoading(false)
            }

            override fun error(p: String?) {
                setLoading(false)
                ShowToast(requireContext(), p)
            }

            override fun onOCRData(data: OCRData, b: Boolean) {
                setLoading(b)
                ocrData = data
                binding.buttonNext.visibility = VISIBLE
            }
        })

    }
}

enum class ImageSelector {
    SELFIE, ID, SIGNATURE
}

@Parcelize
data class IDDetails(
    @field:SerializedName("title")
    val title: String?,
    @field:SerializedName("id")
    val id: ImageData?,
    @field:SerializedName("signature")
    val signature: ImageData?,
    @field:SerializedName("selfie")
    @field:Expose
    val selfie: ImageData?,
    @field:SerializedName("ocr")
    @field:Expose
    var data: OCRData?
) : Parcelable

@Parcelize
data class OCRData(
    @field:SerializedName("F-5")
    @field:Expose
    val names: String?,
    @field:SerializedName("F-3")
    @field:Expose
    val surname: String?,
    @field:SerializedName("F-14")
    @field:Expose
    val idNo: String?,
    @field:SerializedName("F-11")
    @field:Expose
    val dob: String?,
    @field:SerializedName("F-6")
    @field:Expose
    val otherName: String?,
    @field:SerializedName("F-7")
    @field:Expose
    val gender: String?,
) : Parcelable

@Parcelize
data class ImageData(
    @field:SerializedName("image")
    val image: String
) : Parcelable


data class OCRRequest(
    @field:SerializedName("IDType")
    @field:Expose
    val iDType: String?,
    @field:SerializedName("IDNumber")
    @field:Expose
    val iDNumber: String?,
    @field:SerializedName("IDFront")
    @field:Expose
    val iDFront: String?,
    @field:SerializedName("Selfie")
    @field:Expose
    val selfie: String?,

    @field:SerializedName("IDBack")
    @field:Expose
    val iDBack: String?
) {
    @field:SerializedName("UserID")
    @field:Expose
    val userID: String = "SC"

    @field:SerializedName("Password")
    @field:Expose
    val password: String = "R00PK1RANI"
}


class IDDetailsConverter {
    @TypeConverter
    fun from(data: IDDetails?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, IDDetails::class.java)
    }

    @TypeConverter
    fun to(data: String?): IDDetails? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, IDDetails::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}


class OCRConverter {
    @TypeConverter
    fun from(data: OCRData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, OCRData::class.java)
    }

    @TypeConverter
    fun to(data: String?): OCRData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, OCRData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}
