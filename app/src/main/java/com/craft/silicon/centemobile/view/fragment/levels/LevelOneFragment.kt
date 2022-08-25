package com.craft.silicon.centemobile.view.fragment.levels

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.airbnb.epoxy.EpoxyRecyclerView
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.control.FormNavigation
import com.craft.silicon.centemobile.data.model.converter.*
import com.craft.silicon.centemobile.data.model.dynamic.DynamicAPIResponse
import com.craft.silicon.centemobile.data.model.dynamic.DynamicDataResponse
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.ModuleCategory
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.BlockCardReaderLayoutBinding
import com.craft.silicon.centemobile.databinding.FragmentLevelOneBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.JSONUtil
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.callbacks.Confirm
import com.craft.silicon.centemobile.util.image.convert
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.binding.*
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.InfoFragment
import com.craft.silicon.centemobile.view.dialog.MainDialogData
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment
import com.craft.silicon.centemobile.view.dialog.confirm.ConfirmFragment
import com.craft.silicon.centemobile.view.dialog.display.DisplayDialogFragment
import com.craft.silicon.centemobile.view.dialog.receipt.ReceiptFragment
import com.craft.silicon.centemobile.view.ep.adapter.InsuranceAdapterItem
import com.craft.silicon.centemobile.view.ep.adapter.LoanAdapterItem
import com.craft.silicon.centemobile.view.ep.controller.*
import com.craft.silicon.centemobile.view.ep.data.*
import com.craft.silicon.centemobile.view.ep.data.Nothing
import com.craft.silicon.centemobile.view.fragment.auth.bio.BioInterface
import com.craft.silicon.centemobile.view.fragment.auth.bio.BiometricFragment
import com.craft.silicon.centemobile.view.fragment.dynamic.RecentFragment
import com.craft.silicon.centemobile.view.fragment.global.GlobalOTPFragment
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.craft.silicon.centemobile.view.qr.QRContent
import com.craft.silicon.centemobile.view.qr.QRResult
import com.craft.silicon.centemobile.view.qr.ScanQRCode
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "data"


/**
 * A simple [Fragment] subclass.
 * Use the [LevelOneFragment.setData] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class LevelOneFragment : Fragment(), AppCallbacks, Confirm {
    // TODO: Rename and change types of parameters
    private var data: DynamicData? = null
    private lateinit var binding: FragmentLevelOneBinding

    private val baseViewModel: BaseViewModel by viewModels()
    private val inputList = mutableListOf<InputData>()

    private val widgetViewModel: WidgetViewModel by viewModels()

    private val subscribe = CompositeDisposable()

    private var serverResponse = MutableLiveData<DynamicAPIResponse?>()
    private lateinit var scanControl: FormControl
    private lateinit var modules: Modules
    private val scanQrCode = registerForActivityResult(ScanQRCode(), ::scanSuccess)
    private val listState = mutableListOf<DisplayState>()
    private var moduleDataRes = MutableLiveData<DynamicDataResponse?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable(ARG_DATA)
            AppLogger.instance.appLog(
                this@LevelOneFragment::class.java.simpleName,
                Gson().toJson(data)
            )
        }

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                cleanLevel()
                requireActivity().onBackPressed()
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLevelOneBinding.inflate(inflater, container, false)
        setBinding()
        setDynamicInputs()
        return binding.root.rootView
    }

    private fun setDynamicInputs() {
        AppLogger.instance.appLog(
            "${LevelOneFragment::class.java.simpleName}:Dynamic:Inputs", Gson().toJson(
                inputData
            )
        )
        inputList.clear()
        if (inputData != null)
            if (inputData!!.isNotEmpty()) {
                for (filter in inputData!!) {
                    inputList.add(
                        InputData(
                            name = filter.name,
                            key = filter.key,
                            value = filter.value,
                            encrypted = filter.encrypted,
                            mandatory = filter.mandatory
                        )
                    )
                }
            }
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.callback = this
        binding.data = data
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            binding.container.setDynamic(
                callbacks = this,
                dynamic = data,
                storage = baseViewModel.dataSource
            )
        }, animationDuration.toLong())

    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun startShimmer() {
        binding.shimmerContainer.startShimmer()
    }

    override fun navigateUp() {
        inputData?.clear()
        requireActivity().onBackPressed()
    }

    private fun filterModules(f: List<Modules>, modules: Modules) {
        val filterModule = mutableListOf<Modules>()
        val moduleList =
            baseViewModel.dataSource.disableModule.value
        if (moduleList != null) {
            if (moduleList.isNotEmpty()) {
                f.forEach { s ->
                    moduleList.forEach { i ->
                        if (s.moduleID == i?.id) {
                            s.available = false
                            s.message = i?.message
                        }
                    }
                    filterModule.add(s)
                }
                navigate(
                    baseViewModel.navigationData
                        .navigateToLevelTwo(GroupModule(modules, filterModule))
                )
            } else navigate(
                baseViewModel.navigationData
                    .navigateToLevelTwo(GroupModule(modules, f.toMutableList()))
            )
        } else navigate(
            baseViewModel.navigationData
                .navigateToLevelTwo(GroupModule(modules, f.toMutableList()))
        )
    }


    override fun onServerValue(formControl: FormControl?, view: TextInputEditText?) {
        AppLogger.instance.appLog(
            "${LevelTwoFragment::class.java.simpleName} ServerForm",
            Gson().toJson(formControl)
        )
        AppLogger.instance.appLog(
            "${LevelTwoFragment::class.java.simpleName} Form",
            Gson().toJson(response)
        )
        try {
            if (response != null) {
                if (!response?.resultsData.isNullOrEmpty())
                    response?.resultsData!!.forEach {
                        if (BaseClass.nonCaps(it.controlID) == BaseClass.nonCaps(formControl?.controlID)) {
                            view?.setText(it.controlValue)
                            userInput(
                                InputData(
                                    name = formControl?.controlText,
                                    key = formControl?.serviceParamID,
                                    value = it.controlValue,
                                    encrypted = formControl?.isEncrypted!!,
                                    mandatory = formControl.isMandatory
                                )
                            )
                        }

                    }
                if (!response?.formField.isNullOrEmpty())
                    response?.formField!!.forEach {
                        if (BaseClass.nonCaps(it.controlID) == BaseClass.nonCaps(formControl?.controlID)) {
                            view?.setText(it.controlValue)
                            userInput(
                                InputData(
                                    name = formControl?.controlText,
                                    key = formControl?.serviceParamID,
                                    value = it.controlValue,
                                    encrypted = formControl?.isEncrypted!!,
                                    mandatory = formControl.isMandatory
                                )
                            )
                        }

                    }

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
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

            if (response?.formField != null) {
                val data =
                    response?.formField?.single { a -> a.controlID == formControl?.controlID }

                if (BaseClass.nonCaps(data?.controlID) == BaseClass.nonCaps("Packages")) {
                    val packages = InsuranceTypeConverter().from(data?.controlValue)
                    val adapter =
                        InsuranceAdapterItem(
                            requireContext(),
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
                                    mandatory = formControl.isMandatory
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
                            requireContext(),
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
                                    mandatory = formControl.isMandatory
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

    override fun onImageSelect(imageView: ImageView?, data: FormControl?) {
        (requireActivity() as MainActivity).onImagePicker(object : AppCallbacks {
            override fun onImage(bitmap: Bitmap?) {
                this@LevelOneFragment.userInput(
                    InputData(
                        name = data?.controlText,
                        key = data?.serviceParamID,
                        value = convert(bitmap!!),
                        encrypted = data?.isEncrypted!!,
                        mandatory = data.isMandatory
                    )
                )
                imageView?.setImageBitmap(bitmap)

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        baseViewModel.dataSource.deleteOtp()

    }

    override fun onStop() {
        super.onStop()
        cleanLevel()
    }

    override fun cleanLevel() {
        if (!inputData.isNullOrEmpty()) {
            AppLogger.instance.appLog(
                "${LevelOneFragment::class.java.simpleName}:Cleaned", Gson().toJson(
                    inputData
                )
            )
            inputData?.clear()
        }

    }


    companion object {
        private var response: DynamicAPIResponse? = null
        private var inputData: MutableList<InputData>? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param response Parameter 1.
         * @param map Parameter 2.
         * @return A new instance of fragment LevelOneFragment.
         */

        @JvmStatic
        fun setData(
            response: DynamicAPIResponse?,
            map: MutableList<InputData>?
        ) =
            LevelOneFragment().apply {
                this@Companion.response = response
                this@Companion.inputData = map
            }
    }

    override fun userInput(inputData: InputData?) {
        AppLogger.instance.appLog(
            "${LevelOneFragment::class.java.simpleName}:UserInput",
            Gson().toJson(inputData)
        )
        inputList.removeIf { a -> a.key == inputData?.key }
        inputList.add(inputData!!)
    }

    override fun clearInputData() {
        if (inputList.isNotEmpty()) inputList.clear()
    }

    override fun onContactSelect(view: AutoCompleteTextView?) {
        (requireActivity() as MainActivity).requestContactsPermission(object : AppCallbacks {
            override fun setContact(contact: String?) {
                view?.setText(contact)
            }
        })

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
            picker.show(this.childFragmentManager, picker.tag)
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
                action?.moduleID, action?.merchantID, requireContext()
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    AppLogger.instance.appLog(
                        "${LevelOneFragment::class.java.simpleName}:D:RECENT",
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
                                "${LevelOneFragment::class.java.simpleName}:E:RECENT",
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

    private fun validateModule(
        jsonObject: JSONObject,
        encrypted: JSONObject,
        modules: Modules?,
        formControl: FormControl?,
        action: ActionControls?
    ) {
        try {
            AppLogger.instance.appLog(
                "${LevelOneFragment::class.java.simpleName}:fields",
                Gson().toJson(jsonObject)
            )
            AppLogger.instance.appLog(
                "${LevelOneFragment::class.java.simpleName}:encrypted",
                Gson().toJson(encrypted)
            )
            AppLogger.instance.appLog(
                "${LevelOneFragment::class.java.simpleName}:${action?.actionType}:action",
                Gson().toJson(action)
            )
            AppLogger.instance.appLog(
                "${LevelOneFragment::class.java.simpleName}:form",
                Gson().toJson(formControl)
            )
            AppLogger.instance.appLog(
                "${LevelOneFragment::class.java.simpleName}:action",
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
                    requireActivity(),
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        try {
                            AppLogger.instance.appLog(
                                "${LevelOneFragment::class.java.simpleName}:Response",
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
                                    "${LevelOneFragment::class.java.simpleName}:Data",
                                    Gson().toJson(resData)
                                )
                                if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                    serverResponse.value = resData
                                    if (resData?.formID != null) {

                                        if (!TextUtils.isEmpty(resData.formID)
                                        ) {

                                            if (BaseClass.nonCaps(resData.formID)
                                                == BaseClass.nonCaps("PAYMENTCONFIRMATIONFORM")
                                            ) {
                                                ReceiptFragment.newInstance(
                                                    this, ReceiptList(
                                                        receipt = resData.receipt!!
                                                            .toMutableList(),
                                                        notification = resData.notifications

                                                    )
                                                )
                                                navigate(
                                                    widgetViewModel.navigation()
                                                        .navigateReceipt(
                                                            ReceiptList(
                                                                receipt = resData.receipt!!
                                                                    .toMutableList(),
                                                                notification = resData.notifications
                                                            )
                                                        )
                                                )
                                            } else if (BaseClass.nonCaps(resData.formID)
                                                == BaseClass.nonCaps("STATEMENT")
                                            ) {
                                                DisplayDialogFragment.setData(
                                                    data = resData.accountStatement
                                                )
                                                navigate(
                                                    baseViewModel
                                                        .navigationData
                                                        .navigateToDisplayDialog(
                                                            DisplayData(
                                                                display = null,
                                                                modules = modules,
                                                                form = formControl
                                                            )
                                                        )
                                                )
                                            } else if (BaseClass.nonCaps(resData.formID)
                                                == BaseClass.nonCaps("RELIGION")
                                            ) {
                                                val mData = GlobalResponseTypeConverter().to(
                                                    BaseClass.decryptLatest(
                                                        it.response,
                                                        baseViewModel.dataSource.deviceData.value!!.device,
                                                        true,
                                                        baseViewModel.dataSource.deviceData.value!!.run
                                                    )
                                                )

                                                DisplayDialogFragment.setData(
                                                    data = mData?.data
                                                )
                                                navigate(
                                                    baseViewModel
                                                        .navigationData
                                                        .navigateToDisplayDialog(
                                                            DisplayData(
                                                                display = null,
                                                                modules = modules,
                                                                form = formControl
                                                            )
                                                        )
                                                )

                                            } else {
                                                ShowToast(requireContext(), resData.message)
                                                setOnNextModule(
                                                    formControl,
                                                    resData.next,
                                                    modules,
                                                    resData.formID
                                                )
                                            }

                                        } else {
                                            SuccessDialogFragment.showDialog(
                                                DialogData(
                                                    title = R.string.success,
                                                    subTitle = resData.message!!,
                                                    R.drawable.success
                                                ),
                                                requireActivity().supportFragmentManager, this
                                            )
                                        }
                                    } else SuccessDialogFragment.showDialog(
                                        DialogData(
                                            title = R.string.success,
                                            subTitle = resData?.message!!,
                                            R.drawable.success
                                        ),
                                        requireActivity().supportFragmentManager, this
                                    )

                                } else if (BaseClass.nonCaps(resData?.status)
                                    == StatusEnum.TOKEN.type
                                ) {
                                    InfoFragment.showDialog(this.childFragmentManager)
                                } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.OTP.type) {
                                    GlobalOTPFragment.setData(
                                        json = jsonObject,
                                        encrypted = encrypted,
                                        inputList = inputList,
                                        module = modules,
                                        action = action,
                                        formControl,
                                        this,
                                    )
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        navigate(widgetViewModel.navigation().navigateToGlobalOtp())
                                    }, 200)
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

    private fun setLoading(b: Boolean) {
        if (b) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
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
            "${LevelOneFragment::class.java.simpleName}:NEXT:FORM:ID",
            formControl?.formID!!
        )
        AppLogger.instance.appLog(
            "${LevelOneFragment::class.java.simpleName}:NEXT:MODULE:ID",
            formID!!
        )
        AppLogger.instance.appLog(
            "${LevelOneFragment::class.java.simpleName}:NEXT:SEQUENCE",
            "$sequence"
        )
        subscribe.add(widgetViewModel.getFormControl(formID, sequence.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f.toMutableList(), modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }

    override fun onDisplay(formControl: FormControl?, modules: Modules?) {
        AppLogger.instance.appLog("DISPLAY:form", Gson().toJson(formControl))
        if (BaseClass.nonCaps(formControl?.controlID) == BaseClass.nonCaps("DISPLAY")) {
            if (!response?.display.isNullOrEmpty()) {
                binding.displayContainer.visibility = View.VISIBLE
                val controller = MainDisplayController(this)
                controller.setData(DisplayData(response?.display, formControl, modules))
                binding.displayContainer.setController(controller)
            }
        } else {
            if (BaseClass.nonCaps(formControl?.controlFormat)
                == BaseClass.nonCaps(ControlFormatEnum.JSON.type)
            ) {
                response?.let { setJsonFormatData(it, formControl, modules) }
            }
        }
    }

    private fun setJsonFormatData(
        data: DynamicAPIResponse?,
        form: FormControl?,
        modules: Modules?
    ) {
        try {
            if (data != null) {
                AppLogger.instance.appLog("DYNAMIC:DATA", Gson().toJson(data.rData))
                AppLogger.instance.appLog("DYNAMIC:FORM", Gson().toJson(data.formField))
                startShimmer()
                val controller = MainDisplayController(this)
                if (!data.resultsData.isNullOrEmpty()) {
                    binding.detailsContainer.visibility = View.VISIBLE
                    stopShimmer()
                    val list = data.resultsData?.single { a -> a.controlID == form?.controlID }
                    val hashMaps: ArrayList<HashMap<String, String>> =
                        JSONUtil.cleanData(list?.controlValue)

                    controller.setData(DisplayData(hashMaps, form, modules))
                    binding.detailsContainer.setController(controller)

                } else if (!data.formField.isNullOrEmpty()) {
                    binding.displayContainer.visibility = View.VISIBLE
                    stopShimmer()
                    val list = data.formField?.single { a -> a.controlID == form?.controlID }
                    val hashMaps: ArrayList<HashMap<String, String>> =
                        JSONUtil.cleanData(list?.controlValue)

                    controller.setData(DisplayData(hashMaps, form, modules))
                    binding.detailsContainer.setController(controller)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCancel() {
        navigate(widgetViewModel.navigation().navigateToHome())
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

    private fun onNavigation(form: List<FormControl>?, modules: Modules?) {
        LevelTwoFragment.setData(response = serverResponse.value, map = inputList)
        navigate(
            widgetViewModel.navigation().navigateToLevelTwo(
                GroupForm(
                    module = modules!!,
                    form = form?.toMutableList()
                )
            )
        )
    }


    private fun onBiometric(forms: MutableList<FormControl>?, modules: Modules?) {
        BiometricFragment.setData(form = forms, module = modules)
        navigate(widgetViewModel.navigation().navigationBio())
    }

    override fun globalAutoLiking(value: String?, editText: TextInputEditText?) {
        try {
            if (response != null)
                if (!response?.resultsData.isNullOrEmpty()) {
                    for (s in response?.resultsData!!) {
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

                } else if (!response?.formField.isNullOrEmpty()) {
                    for (s in response?.formField!!) {
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


    override fun onForm(formControl: FormControl?, modules: Modules?) {
        lifecycleScope.launch {
            requireActivity().hideSoftKeyboard(binding.root)
        }
        if (BaseClass.nonCaps(formControl?.controlFormat)
            == BaseClass.nonCaps(ControlFormatEnum.END.type)
        ) (requireActivity().onBackPressed())
        else {
            try {
                subscribe.add(
                    widgetViewModel.getActionControlCID(formControl?.controlID).subscribe({
                        if (it.isNotEmpty()) {
                            val merchantID = it.map { a -> a.merchantID }
                            AppLogger.instance.appLog(
                                "${LevelOneFragment::class.java.simpleName}:MERCHANT:ID",
                                Gson().toJson(merchantID)
                            )
                            val action = it.first { a -> a.moduleID == formControl?.moduleID }
                            AppLogger.instance.appLog(
                                "${LevelOneFragment::class.java.simpleName}:NextModuleID",
                                Gson().toJson(action.nextModuleID)
                            )
                            apiCall(action, formControl, modules)
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
        if (FieldValidationHelper.instance.validateFields(
                inputList = inputList,
                params = params!!,
                activity = requireActivity(),
                json = json,
                encrypted = encrypted
            )
        ) {
            when (BaseClass.nonCaps(formControl?.controlFormat)) {
                BaseClass.nonCaps(ControlFormatEnum.NEXT.type) -> {
                    setOnNextModule(
                        formControl = formControl,
                        next = null,
                        modules = modules,
                        formID = modules?.moduleID
                    )
                }
                else -> {
                    val destination = formControl?.formID
                    when (BaseClass.nonCaps(destination)) {
                        BaseClass.nonCaps(FormNavigation.PAYMENT.name) -> {
                            if (action.confirmationModuleID != null)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    ConfirmFragment.showDialog(
                                        manager = this.childFragmentManager,
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
                requireActivity()
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

                                if (resData?.formID != null) {
                                    if (BaseClass.nonCaps(resData.formID)
                                        == BaseClass.nonCaps("STATEMENT")
                                    ) {
                                        DisplayDialogFragment.setData(
                                            data = resData.accountStatement
                                        )
                                        navigate(
                                            baseViewModel
                                                .navigationData
                                                .navigateToDisplayDialog(
                                                    DisplayData(
                                                        display = null,
                                                        modules = modules,
                                                        form = formControl
                                                    )
                                                )
                                        )

                                    } else if (BaseClass.nonCaps(resData.formID)
                                        == BaseClass.nonCaps("PAYMENTCONFIRMATIONFORM")
                                    ) {
                                        AppLogger.instance.appLog("Pay", Gson().toJson(resData))
                                        ReceiptFragment.newInstance(
                                            this, ReceiptList(
                                                receipt = resData.receipt!!
                                                    .toMutableList(),
                                                notification = resData.notifications
                                            )
                                        )

                                        navigate(
                                            widgetViewModel.navigation()
                                                .navigateReceipt(
                                                    ReceiptList(
                                                        receipt = resData.receipt!!
                                                            .toMutableList(),
                                                        notification = resData.notifications
                                                    )
                                                )
                                        )
                                    } else if (BaseClass.nonCaps(resData.formID) == BaseClass.nonCaps(
                                            "RELIGION"
                                        )
                                    ) {
                                        val mData = GlobalResponseTypeConverter().to(
                                            BaseClass.decryptLatest(
                                                it.response,
                                                baseViewModel.dataSource.deviceData.value!!.device,
                                                true,
                                                baseViewModel.dataSource.deviceData.value!!.run
                                            )
                                        )

                                        DisplayDialogFragment.setData(
                                            data = mData?.data
                                        )
                                        navigate(
                                            baseViewModel
                                                .navigationData
                                                .navigateToDisplayDialog(
                                                    DisplayData(
                                                        display = null,
                                                        modules = modules,
                                                        form = formControl
                                                    )
                                                )
                                        )

                                    }
                                } else SuccessDialogFragment.showDialog(
                                    DialogData(
                                        title = R.string.success,
                                        subTitle = resData?.message!!,
                                        R.drawable.success
                                    ),
                                    requireActivity().supportFragmentManager, this
                                )


                            } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                InfoFragment.showDialog(this.childFragmentManager)
                            } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.OTP.type) {
                                (requireActivity() as MainActivity).initSMSBroadCast()
                                GlobalOTPFragment.setData(
                                    json = json,
                                    encrypted = encrypted,
                                    inputList = inputList,
                                    module = module,
                                    action = action,
                                    formControl,
                                    this,
                                )
                                Handler(Looper.getMainLooper()).postDelayed({
                                    navigate(widgetViewModel.navigation().navigateToGlobalOtp())
                                }, 200)

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

    private fun showError(data: MainDialogData) {
        navigate(baseViewModel.navigationData.navigateToAlertDialog(data))
    }

    override fun onModule(modules: Modules?) {
        inputData?.clear()
        if (modules?.available!!)
            if (modules.moduleURLTwo != null) {
                if (!TextUtils.isEmpty(modules.moduleURLTwo)) {
                    openUrl(modules.moduleURLTwo)
                } else navigateTo(modules)
            } else navigateTo(modules)
        else ShowToast(requireContext(), modules.message)
    }

    private fun navigateTo(modules: Modules?) {
        AppLogger.instance.appLog("Module", Gson().toJson(modules))
        if (modules!!.ModuleCategory == ModuleCategory.BLOCK.type) {
            subscribe.add(widgetViewModel.getModules(modules.moduleID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ f: List<Modules> ->
                    filterModules(f, modules)
                }) { obj: Throwable -> obj.printStackTrace() })
        } else getFormControl(modules)
    }

    private fun getFormControl(modules: Modules) {
        subscribe.add(widgetViewModel.getFormControl(modules.moduleID, "1")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f.toMutableList(), modules)
            }) { obj: Throwable -> obj.printStackTrace() })
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
                modules, requireActivity()
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
                                                mandatory = formControl.isMandatory
                                            )
                                        )

                                    } else if (BaseClass.nonCaps(moduleData?.status)
                                        == StatusEnum.TOKEN.type
                                    ) {
                                        InfoFragment.showDialog(this.childFragmentManager)
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


    private fun scanSuccess(result: QRResult?) {
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
                    mandatory = scanControl.isMandatory
                )
            )
            Handler(Looper.getMainLooper()).postDelayed({
                requireActivity().runOnUiThread {
                    onForm(scanControl, modules)
                }
            }, 200)
        } else {
            AppLogger.instance.appLog("SCAN:QR", text)
        }
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
        RecentFragment.showDialog(childFragmentManager, moduleDataRes.value!!)
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
        layout.setData(LoadingState())
        val json = JSONObject()
        val encrypted = JSONObject()
        subscribe.add(
            baseViewModel.dynamicCall(
                action,
                json,
                encrypted,
                modules, requireActivity()
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
                                        InfoFragment.showDialog(this.childFragmentManager)

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
                }, { it.printStackTrace() })
        )
    }


    override fun bioPayment(view: TextInputEditText?) {
        if ((requireActivity() as MainActivity).isBiometric()) {
            (requireActivity() as MainActivity).authenticateTo(object : BioInterface {
                override fun onPin(pin: String) {
                    view?.setText(pin)
                }
            })
        }
    }


}