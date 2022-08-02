package com.craft.silicon.centemobile.view.fragment.go.steps

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.room.TypeConverter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.ToolbarEnum
import com.craft.silicon.centemobile.data.model.converter.DynamicAPIResponseConverter
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.FragmentIDBinding
import com.craft.silicon.centemobile.util.*
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.image.convert
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.LoadingFragment
import com.craft.silicon.centemobile.view.ep.adapter.NameBaseAdapter
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import kotlin.math.abs

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
    private lateinit var stateData: IDDetails
    private var ocrData: OCRData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        activity?.onBackPressedDispatcher?.addCallback(this,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    ShowAlertDialog().showDialog(
//                        requireContext(),
//                        getString(R.string.exit_registration),
//                        getString(R.string.proceed_registration),
//                        this@IDFragment
//                    )
//                }
//
//            }
//        )
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
        setToolTitle()
        return binding.root.rootView
    }

    private fun setToolTitle() {
        var state = ToolbarEnum.EXPANDED

        binding.collapsedLay.apply {
            setCollapsedTitleTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.poppins_medium
                )
            )
            setExpandedTitleTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.poppins_bold
                )
            )
        }

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (state !== ToolbarEnum.EXPANDED) {
                    state =
                        ToolbarEnum.EXPANDED
                    binding.collapsedLay.title = getString(R.string.national_id)

                }
            } else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                if (state !== ToolbarEnum.COLLAPSED) {
                    val title = getString(R.string.national_id)
                    title.replace("\n", " ")
                    state =
                        ToolbarEnum.COLLAPSED
                    binding.collapsedLay.title = title
                }
            }
        }
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
        val titles = requireContext().resources.getStringArray(R.array.title)
        val adapter = NameBaseAdapter(requireContext(), 0, titles.toMutableList())
        binding.titleLay.autoEdit.setAdapter(adapter)
    }

    override fun setOnClick() {
        binding.idLay.avatar.setOnClickListener {
            imageSelector(ImageSelector.ID)
        }
        binding.signatureLay.avatar.setOnClickListener {
            imageSelector(ImageSelector.SIGNATURE)
        }
        binding.selfieLay.avatar.setOnClickListener {
            imageSelector(ImageSelector.SELFIE)
        }
        binding.buttonNext.setOnClickListener(this)
        binding.buttonBack.setOnClickListener(this)
    }

    override fun setBinding() {
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
        binding.shimmerContainer.visibility = View.GONE
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


    override fun imageSelector(selector: ImageSelector?) {
        imageLive = selector
        (requireActivity() as MainActivity).onImagePicker(this)
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
                uploadOCR()
            }
        } else if (p == binding.buttonBack)
            pagerData?.onBack(2)
    }

    private fun uploadOCR() {
        val json = JSONObject()
        json.put("INFOFIELD3", id?.image)
        json.put("INFOFIELD4", signature?.image)
        json.put("INFOFIELD5", selfie?.image)

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
                        if (it.response == StatusEnum.ERROR.type) {
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

                                        if (resData?.display!!.isNotEmpty()) {
                                            val display = resData.display?.first()
                                            val name = display?.get("First Name")?.split(" ")
                                            val data = OCRData(
                                                names = name!![0],
                                                surname = display["SurName"]!!,
                                                dob = display["Date Of Birth"]!!,
                                                idNo = display["National ID Number"]!!,
                                                otherName = name[1]
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
    val names: String,
    @field:SerializedName("F-3")
    @field:Expose
    val surname: String,
    @field:SerializedName("F-14")
    @field:Expose
    val idNo: String,
    @field:SerializedName("F-11")
    @field:Expose
    val dob: String,
    @field:SerializedName("F-6")
    @field:Expose
    val otherName: String,
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

