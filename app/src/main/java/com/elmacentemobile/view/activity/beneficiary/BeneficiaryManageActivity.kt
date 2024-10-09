package com.elmacentemobile.view.activity.beneficiary

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.elmacentemobile.R
import com.elmacentemobile.data.model.action.ActionControls
import com.elmacentemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.elmacentemobile.data.model.module.ModuleDataTypeConverter
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.model.user.Beneficiary
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.data.source.remote.callback.DynamicResponse
import com.elmacentemobile.databinding.ActivityBeneficiaryManageBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.callbacks.Confirm
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.dialog.DialogData
import com.elmacentemobile.view.dialog.InfoFragment
import com.elmacentemobile.view.dialog.MainDialogData
import com.elmacentemobile.view.dialog.NewAlertDialogFragment
import com.elmacentemobile.view.dialog.SuccessDialogFragment
import com.elmacentemobile.view.ep.controller.BeneficiaryList
import com.elmacentemobile.view.ep.controller.MainDisplayController
import com.elmacentemobile.view.ep.data.Nothing
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class BeneficiaryManageActivity : AppCompatActivity(), AppCallbacks, Confirm {

    private var data: Modules? = null
    private val baseViewModel: BaseViewModel by viewModels()

    private val subscribe = CompositeDisposable()


    private lateinit var controller: MainDisplayController

    private val widgetViewModel: WidgetViewModel by viewModels()

    private lateinit var binding: ActivityBeneficiaryManageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setController()
        setToolbar()
        onUserInteraction()
        listenToInActivity()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        baseViewModel.interactionDataSource.onUserInteracted()
    }

    private fun listenToInActivity() {
        val state = baseViewModel.dataSource.inActivity.asLiveData()
        state.observe(this) {
            if (it != null) {
                if (it) onCancel()
            }
        }
    }




    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_beneficiary_manage)
        binding.lifecycleOwner = this


    }

    override fun setController() {
        controller = MainDisplayController(this)
        data = ModuleDataTypeConverter().to(intent.getStringExtra("beneficiary"))
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


    override fun onCancel() {
        val openMainActivity = Intent(this, MainActivity::class.java)
        openMainActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivityIfNeeded(openMainActivity, 0)
        finish()
    }
}