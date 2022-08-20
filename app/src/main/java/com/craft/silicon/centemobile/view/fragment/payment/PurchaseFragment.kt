package com.craft.silicon.centemobile.view.fragment.payment

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
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
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.model.converter.GlobalResponseTypeConverter
import com.craft.silicon.centemobile.data.model.converter.InsuranceTypeConverter
import com.craft.silicon.centemobile.data.model.converter.LoanTypeConverter
import com.craft.silicon.centemobile.data.model.dynamic.DynamicAPIResponse
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.FragmentPurchaseBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.BaseClass.nonCaps
import com.craft.silicon.centemobile.util.JSONUtil
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
import com.craft.silicon.centemobile.view.dialog.confirm.ConfirmFragment
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
import com.craft.silicon.centemobile.view.fragment.auth.bio.BioInterface
import com.craft.silicon.centemobile.view.fragment.auth.bio.BiometricFragment
import com.craft.silicon.centemobile.view.fragment.global.GlobalFragment
import com.craft.silicon.centemobile.view.fragment.global.GlobalOTPFragment
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.PaymentViewModel
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


/**
 * A simple [Fragment] subclass.
 * Use the [PurchaseFragment] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class PurchaseFragment : Fragment(), AppCallbacks, Confirm {

    private lateinit var binding: FragmentPurchaseBinding
    private val inputList = mutableListOf<InputData>()

    private val widgetViewModel: WidgetViewModel by viewModels()
    private val paymentViewModel: PaymentViewModel by viewModels()

    private val subscribe = CompositeDisposable()

    private var actionControls = MutableLiveData<MutableList<ActionControls>>()


    private val liveFormData = MutableLiveData<DynamicData?>()

    private val baseViewModel: BaseViewModel by viewModels()

    private val contactLiveData = MutableLiveData<String?>()
    private val displayData = mutableListOf<DisplayVault>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPurchaseBinding.inflate(inflater, container, false)
        setBinding()
        setController()
        return binding.root.rootView
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

    override fun setBinding() {
        startShimmer()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.storage = widgetViewModel.storageDataSource
        liveFormData.value = navigationData
        binding.callback = this
        getActionControl(navigationData as GroupForm)
        AppLogger.instance.appLog("OnPurchase", Gson().toJson(navigationData))
    }

    override fun onForm(formControl: FormControl?, modules: Modules?) {
        requireActivity().hideSoftKeyboard(binding.root)
        when (nonCaps(formControl?.controlFormat)) {
            nonCaps(ControlFormatEnum.END.type) -> (requireActivity()).onBackPressed()
            else -> {
                try {
                    subscribe.add(
                        widgetViewModel.getActionControlCID(formControl?.controlID).subscribe({
                            if (it.isNotEmpty()) {
                                val merchantID = it.map { a -> a.merchantID }
                                AppLogger.instance.appLog(
                                    "PURCHASE:MERCHANT:ID",
                                    Gson().toJson(merchantID)
                                )
                                AppLogger.instance.appLog(
                                    "PURCHASE:ACTIONS",
                                    Gson().toJson(it)
                                )

                                val action = it.first { a -> a.moduleID == formControl?.moduleID }
                                apiCall(action, formControl, modules)

                            }
                        }, { it.printStackTrace() })
                    )
                } catch (e: Exception) {
                    e.localizedMessage?.let { AppLogger.instance.appLog("PURCHASE:onForm", it) }
                }
            }
        }
    }

    private fun apiCall(
        action: ActionControls?, formControl: FormControl?,
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
            when (nonCaps(formControl?.controlFormat)) {
                nonCaps(ControlFormatEnum.NEXT.type) -> {
                    setOnNextModule(
                        formControl = formControl,
                        next = null,
                        modules = modules,
                        formID = modules?.moduleID
                    )
                }
                else -> {
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
                    else
                        onPay(
                            json = json, encrypted = encrypted,
                            inputList = inputList, module = modules,
                            action = action, formControl = formControl
                        )
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
                                paymentViewModel.dataSource.deviceData.value!!.device,
                                true,
                                paymentViewModel.dataSource.deviceData.value!!.run
                            )
                        )
                        if (nonCaps(it.response) != "ok") {
                            val resData = DynamicDataResponseTypeConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    paymentViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    paymentViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            if (nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {

                                if (resData?.formID != null) {
                                    if (nonCaps(resData.formID)
                                        == nonCaps("STATEMENT")
                                    ) {
                                        DisplayDialogFragment.showDialog(
                                            manager = this.parentFragmentManager,
                                            data = resData?.accountStatement,
                                            modules = module,
                                            controller = formControl
                                        )
                                    } else if (nonCaps(resData.formID)
                                        == nonCaps("PAYMENTCONFIRMATIONFORM")
                                    ) {
                                        AppLogger.instance.appLog("Pay", Gson().toJson(resData))
                                        ReceiptFragment.newInstance(
                                            this, ReceiptList(
                                                receipt = resData?.receipt!!
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
                                    } else if (nonCaps(resData.formID) == nonCaps("RELIGION")) {
                                        val mData = GlobalResponseTypeConverter().to(
                                            BaseClass.decryptLatest(
                                                it.response,
                                                baseViewModel.dataSource.deviceData.value!!.device,
                                                true,
                                                baseViewModel.dataSource.deviceData.value!!.run
                                            )
                                        )
                                        DisplayDialogFragment.showDialog(
                                            manager = this.parentFragmentManager,
                                            data = mData?.data,
                                            modules = module,
                                            controller = formControl
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


                            } else if (nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                InfoFragment.showDialog(this.childFragmentManager)
                            } else if (nonCaps(resData?.status) == StatusEnum.OTP.type) {
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
                                    AlertDialogFragment.newInstance(
                                        DialogData(
                                            title = R.string.error,
                                            subTitle = resData?.message,
                                            R.drawable.warning_app
                                        ),
                                        this.childFragmentManager
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
        modules: Modules?,
        formID: String?
    ) {
        val sequence = if (next != null) {
            if (next == 0) {
                formControl?.formSequence?.toInt()?.plus(1)
            } else next
        } else formControl?.formSequence?.toInt()?.plus(1)

        AppLogger.instance.appLog("P:NEXT:FORM:ID", formControl?.formID!!)
        AppLogger.instance.appLog("P:NEXT:MODULE:ID", formID!!)
        AppLogger.instance.appLog("P:NEXT:SEQUENCE", "$sequence")
        subscribe.add(widgetViewModel.getFormControl(
            formID, sequence.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f.toMutableList(), modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }

    override fun setFormNavigation(forms: MutableList<FormControl>?, modules: Modules?) {
        AppLogger.instance.appLog("NEXT:purchase", Gson().toJson(forms))
        try {
            when (forms?.map { it.formID }?.first()) {
                nonCaps(FormNavigation.VALIDATE.name) -> {
                    when (nonCaps(modules?.moduleID)) {
                        nonCaps(FormNavigation.FINGERPRINT.name) -> onBiometric(
                            forms,
                            modules
                        )
                        else -> onGlobal(forms, modules)
                    }
                }
                else -> onGlobal(forms, modules)
            }
        } catch (e: Exception) {
            onGlobal(forms, modules)
        }
    }

    private fun onGlobal(forms: MutableList<FormControl>?, modules: Modules?) {
        GlobalFragment.setData(
            data = GroupForm(
                module = modules!!,
                form = forms?.toMutableList()
            ),
            response = null,
            map = inputList
        )

        navigate(widgetViewModel.navigation().navigateGlobal())
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
        binding.shimmerContainer.visibility = VISIBLE
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
         * @param data NavigationData.
         * @return A new instance of fragment PurchaseFragment.
         */
        @JvmStatic
        fun setData(
            data: DynamicData,
            response: DynamicAPIResponse?,
            map: MutableList<InputData>?
        ) =
            PurchaseFragment().apply {
                this@Companion.navigationData = data
                this@Companion.response = response
                this@Companion.validationData = map
            }
    }


    override fun onCancel() {
        Handler(Looper.getMainLooper()).postDelayed({
            (requireActivity() as MainActivity)
                .provideNavigationGraph()
                .navigateUp()
        }, 200)
    }


    override fun userInput(inputData: InputData?) {
        AppLogger.instance.appLog("Input:fields", Gson().toJson(inputData))
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


    override fun onDisplay(formControl: FormControl?, modules: Modules?) {
        AppLogger.instance.appLog("DISPLAY:form", Gson().toJson(formControl))
        if (nonCaps(formControl?.controlID) == nonCaps("DISPLAY")) {
            if (!response?.display.isNullOrEmpty()) {
                val controller = MainDisplayController(this)
                controller.setData(DisplayData(response?.display, formControl, modules))
                binding.displayContainer.setController(controller)
                binding.displayContainer.visibility = VISIBLE
            }
        } else {
            if (nonCaps(formControl?.controlFormat)
                == nonCaps(ControlFormatEnum.JSON.type)
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
        displayData.clear()
        try {
            if (data != null) {
                AppLogger.instance.appLog("DYNAMIC:DATA", Gson().toJson(data.rData))
                AppLogger.instance.appLog("DYNAMIC:FORM", Gson().toJson(data.formField))
                startShimmer()
                binding.detailsContainer.visibility = VISIBLE
                val controller = MainDisplayController(this)
                if (!data.resultsData.isNullOrEmpty()) {
                    stopShimmer()
                    val list = data.resultsData?.single { a -> a.controlID == form?.controlID }
                    val hashMaps: ArrayList<HashMap<String, String>> =
                        JSONUtil.cleanData(list?.controlValue)

                    controller.setData(DisplayData(hashMaps, form, modules))
                    binding.detailsContainer.setController(controller)

                } else if (!data.formField.isNullOrEmpty()) {
                    stopShimmer()
                    binding.detailsContainer.visibility = VISIBLE
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


    override fun onServerValue(formControl: FormControl?, view: TextInputEditText?) {
        try {
            (if (response?.formField.isNullOrEmpty())
                response?.resultsData
            else if (response?.resultsData.isNullOrEmpty())
                response?.formField
            else null)?.forEach {
                if (it.controlID == formControl?.controlID) {
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
            AppLogger.instance.appLog("DYNAMIC:AUTO", Gson().toJson(formControl))
            editTextList?.forEach {
                AppLogger.instance.appLog("DYNAMIC:EDIT:TAGS", it.tag.toString())
            }

            if (response?.formField != null) {
                val data =
                    response?.formField?.single { a -> a.controlID == formControl?.controlID }

                if (nonCaps(data?.controlID) == nonCaps("Packages")) {
                    AppLogger.instance.appLog("DYNAMIC:DROP:PACKAGES", Gson().toJson(data))
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
                    AppLogger.instance.appLog("DYNAMIC:DROP:LOAN", Gson().toJson(data))

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
                this@PurchaseFragment.userInput(
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

    private fun onBiometric(forms: MutableList<FormControl>?, modules: Modules?) {
        BiometricFragment.setData(
            form = forms,
            module = modules
        )
        navigate(widgetViewModel.navigation().navigationBio())
    }

    override fun onDestroy() {
        super.onDestroy()
        widgetViewModel.storageDataSource.deleteOtp()
    }





}

