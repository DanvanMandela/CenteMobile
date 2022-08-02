package com.craft.silicon.centemobile.view.fragment.validation

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.airbnb.epoxy.EpoxyRecyclerView
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.control.FormNavigation
import com.craft.silicon.centemobile.data.model.converter.DynamicAPIResponseConverter
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.model.converter.GlobalResponseTypeConverter
import com.craft.silicon.centemobile.data.model.dynamic.DynamicAPIResponse
import com.craft.silicon.centemobile.data.model.dynamic.DynamicDataResponse
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.FragmentValidationBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.BaseClass.nonCaps
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.image.convert
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.binding.FieldValidationHelper
import com.craft.silicon.centemobile.view.binding.setDynamicToolbar
import com.craft.silicon.centemobile.view.dialog.*
import com.craft.silicon.centemobile.view.dialog.display.DisplayDialogFragment
import com.craft.silicon.centemobile.view.dialog.receipt.ReceiptFragment
import com.craft.silicon.centemobile.view.ep.controller.DisplayData
import com.craft.silicon.centemobile.view.ep.controller.DisplayState
import com.craft.silicon.centemobile.view.ep.controller.LoadingState
import com.craft.silicon.centemobile.view.ep.controller.MainDisplayController
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.ep.data.ReceiptList
import com.craft.silicon.centemobile.view.fragment.auth.bio.BiometricFragment
import com.craft.silicon.centemobile.view.fragment.dynamic.RecentFragment
import com.craft.silicon.centemobile.view.fragment.global.GlobalFragment
import com.craft.silicon.centemobile.view.fragment.payment.Confirm
import com.craft.silicon.centemobile.view.fragment.payment.PurchaseFragment
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass.
 * Use the [ValidationFragment.setData] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ValidationFragment : Fragment(), AppCallbacks, Confirm {

    private lateinit var binding: FragmentValidationBinding
    private val widgetViewModel: WidgetViewModel by viewModels()

    private val subscribe = CompositeDisposable()

    private val inputList = mutableListOf<InputData>()
    private var actionControls = MutableLiveData<MutableList<ActionControls>>()

    private var dynamicResponse = MutableLiveData<DynamicAPIResponse>()
    private var moduleDataRes = MutableLiveData<DynamicDataResponse>()


    private val liveFormData = MutableLiveData<DynamicData>()
    private val fetchRecentLive = MutableLiveData<FormControl>()

    private val baseViewModel: BaseViewModel by viewModels()

    private val contactLiveData = MutableLiveData<String>()

    private val listState = mutableListOf<DisplayState>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentValidationBinding.inflate(inflater, container, false)
        setBinding()
        setController()
        setToolbar()
        return binding.root.rootView
    }


    private fun setToolbar() {
        binding.toolbar.setDynamicToolbar(navigationData, this)
    }


    override fun setBinding() {
        startShimmer()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.storage = widgetViewModel.storageDataSource
        liveFormData.value = navigationData
        binding.callback = this
        getActionControl(navigationData as GroupForm)
        AppLogger.instance.appLog("OnValidation", Gson().toJson(navigationData))
    }

    override fun setController() {
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

    companion object {
        private var navigationData: DynamicData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data NavigationData.
         * @return A new instance of fragment ValidationFragment.
         */
        @JvmStatic
        fun setData(data: DynamicData) =
            ValidationFragment().apply {
                navigationData = data
            }
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

    override fun onForm(formControl: FormControl?, modules: Modules?) {
        when (nonCaps(formControl?.controlFormat)) {
            nonCaps(ControlFormatEnum.END.type) -> (requireActivity()).onBackPressed()
            else -> {
                try {
                    subscribe.add(
                        widgetViewModel.getActionControlCID(formControl?.controlID).subscribe({
                            if (it.isNotEmpty()) {
                                val merchantID = it.map { a -> a.merchantID }
                                AppLogger.instance.appLog(
                                    "VALIDATION:MERCHANT:ID",
                                    Gson().toJson(merchantID)
                                )
                                val action = it.first { a -> a.moduleID == formControl?.moduleID }
                                apiCall(action, formControl, modules)

                            }
                        }, { it.printStackTrace() })
                    )

                } catch (e: Exception) {
                    e.localizedMessage?.let { AppLogger.instance.appLog("VALIDATION:FORM", it) }
                }
            }
        }


    }

    private fun apiCall(
        action: ActionControls,
        form: FormControl?,
        modules: Modules?
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
                when (nonCaps(form?.controlFormat)) {
                    nonCaps(ControlFormatEnum.NEXT.type) -> {
                        setOnNextModule(
                            formControl = form,
                            next = null,
                            modules = modules,
                            formID = modules?.moduleID
                        )
                    }
                    else -> validateModule(
                        jsonObject = json,
                        encrypted = encrypted,
                        modules = modules,
                        formControl = form,
                        action = action
                    )
                }
            }, 200)
        }

    }

    private fun validateModule(
        jsonObject: JSONObject,
        encrypted: JSONObject,
        modules: Modules?,
        formControl: FormControl?,
        action: ActionControls?
    ) {

        AppLogger.instance.appLog("VALIDATION:fields", Gson().toJson(jsonObject))
        AppLogger.instance.appLog("VALIDATION:encrypted", Gson().toJson(encrypted))
        AppLogger.instance.appLog("VALIDATION:action", Gson().toJson(action))
        AppLogger.instance.appLog("VALIDATION:form", Gson().toJson(formControl))

        action?.merchantID?.let { AppLogger.instance.appLog("MERCHANT:ID:ACTION", it) }
        modules?.merchantID?.let { AppLogger.instance.appLog("MERCHANT:ID:MODULE", it) }
        setLoading(true)
        subscribe.add(
            baseViewModel.dynamicCall(
                action,
                jsonObject,
                encrypted,
                modules,
                requireContext()
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
                            setLoading(false)
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
                                setLoading(false)
                                dynamicResponse.value = resData


                                if (!resData?.formID.isNullOrEmpty()
                                    || !resData?.formID.isNullOrBlank()
                                ) {

                                    if (nonCaps(resData?.formID)
                                        == nonCaps("TRANSACTIONSCENTER")
                                    ) {
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
                                            modules = modules
                                        )

                                    } else if (nonCaps(resData?.formID)
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
                                        (requireActivity() as MainActivity)
                                            .provideNavigationGraph()
                                            .navigate(
                                                widgetViewModel.navigation()
                                                    .navigateReceipt(
                                                        ReceiptList(
                                                            receipt = resData.receipt!!
                                                                .toMutableList(),
                                                            notification = resData.notifications
                                                        )
                                                    )
                                            )
                                    } else if (nonCaps(resData?.formID)
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
                                        DisplayDialogFragment.showDialog(
                                            manager = this.parentFragmentManager,
                                            data = mData?.data,
                                            modules = modules
                                        )
                                    } else {
                                        ShowToast(requireContext(), resData?.message)
                                        setOnNextModule(
                                            formControl,
                                            resData?.next,
                                            modules,
                                            resData?.formID
                                        )
                                    }

                                } else {
                                    SuccessDialogFragment.showDialog(
                                        DialogData(
                                            title = R.string.success,
                                            subTitle = resData?.message!!,
                                            R.drawable.success
                                        ),
                                        requireActivity().supportFragmentManager, this
                                    )
                                }
                            } else if (nonCaps(resData?.status) == StatusEnum.OTP.type) {
                                setOnNextModule(
                                    formControl,
                                    resData?.next,
                                    modules,
                                    resData?.formID
                                )
                            } else if (nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                setLoading(false)
                                InfoFragment.showDialog(this.childFragmentManager)

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

                }, {
                    it.printStackTrace()
                })
        )
    }

    private fun setLoading(it: Boolean) {
        if (it) {
            LoadingFragment.show(requireActivity().supportFragmentManager)
        } else {
            LoadingFragment.dismiss(requireActivity().supportFragmentManager)
        }
    }

    private fun setError(message: String?) {
        AlertDialogFragment.newInstance(
            DialogData(
                title = R.string.error,
                subTitle = message,
                R.drawable.warning_app
            ),
            requireActivity().supportFragmentManager
        )
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

        fetchRecentLive.value = formControl
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


        subscribe.add(widgetViewModel.getFormControl(
            formID, sequence.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f.toMutableList(), modules)
            }) { obj: Throwable -> obj.printStackTrace() })
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
                        else -> onGlobal(forms, modules)
                    }
                }
                else -> onGlobal(forms, modules)
            }
        } catch (e: Exception) {
            onGlobal(forms, modules)
        }
    }

    private fun onBiometric(forms: MutableList<FormControl>?, modules: Modules?) {
        BiometricFragment.setData(
            form = forms,
            module = modules
        )
        ((requireActivity()) as MainActivity)
            .provideNavigationGraph()
            .navigate(
                widgetViewModel.navigation().navigationBio()
            )
    }

    private fun onGlobal(forms: MutableList<FormControl>?, modules: Modules?) {
        GlobalFragment.setData(
            data = GroupForm(
                module = modules!!,
                form = forms?.toMutableList()
            ),
            response = dynamicResponse.value,
            map = inputList
        )
        ((requireActivity()) as MainActivity)
            .provideNavigationGraph()
            .navigate(
                widgetViewModel.navigation().navigateGlobal()
            )
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
        ((requireActivity()) as MainActivity)
            .provideNavigationGraph()
            .navigate(
                widgetViewModel.navigation().navigatePurchase()
            )
    }

    override fun onMenuItem() {
        RecentFragment.showDialog(childFragmentManager, moduleDataRes.value!!)
    }

    override fun userInput(inputData: InputData?) {
        AppLogger.instance.appLog("Input:fields", Gson().toJson(inputData))
        inputList.removeIf { a -> a.key == inputData?.key }
        inputList.add(inputData!!)
    }

    override fun clearInputData() {
        if (inputList.isNotEmpty()) {
            AppLogger.instance.appLog("Input:fields", "User input cleared")
            inputList.clear()
        }
    }

    override fun onCancel() {
        requireActivity().onBackPressed()
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
        val datePattern = "dd MMM YYYY"
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(formControl?.controlText)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        datePicker.show(this.childFragmentManager, this::class.java.simpleName)

        datePicker.addOnPositiveButtonClickListener {
            val dateTime: LocalDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
            val dateFormatted: String =
                dateTime.format(DateTimeFormatter.ofPattern(datePattern))
            view?.setText(dateFormatted)
        }

    }

    override fun onImageSelect(imageView: ImageView?, data: FormControl?) {
        (requireActivity() as MainActivity).onImagePicker(object : AppCallbacks {
            override fun onImage(bitmap: Bitmap?) {
                this@ValidationFragment.userInput(
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
                        requireActivity().runOnUiThread {
                            fetchList(action, modules, controller)
                        }
                    else controller.setData(null)
                }
            }, { it.printStackTrace() })
        )
    }

    private fun fetchList(
        action: ActionControls?,
        modules: Modules?,
        layout: MainDisplayController
    ) {
        layout.setData(LoadingState())
        setLoading(true)
        val json = JSONObject()
        val encrypted = JSONObject()
        subscribe.add(
            baseViewModel.dynamicCall(
                action,
                json,
                encrypted,
                modules, requireContext()
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
                            setError(getString(R.string.something_))
                            setLoading(false)
                        } else
                            if (nonCaps(it.response) != StatusEnum.ERROR.type) {
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
                                    if (nonCaps(moduleData?.status)
                                        == StatusEnum.SUCCESS.type
                                    ) {

                                        val s = DisplayData(moduleData?.data!!.toMutableList())
                                        setLoading(false)
                                        layout.setData(s)
                                        listState.add(
                                            DisplayState(
                                                id = action?.actionID!!,
                                                data = s
                                            )
                                        )
                                    } else if (nonCaps(moduleData?.status)
                                        == StatusEnum.TOKEN.type
                                    ) {
                                        setLoading(false)
                                        InfoFragment.showDialog(this.childFragmentManager)

                                    } else {
                                        setLoading(false)
                                        setError(moduleData?.message)
                                    }
                                } catch (e: Exception) {
                                    setError(getString(R.string.something_))
                                    setLoading(false)
                                    e.printStackTrace()
                                }
                            }
                    } catch (e: Exception) {
                        setLoading(false)
                        setError(getString(R.string.something_))
                        e.printStackTrace()
                    }
                }, { it.printStackTrace() })
        )
    }

    override fun onListOption(control: FormControl?, modules: Modules?) {

    }
}