package com.craft.silicon.centemobile.view.fragment.transaction

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.action.ActionTypeEnum
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.converter.DynamicAPIResponseConverter
import com.craft.silicon.centemobile.data.model.dynamic.DynamicAPIResponse
import com.craft.silicon.centemobile.data.model.dynamic.FormField
import com.craft.silicon.centemobile.data.model.input.InputData
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.PendingTransaction
import com.craft.silicon.centemobile.data.model.user.PendingTransactionList
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.FragmentPendingTransactionBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.FieldValidationHelper
import com.craft.silicon.centemobile.view.binding.isOnline
import com.craft.silicon.centemobile.view.binding.navigate
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.InfoFragment
import com.craft.silicon.centemobile.view.dialog.MainDialogData
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment
import com.craft.silicon.centemobile.view.ep.controller.MainDisplayController
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.ep.data.Nothing
import com.craft.silicon.centemobile.view.fragment.levels.LevelOneFragment
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "module"

/**
 * A simple [Fragment] subclass.
 * Use the [PendingTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class PendingTransactionFragment : Fragment(), AppCallbacks, RejectTransaction {
    // TODO: Rename and change types of parameters
    private var data: Modules? = null

    private val baseViewModel: BaseViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private val controller = MainDisplayController(this)
    private lateinit var binding: FragmentPendingTransactionBinding
    private lateinit var pendingTransaction: PendingTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable(ARG_DATA)
            AppLogger.instance.appLog(
                this@PendingTransactionFragment::class.java.simpleName,
                Gson().toJson(data)
            )
        }
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.toolbar.title = data?.moduleName
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            fetchTransaction()
        }, animationDuration.toLong())
    }


    private fun fetchTransaction() {
        subscribe.add(
            widgetViewModel.pendingTransaction
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    stopShimmer()
                    setData(it)
                    if (it.isNullOrEmpty()) controller.setData(Nothing())
                }, { it.printStackTrace() })
        )
    }

    private fun setData(it: MutableList<PendingTransaction>?) {
        AppLogger.instance.appLog(
            "${PendingTransactionFragment::class.java.simpleName} :Data",
            Gson().toJson(it)
        )

        controller.setData(PendingTransactionList(it))
        binding.container.setController(controller)
    }


    private fun setNoData(b: Boolean) {
        if (b) binding.noData.root.visibility = View.VISIBLE
        else binding.noData.root.visibility = View.GONE

    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPendingTransactionBinding.inflate(inflater, container, false)
        setController()
        setBinding()
        setToolbar()
        return binding.root.rootView
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data: Modules
         * @return A new instance of fragment PendingTransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: Modules) =
            PendingTransactionFragment().apply {
                arguments = Bundle().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_DATA, data)
                    }
                }
            }
    }

    override fun rejectTransaction(data: PendingTransaction?) {
        pendingTransaction = data!!
        RejectPendingFragment.setData(this)
        navigate(widgetViewModel.navigation().navigateToRejectTransaction())
    }

    override fun onReject(message: String) {
        val data = pendingTransaction
        val inputList = mutableListOf<InputData>()

        inputList.add(
            InputData(
                name = "Message",
                key = "Message",
                value = message,
                encrypted = false,
                mandatory = true,
                validation = null
            )
        )

        val form = mutableListOf<FormControl>()
        form.add(
            FormControl(
                moduleID = data.form.last().moduleID,
                controlID = "PAY",
                controlText = getString(R.string.approve),
                controlType = ControlTypeEnum.BUTTON.type,
                controlFormat = data.form.last().controlFormat,
                displayOrder = data.form.last().displayOrder!!.toDouble().plus(1),
                actionType = ActionTypeEnum.DB_CALL.type,
                serviceParamID = ControlTypeEnum.BUTTON.type,
                displayControl = data.form.last().displayControl,
                isEncrypted = false,
                isMandatory = false,
                formID = ActionTypeEnum.PAY_BILL.type
            )
        )
        for (s in data.payload.list.entries) {
            inputList.add(
                InputData(
                    name = s.key,
                    key = s.key,
                    value = s.value,
                    encrypted = false,
                    mandatory = true,
                    validation = null
                )
            )
        }


        val module = this@PendingTransactionFragment.data!!
        module.moduleID = data.form.first().moduleID!!
        module.merchantID = data.payload.list["MerchantID"]

        try {
            subscribe.add(
                widgetViewModel.getActionControlCID(form.first().controlID).subscribe({
                    if (it.isNotEmpty()) {
                        val merchantID = it.map { a -> a.merchantID }
                        AppLogger.instance.appLog(
                            "${PendingTransactionFragment::class.java.simpleName}:MERCHANT:ID",
                            Gson().toJson(merchantID)
                        )
                        val action = it.first { a -> a.moduleID == form.first().moduleID }
                        if (requireActivity().isOnline()) {
                            action.actionID = "REJECT"
                            action.actionType = ActionTypeEnum.DB_CALL.type
                            lifecycleScope.launch {
                                apiCall(action, form.first(), module, inputList, data)
                            }
                        } else ShowToast(
                            requireContext(),
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


    private fun apiCall(
        action: ActionControls?,
        formControl: FormControl,
        module: Modules,
        inputList: MutableList<InputData>,
        data: PendingTransaction
    ) {

        val params = action?.serviceParamIDs?.split(",")
        val json = JSONObject()
        val encrypted = JSONObject()
        if (FieldValidationHelper.instance.validateFields(
                inputList = inputList,
                params = params!!,
                activity = requireActivity(),
                json = json,
                encrypted = encrypted,
                all = true
            )
        ) {

            try {
                AppLogger.instance.appLog(
                    "${PendingTransactionFragment::class.java.simpleName}:fields",
                    Gson().toJson(json)
                )
                AppLogger.instance.appLog(
                    "${PendingTransactionFragment::class.java.simpleName}:encrypted",
                    Gson().toJson(encrypted)
                )
                AppLogger.instance.appLog(
                    "${PendingTransactionFragment::class.java.simpleName}:${action.actionType}:action",
                    Gson().toJson(action)
                )
                AppLogger.instance.appLog(
                    "${PendingTransactionFragment::class.java.simpleName}:form",
                    Gson().toJson(formControl)
                )
                AppLogger.instance.appLog(
                    "${PendingTransactionFragment::class.java.simpleName}:action",
                    Gson().toJson(action)
                )
                action.merchantID?.let { AppLogger.instance.appLog("MERCHANT:ID:ACTION", it) }
                module.merchantID?.let { AppLogger.instance.appLog("MERCHANT:ID:MODULE", it) }

                subscribe.add(
                    baseViewModel.dynamicCall(
                        action,
                        json,
                        encrypted,
                        module,
                        requireActivity(),
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            try {
                                AppLogger.instance.appLog(
                                    "${PendingTransactionFragment::class.java.simpleName}:v:Response",
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
                                        "${PendingTransactionFragment::class.java.simpleName}:Data",
                                        Gson().toJson(resData)
                                    )
                                    if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                        pendingTransaction = data
                                        setSuccess(resData!!.message)
                                    } else if (BaseClass.nonCaps(resData?.status)
                                        == StatusEnum.TOKEN.type
                                    ) {
                                        InfoFragment.showDialog(this.childFragmentManager)
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
    }

    override fun approveTransaction(data: PendingTransaction?) {
        val inputList = mutableListOf<InputData>()
        val form = mutableListOf<FormControl>()
        form.add(
            FormControl(
                moduleID = data?.form!!.last().moduleID,
                controlID = "Display",
                controlText = getString(R.string.approve),
                controlType = ControlTypeEnum.TEXTVIEW.type,
                controlFormat = ControlFormatEnum.JSON.type,
                displayOrder = data.form.first().displayOrder!!.toDouble().minus(1),
                actionType = data.form.last().actionType,
                serviceParamID = data.form.last().serviceParamID,
                displayControl = data.form.last().displayControl,
                isEncrypted = data.form.last().isEncrypted,
                isMandatory = data.form.last().isMandatory
            )
        )
        for (s in data.form) {
            form.add(
                FormControl(
                    moduleID = s.moduleID,
                    controlID = s.controlID,
                    controlText = s.controlText,
                    controlType = s.controlType,
                    controlFormat = s.controlFormat,
                    displayOrder = s.displayOrder!!.toDouble(),
                    actionType = s.actionType,
                    serviceParamID = s.serviceParamID,
                    displayControl = s.displayControl,
                    isEncrypted = s.isEncrypted,
                    isMandatory = true
                )
            )
        }

        form.add(
            FormControl(
                moduleID = data.form.last().moduleID,
                controlID = "PAY",
                controlText = getString(R.string.approve),
                controlType = ControlTypeEnum.BUTTON.type,
                controlFormat = data.form.last().controlFormat,
                displayOrder = data.form.last().displayOrder!!.toDouble().plus(1),
                actionType = data.form.last().actionType,
                serviceParamID = ControlTypeEnum.BUTTON.type,
                displayControl = data.form.last().displayControl,
                isEncrypted = false,
                isMandatory = false,
                formID = "PAYMENT"
            )
        )
        for (s in data.payload.list.entries) {
            inputList.add(
                InputData(
                    name = s.key,
                    key = s.key,
                    value = s.value,
                    encrypted = false,
                    mandatory = true,
                    validation = null
                )
            )
        }
        val dynamicDataResponse = DynamicAPIResponse(
            formField = mutableListOf(
                FormField(
                    controlID = "JSON",
                    controlValue = Gson().toJson(mutableListOf(data.display.list))
                )
            )
        )

        val module = this@PendingTransactionFragment.data!!
        module.moduleID = data.form.first().moduleID!!
        module.merchantID = data.payload.list["MerchantID"]

        AppLogger.instance.appLog(
            PendingTransaction::class.java.simpleName,
            Gson().toJson(dynamicDataResponse.formField)
        )
        LevelOneFragment.setData(response = dynamicDataResponse, map = inputList)
        navigate(
            widgetViewModel.navigation().navigateToLevelOne(
                GroupForm(
                    module = module,
                    form = form.toMutableList(),
                    allow = true
                )
            )
        )
    }

    private fun showError(data: MainDialogData) {
        navigate(baseViewModel.navigationData.navigateToAlertDialog(data))
    }


    private fun setLoading(b: Boolean) {
        if (b) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }

    private fun setSuccess(message: String?, callback: AppCallbacks? = this) {
        SuccessDialogFragment.showDialog(
            DialogData(
                title = R.string.success,
                subTitle = message,
                R.drawable.success
            ),
            childFragmentManager, callback
        )
    }

    override fun navigateUp() {
        requireActivity().runOnUiThread {
            widgetViewModel.deletePendingTransactionsByID(pendingTransaction.id)
        }
    }

}