package com.craft.silicon.centemobile.view.activity.transaction

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
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
import com.craft.silicon.centemobile.databinding.ActivityPendingTransactionBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.callbacks.Confirm
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.activity.level.FalconHeavyActivity
import com.craft.silicon.centemobile.view.binding.FieldValidationHelper
import com.craft.silicon.centemobile.view.binding.isOnline
import com.craft.silicon.centemobile.view.dialog.*
import com.craft.silicon.centemobile.view.ep.controller.MainDisplayController
import com.craft.silicon.centemobile.view.ep.data.BusData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.ep.data.Nothing
import com.craft.silicon.centemobile.view.fragment.go.steps.OTP
import com.craft.silicon.centemobile.view.fragment.go.steps.OTPCountDownTimer
import com.craft.silicon.centemobile.view.fragment.transaction.PendingTransactionFragment
import com.craft.silicon.centemobile.view.fragment.transaction.RejectPendingFragment
import com.craft.silicon.centemobile.view.fragment.transaction.RejectTransaction
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

@AndroidEntryPoint
class PendingTransactionActivity : AppCompatActivity(), AppCallbacks, RejectTransaction, OTP,
    Confirm {
    private lateinit var binding: ActivityPendingTransactionBinding
    private lateinit var data: Modules

    private val baseViewModel: BaseViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private val controller = MainDisplayController(this)

    private lateinit var pendingTransaction: PendingTransaction

    private var countDownTimer: CountDownTimer? = null

    private var startTime = (24 * 1000).toLong()
    private val interval = (1 * 1000).toLong()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setController()
        setToolbar()
        updateTimeout()
    }

    override fun setController() {
        data = EventBus.getDefault().getStickyEvent(Modules::class.java)
        binding.lifecycleOwner = this
        binding.toolbar.title = data.moduleName
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
            "${PendingTransactionActivity::class.java.simpleName} :Data",
            Gson().toJson(it)
        )

        controller.setData(PendingTransactionList(it))
        binding.container.setController(controller)
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun setBinding() {
        EventBus.getDefault().register(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pending_transaction)
        binding.lifecycleOwner = this
        fetchTransaction()
    }

    override fun rejectTransaction(data: PendingTransaction?) {
        pendingTransaction = data!!
        RejectPendingFragment.show(this, supportFragmentManager)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEvent(busData: BusData?) {
        AppLogger.instance.appLog("BUS", Gson().toJson(busData))
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


        val module = this.data
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
                        if (isOnline()) {
                            action.actionID = "REJECT"
                            action.actionType = ActionTypeEnum.DB_CALL.type
                            lifecycleScope.launch {
                                apiCall(action, form.first(), module, inputList, data)
                            }
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
                activity = this,
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
                        this,
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
                                        InfoFragment.showDialog(supportFragmentManager, this)
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

    private fun showError(data: MainDialogData) {
        NewAlertDialogFragment.show(data, supportFragmentManager)
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
            supportFragmentManager, callback
        )
    }

    override fun timeOut() {
        onCancel()
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

        val module = this.data
        module.moduleID = data.form.first().moduleID!!
        module.merchantID = data.payload.list["MerchantID"]

        AppLogger.instance.appLog(
            PendingTransaction::class.java.simpleName,
            Gson().toJson(dynamicDataResponse.formField)
        )
        EventBus.getDefault().postSticky(
            BusData(
                data = GroupForm(
                    module = module,
                    form = form.toMutableList(),
                    allow = true
                ),
                inputs = inputList, res = dynamicDataResponse
            )
        )
        val i = Intent(this, FalconHeavyActivity::class.java)
        startActivity(i)
    }

    override fun navigateUp() {
        runOnUiThread {
            widgetViewModel.deletePendingTransactionsByID(pendingTransaction.id)
        }
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

    private fun setTimer() {
        baseViewModel.dataSource.setInactivity(false)
        countDownTimer = OTPCountDownTimer(startTime = startTime, interval = interval, this)
        timerControl(false)
        timerControl(true)
        done(false)
    }

    private fun timerControl(startTimer: Boolean) {
        if (startTimer) {
            countDownTimer!!.start()
        } else {
            countDownTimer!!.cancel()
        }
    }

    override fun timer(str: String) {

    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        timerControl(false)
        setTimer()
    }

    override fun done(boolean: Boolean) {
        if (boolean) {
            timerControl(false)
            runOnUiThread {
                baseViewModel.dataSource.setInactivity(true)
                onCancel()
            }
        }
    }

    override fun onCancel() {
        val openMainActivity = Intent(this, MainActivity::class.java)
        openMainActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivityIfNeeded(openMainActivity, 0)
        finish()
    }

}