package com.craft.silicon.centemobile.view.fragment.global

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.control.FormNavigation
import com.craft.silicon.centemobile.data.model.converter.*
import com.craft.silicon.centemobile.data.model.dynamic.DynamicAPIResponse
import com.craft.silicon.centemobile.data.model.dynamic.DynamicDataResponse
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.FragmentGlobalBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.BaseClass.nonCaps
import com.craft.silicon.centemobile.util.JSONUtil.cleanData
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.callbacks.Confirm
import com.craft.silicon.centemobile.util.image.convert
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.binding.FieldValidationHelper
import com.craft.silicon.centemobile.view.binding.calendarToDate
import com.craft.silicon.centemobile.view.binding.hideSoftKeyboard
import com.craft.silicon.centemobile.view.binding.navigate
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.InfoFragment
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment
import com.craft.silicon.centemobile.view.dialog.display.DisplayDialogFragment
import com.craft.silicon.centemobile.view.dialog.receipt.ReceiptFragment
import com.craft.silicon.centemobile.view.ep.adapter.InsuranceAdapterItem
import com.craft.silicon.centemobile.view.ep.adapter.LoanAdapterItem
import com.craft.silicon.centemobile.view.ep.controller.DisplayData
import com.craft.silicon.centemobile.view.ep.controller.DisplayVault
import com.craft.silicon.centemobile.view.ep.controller.MainDisplayController
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.ep.data.ReceiptList
import com.craft.silicon.centemobile.view.fragment.auth.bio.BiometricFragment
import com.craft.silicon.centemobile.view.fragment.payment.PurchaseFragment
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.*

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GlobalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class GlobalFragment : Fragment(), AppCallbacks, Confirm {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var binding: FragmentGlobalBinding

    private val inputList = mutableListOf<InputData>()

    private val widgetViewModel: WidgetViewModel by viewModels()

    private val subscribe = CompositeDisposable()
    private var dynamicResponse = MutableLiveData<DynamicAPIResponse?>()
    private var actionControls = MutableLiveData<MutableList<ActionControls>>()

    private var moduleDataRes = MutableLiveData<DynamicDataResponse?>()
    private val liveFormData = MutableLiveData<DynamicData?>()

    private val baseViewModel: BaseViewModel by viewModels()

    private val contactLiveData = MutableLiveData<String?>()

    private val displayData = mutableListOf<DisplayVault>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGlobalBinding.inflate(inflater, container, false)
        setBinding()
        setController()
        return binding.root.rootView
    }

    override fun setBinding() {
        startShimmer()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.storage = widgetViewModel.storageDataSource
        liveFormData.value = navigationData
        binding.callback = this
        getActionControl(navigationData as GroupForm)
        AppLogger.instance.appLog("OnGlobal", Gson().toJson(navigationData))
    }

    override fun setController() {
        if (validationData != null) {
            if (validationData != null)
                for (filter in validationData!!) {
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


        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            liveFormData.observe(viewLifecycleOwner) {
                if (it != null)
                    binding.data = it
            }

        }, animationDuration.toLong())
    }

    override fun onModule(modules: Modules?) {
        AppLogger.instance.appLog("TAG", Gson().toJson(modules))
    }


    private fun getActionControl(dynamicData: GroupForm) {
        subscribe.add(
            widgetViewModel.getActionControl(dynamicData.module.moduleID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    actionControls.value = it
                }, { it.printStackTrace() })
        )
    }


    override fun navigateUp() {
        requireActivity().onBackPressed()
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun startShimmer() {
        binding.shimmerContainer.startShimmer()
    }


    companion object {
        private var navigationData: DynamicData? = null
        private var response: DynamicAPIResponse? = null
        private var validationData: MutableList<InputData>? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GlobalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GlobalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun setData(
            data: DynamicData,
            response: DynamicAPIResponse?,
            map: MutableList<InputData>?
        ) =
            GlobalFragment().apply {
                this@Companion.navigationData = data
                this@Companion.response = response
                this@Companion.validationData = map
            }
    }

    override fun userInput(inputData: InputData?) {
        AppLogger.instance.appLog("GLOBAL:UserInput", Gson().toJson(inputData))
        inputList.removeIf { a -> a.key == inputData?.key }
        inputList.add(inputData!!)
    }

    override fun clearInputData() {
        if (inputList.isNotEmpty()) inputList.clear()
    }

    override fun onContactSelect(view: AutoCompleteTextView?) {
        (requireActivity() as MainActivity).requestContactsPermission(this)
        contactLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                view?.setText(it)
            }
        }

    }


    override fun setContact(contact: String?) {
        contactLiveData.value = contact
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

    override fun onForm(formControl: FormControl?, modules: Modules?) {
        requireActivity().hideSoftKeyboard(binding.root)
        if (nonCaps(formControl?.controlFormat)
            == nonCaps(ControlFormatEnum.END.type)
        ) (requireActivity().onBackPressed())
        else {
            try {
                subscribe.add(
                    widgetViewModel.getActionControlCID(formControl?.controlID).subscribe({
                        if (it.isNotEmpty()) {
                            val merchantID = it.map { a -> a.merchantID }
                            AppLogger.instance.appLog(
                                "GLOBAL:MERCHANT:ID",
                                Gson().toJson(merchantID)
                            )
                            val action = it.first { a -> a.moduleID == formControl?.moduleID }

                            dbCall(action, modules, formControl)
                        }
                    }, { it.printStackTrace() })
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun dbCall(
        action: ActionControls,
        modules: Modules?,
        formControl: FormControl?
    ) {
        val params = action.serviceParamIDs?.split(",")
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
            Handler(Looper.getMainLooper()).postDelayed({
                when (nonCaps(formControl?.controlFormat)) {
                    nonCaps(ControlFormatEnum.NEXT.type) -> {
                        setOnNextModule(
                            formControl = formControl,
                            next = null,
                            modules = modules,
                            formID = modules?.moduleID
                        )
                    }
                    else -> validateModule(
                        jsonObject = json,
                        encrypted = encrypted,
                        modules = modules,
                        formControl = formControl,
                        action = action
                    )
                }
            }, 200)
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
                        "RECENT", BaseClass.decryptLatest(
                            it.response,
                            widgetViewModel.storageDataSource.deviceData.value!!.device,
                            true,
                            widgetViewModel.storageDataSource.deviceData.value!!.run
                        )
                    )
                    if (nonCaps(it.response) != StatusEnum.ERROR.type) {
                        try {
                            val moduleData = DynamicDataResponseTypeConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                                    true,
                                    widgetViewModel.storageDataSource.deviceData.value!!.run
                                )
                            )
                            AppLogger.instance.appLog("RECENT", Gson().toJson(moduleData))
                            if (nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
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

            AppLogger.instance.appLog("GLOBAL:fields", Gson().toJson(jsonObject))
            AppLogger.instance.appLog("GLOBAL:encrypted", Gson().toJson(encrypted))
            AppLogger.instance.appLog(
                "GLOBAL:${action?.actionType}:action", Gson()
                    .toJson(action)
            )
            AppLogger.instance.appLog("GLOBAL:form", Gson().toJson(formControl))
            AppLogger.instance.appLog("GLOBAL:action", Gson().toJson(action))
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
                                "VALIDATION:Response", BaseClass.decryptLatest(
                                    it.response,
                                    baseViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    baseViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            if (nonCaps(it.response) != StatusEnum.ERROR.type) {

                                val resData = DynamicAPIResponseConverter().to(
                                    BaseClass.decryptLatest(
                                        it.response,
                                        baseViewModel.dataSource.deviceData.value!!.device,
                                        true,
                                        baseViewModel.dataSource.deviceData.value!!.run
                                    )
                                )
                                AppLogger.instance.appLog("Validation", Gson().toJson(resData))
                                if (nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                    dynamicResponse.value = resData

                                    if (resData?.formID != null) {

                                        if (!TextUtils.isEmpty(resData.formID)
                                        ) {

                                            if (nonCaps(resData.formID)
                                                == nonCaps("PAYMENTCONFIRMATIONFORM")
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
                                            } else if (nonCaps(resData.formID)
                                                == nonCaps("STATEMENT")
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


                                            } else if (nonCaps(resData.formID)
                                                == nonCaps("RELIGION")
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
                                                    resData.next!!.toInt(),
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

                                } else if (nonCaps(resData?.status)
                                    == StatusEnum.TOKEN.type
                                ) {
                                    InfoFragment.showDialog(this.childFragmentManager)
                                } else if (nonCaps(resData?.status) == StatusEnum.OTP.type) {
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
                            AlertDialogFragment.newInstance(
                                DialogData(
                                    title = R.string.error,
                                    subTitle = getString(R.string.something_),
                                    R.drawable.warning_app
                                ),
                                requireActivity().supportFragmentManager
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

        AppLogger.instance.appLog("G:NEXT:FORM:ID", formControl?.formID!!)
        AppLogger.instance.appLog("G:NEXT:MODULE:ID", formID!!)
        AppLogger.instance.appLog("G:NEXT:SEQUENCE", "$sequence")

        subscribe.add(widgetViewModel.getFormControl(
            formID, sequence.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f.toMutableList(), modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }


    override fun onDisplay(formControl: FormControl?, modules: Modules?) {
        AppLogger.instance.appLog("DISPLAY:form", Gson().toJson(formControl))
        if (nonCaps(formControl?.controlID) == nonCaps("DISPLAY")) {
            if (!response?.display.isNullOrEmpty()) {
                binding.displayContainer.visibility = VISIBLE
                val controller = MainDisplayController(this)
                controller.setData(DisplayData(response?.display, formControl, modules))
                binding.displayContainer.setController(controller)
            }
        } else {
            if (nonCaps(formControl?.controlFormat)
                == nonCaps(ControlFormatEnum.JSON.type)
            ) {
                response?.let { setJsonFormatData(it, formControl, modules) }
            }
        }
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

    override fun setFormNavigation(forms: MutableList<FormControl>?, modules: Modules?) {
        try {
            val destination = forms?.map { it.formID }?.first()
            when (nonCaps(destination)) {
                nonCaps(FormNavigation.PAYMENT.name) -> onPayment(forms, modules)
                nonCaps(FormNavigation.VALIDATE.name) -> {
                    when (nonCaps(modules?.moduleID)) {
                        nonCaps(FormNavigation.FINGERPRINT.name) -> onBiometric(
                            forms,
                            modules
                        )
                        else -> onPayment(forms, modules)
                    }
                }
                else -> onPayment(forms, modules)
            }
        } catch (e: Exception) {
            onGlobal(forms, modules)
        }
    }

    private fun onGlobal(forms: MutableList<FormControl>?, modules: Modules?) {
        setData(
            data = GroupForm(
                module = modules!!,
                form = forms?.toMutableList()
            ),
            response = dynamicResponse.value,
            map = inputList
        )
        navigate(widgetViewModel.navigation().navigateGlobal())
    }

    private fun onBiometric(forms: MutableList<FormControl>?, modules: Modules?) {
        BiometricFragment.setData(form = forms, module = modules)
        navigate(widgetViewModel.navigation().navigationBio())
    }

    private fun onPayment(form: List<FormControl>?, modules: Modules?) {
        PurchaseFragment.setData(
            data = GroupForm(
                module = modules!!,
                form = form?.toMutableList()
            ),
            response = dynamicResponse.value,
            map = inputList
        )
        navigate(widgetViewModel.navigation().navigatePurchase())
    }

    override fun onCancel() {
        navigateUp()
    }


    private fun setJsonFormatData(
        data: DynamicAPIResponse?,
        form: FormControl?,
        modules: Modules?
    ) {
        displayData.clear()
        try {
            if (data != null) {
                AppLogger.instance.appLog("DYNAMIC:DATA", Gson().toJson(data.rData))
                AppLogger.instance.appLog("DYNAMIC:FORM", Gson().toJson(data.formField))
                startShimmer()
                val controller = MainDisplayController(this)
                if (!data.resultsData.isNullOrEmpty()) {
                    binding.detailsContainer.visibility = VISIBLE
                    stopShimmer()
                    val list = data.resultsData?.single { a -> a.controlID == form?.controlID }
                    val hashMaps: ArrayList<HashMap<String, String>> =
                        cleanData(list?.controlValue)

                    controller.setData(DisplayData(hashMaps, form, modules))
                    binding.detailsContainer.setController(controller)

                } else if (!data.formField.isNullOrEmpty()) {
                    binding.displayContainer.visibility = VISIBLE
                    stopShimmer()
                    val list = data.formField?.single { a -> a.controlID == form?.controlID }
                    val hashMaps: ArrayList<HashMap<String, String>> =
                        cleanData(list?.controlValue)

                    controller.setData(DisplayData(hashMaps, form, modules))
                    binding.detailsContainer.setController(controller)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onServerValue(formControl: FormControl?, view: TextInputEditText?) {
        try {
            response?.formField!!.forEach {
                if (nonCaps(it.controlID) == nonCaps(formControl?.controlID)) {
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

                if (nonCaps(data?.controlID) == nonCaps("Packages")) {
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
                                        nonCaps(a.tag.toString()) == nonCaps(s.key)
                                    }.map { it.setText(s.value.toString()) }
                                }
                        }
                } else if (nonCaps(data?.controlID) == nonCaps("LOANS")) {
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
                                        nonCaps(a.tag.toString()) == nonCaps(s.key)
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
                this@GlobalFragment.userInput(
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
        }, 1, 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        widgetViewModel.storageDataSource.deleteOtp()
    }


}