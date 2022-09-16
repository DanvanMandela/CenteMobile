package com.craft.silicon.centemobile.view.activity.beneficiary

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
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse
import com.craft.silicon.centemobile.databinding.ActivityBeneficiaryManageBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.callbacks.Confirm
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.dialog.*
import com.craft.silicon.centemobile.view.ep.adapter.TransactionAdapterItem
import com.craft.silicon.centemobile.view.ep.controller.BeneficiaryList
import com.craft.silicon.centemobile.view.ep.controller.MainDisplayController
import com.craft.silicon.centemobile.view.ep.data.BusData
import com.craft.silicon.centemobile.view.ep.data.Nothing
import com.craft.silicon.centemobile.view.fragment.go.steps.OTP
import com.craft.silicon.centemobile.view.fragment.go.steps.OTPCountDownTimer
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
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

@AndroidEntryPoint
class BeneficiaryManageActivity : AppCompatActivity(), AppCallbacks, OTP, Confirm {

    private var data: Modules? = null
    private val baseViewModel: BaseViewModel by viewModels()

    private val subscribe = CompositeDisposable()
    private lateinit var adapter: TransactionAdapterItem

    private lateinit var controller: MainDisplayController

    private val widgetViewModel: WidgetViewModel by viewModels()

    private lateinit var binding: ActivityBeneficiaryManageBinding

    private var countDownTimer: CountDownTimer? = null

    private var startTime = (24 * 1000).toLong()
    private val interval = (1 * 1000).toLong()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setController()
        setToolbar()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEvent(busData: BusData?) {
        AppLogger.instance.appLog("BUS", Gson().toJson(busData))
    }


    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun setBinding() {
        EventBus.getDefault().register(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_beneficiary_manage)
        binding.lifecycleOwner = this


    }

    override fun setController() {
        controller = MainDisplayController(this)
        data = EventBus.getDefault().getStickyEvent(Modules::class.java)
        binding.toolbar.title = data?.moduleName
        stopShimmer()
        setData()
        val staticData = baseViewModel.dataSource.beneficiary.asLiveData()
        staticData.observe(this) {
            setData(it?.filter { s -> s?.rowID != "0" })
        }
    }

    private fun setData(it: List<Beneficiary?>?) {
        if (it!!.isEmpty()) controller.setData(Nothing())
        else controller.setData(BeneficiaryList(list = it.toMutableList(), module = data))
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

    override fun navigateUp() {
        finish()
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun setData() {
        binding.container.setController(controller)
    }

    private fun onDeleteSuccess(it: DynamicResponse?, modules: Modules?) {
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
                        val moduleData = DynamicDataResponseTypeConverter().to(
                            BaseClass.decryptLatest(
                                it?.response,
                                widgetViewModel.storageDataSource.deviceData.value!!.device,
                                true,
                                widgetViewModel.storageDataSource.deviceData.value!!.run
                            )
                        )
                        AppLogger.instance.appLog("Beneficiary", Gson().toJson(moduleData))
                        if (BaseClass.nonCaps(moduleData?.status)
                            == StatusEnum.SUCCESS.type
                        ) {

                            if (moduleData!!.beneficiary.isNullOrEmpty()) {
                                controller.setData(Nothing())
                                setSuccess(moduleData.message)
                            } else {
                                setSuccess(moduleData.message, null)
                                controller.setData(
                                    BeneficiaryList(
                                        list = moduleData.beneficiary!!,
                                        module = modules
                                    )
                                )
                                val beneficiary = mutableListOf<Beneficiary>()
                                moduleData.beneficiary!!.forEach { b ->
                                    beneficiary.add(b!!)
                                }
                                baseViewModel.dataSource.setBeneficiary(beneficiary)
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

    override fun timeOut() {
        onCancel()
    }

    override fun deleteBeneficiary(modules: Modules?, beneficiary: Beneficiary?) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_beneficiary))
            .setMessage(getString(R.string.delete_b_message))
            .setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->
                subscribe.add(
                    widgetViewModel.getActionControlCID("DELETE").subscribe({
                        if (it.isNotEmpty()) {
                            val action = it.first { a -> a.moduleID == modules?.moduleID }
                            lifecycleScope.launch(Dispatchers.Main) {
                                onDeleteBeneficiaryOrder(
                                    action = action,
                                    modules = modules,
                                    beneficiary = beneficiary

                                )
                            }
                        }
                    }, { it.printStackTrace() })
                )
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ -> }
            .show()

    }

    override fun onStart() {
        super.onStart()
        updateTimeout()
    }

    override fun onPause() {
        super.onPause()
        timerControl(false)
    }


    private fun onDeleteBeneficiaryOrder(
        modules: Modules?,
        action: ActionControls?,
        beneficiary: Beneficiary?
    ) {
        setLoading(true)
        val json = JSONObject()
        json.put("INFOFIELD1", beneficiary?.accountAlias)
        json.put("INFOFIELD2", beneficiary?.merchantID)
        json.put("INFOFIELD4", beneficiary?.accountID)
        json.put("INFOFIELD3", beneficiary?.rowID)
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
                    onDeleteSuccess(it, modules)
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

    private fun setLoading(b: Boolean) {
        if (b) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }

    private fun showError(data: MainDialogData) {
        NewAlertDialogFragment.show(data, supportFragmentManager)
    }

    private fun updateTimeout() {
        val timeout = baseViewModel.dataSource.timeout.value
        val time = timeout ?: startTime
        countDownTimer = OTPCountDownTimer(startTime = time, interval = interval, this)
        setTimer()
    }

    private fun setTimer() {
        baseViewModel.dataSource.setInactivity(false)
        timerControl(true)
        done(false)
    }

    private fun timerControl(startTimer: Boolean) {
        if (startTimer) {
            countDownTimer!!.start()
        } else {
            countDownTimer!!.cancel()
            countDownTimer = null
        }
    }

    override fun timer(str: String) {

    }


    override fun onUserInteraction() {
        super.onUserInteraction()
        updateTimeout()
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