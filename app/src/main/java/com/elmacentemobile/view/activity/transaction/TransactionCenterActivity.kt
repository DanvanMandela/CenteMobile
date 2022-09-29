package com.elmacentemobile.view.activity.transaction

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.asLiveData
import com.elmacentemobile.R
import com.elmacentemobile.data.model.dynamic.TransactionData
import com.elmacentemobile.data.model.dynamic.TransactionResponseResponseConverter
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.data.source.remote.callback.DynamicResponse
import com.elmacentemobile.databinding.ActivityTransactionCenterBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.callbacks.Confirm
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.dialog.InfoFragment
import com.elmacentemobile.view.ep.adapter.TransactionAdapterItem
import com.elmacentemobile.view.ep.data.BusData
import com.elmacentemobile.view.fragment.transaction.TransactionCenterFragment
import com.elmacentemobile.view.fragment.transaction.TransactionDetailsFragment
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class TransactionCenterActivity : AppCompatActivity(), AppCallbacks,
    Confirm {

    private lateinit var binding: ActivityTransactionCenterBinding

    private lateinit var data: Modules
    private val baseViewModel: BaseViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private lateinit var adapter: TransactionAdapterItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setToolbar()
        setController()
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


    override fun setBinding() {
        EventBus.getDefault().register(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction_center)
        binding.lifecycleOwner = this
    }

    override fun setController() {
        data = EventBus.getDefault().getStickyEvent(Modules::class.java)
        binding.lifecycleOwner = this
        binding.toolbar.title = data.moduleName
        adapter = TransactionAdapterItem(mutableListOf(), this)
        binding.container.adapter = adapter

        fetchTransaction()

    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun fetchTransaction() {
        subscribe.add(
            baseViewModel.transactionCenter(data, this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    stopShimmer()
                    onData(it)
                }, { it.printStackTrace() })
        )
    }

    private fun onData(it: DynamicResponse?) {

        try {
            AppLogger.instance.appLog(
                "${TransactionCenterFragment::class.java.simpleName}:D:Transaction",
                BaseClass.decryptLatest(
                    it?.response,
                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                    true,
                    widgetViewModel.storageDataSource.deviceData.value!!.run
                )
            )
            AppLogger.instance.logTXT(
                BaseClass.decryptLatest(
                    it?.response,
                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                    true,
                    widgetViewModel.storageDataSource.deviceData.value!!.run
                ), this
            )
            val response = TransactionResponseResponseConverter().to(
                BaseClass.decryptLatest(
                    it?.response,
                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                    true,
                    widgetViewModel.storageDataSource.deviceData.value!!.run
                )
            )
            if (BaseClass.nonCaps(it?.response) != StatusEnum.ERROR.type) {
                if (BaseClass.nonCaps(response?.status)
                    == BaseClass.nonCaps(StatusEnum.SUCCESS.type)
                ) {
                    setNoData(response?.data.isNullOrEmpty())
                    adapter.replaceData(response?.data!!)
                } else if (BaseClass.nonCaps(response?.status)
                    == StatusEnum.TOKEN.type
                ) {
                    InfoFragment.showDialog(supportFragmentManager, this)
                }
            } else {
                ShowToast(this, getString(R.string.error_fetching_transactions))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun timeOut() {
        onCancel()
    }

    private fun setNoData(b: Boolean) {
        if (b) binding.noData.root.visibility = View.VISIBLE
        else binding.noData.root.visibility = View.GONE

    }


    override fun onTransactionDetails(data: TransactionData?) {
        TransactionDetailsFragment.show(data, supportFragmentManager)
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    override fun navigateUp() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEvent(busData: BusData?) {
        AppLogger.instance.appLog("BUS", Gson().toJson(busData))
    }


    override fun onCancel() {
        val openMainActivity = Intent(this, MainActivity::class.java)
        openMainActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivityIfNeeded(openMainActivity, 0)
        finish()
    }
}