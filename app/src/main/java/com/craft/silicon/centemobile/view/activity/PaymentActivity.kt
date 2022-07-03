package com.craft.silicon.centemobile.view.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.model.dynamic.DynamicDataResponse
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.databinding.ActivityPaymentBinding
import com.craft.silicon.centemobile.databinding.BlockDisplayItemLayoutBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.LoadingFragment
import com.craft.silicon.centemobile.view.dialog.confirm.ConfirmFragment
import com.craft.silicon.centemobile.view.ep.data.DisplayContent
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.model.AccountViewModel
import com.craft.silicon.centemobile.view.model.PaymentViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


@AndroidEntryPoint
class PaymentActivity : AppCompatActivity(), AppCallbacks {
    private lateinit var binding: ActivityPaymentBinding
    private var dynamicData: DynamicData? = null
    private var dynamicResponse: DynamicDataResponse? = null
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()
    private var actionControls = MutableLiveData<MutableList<ActionControls>>()
    private val paymentViewModel: PaymentViewModel by viewModels()

    private var hashMap = HashMap<String, String>()
    private val subscribe = CompositeDisposable()

    private val linkedMutable = MutableLiveData<FormControl>()
    private val staticData = MutableLiveData<StaticDataDetails>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        setBinding()
        onModuleData()
        buildViews()
    }

    private fun buildViews() {

    }


    override fun onModuleData() {
        subscribe.add(
            widgetViewModel.layoutData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null) {
                        it.data?.let { it1 -> setAccountDetails(it1) }
                        binding.data = it.layout
                        setFormData(it.layout)
                    }
                }, { it.printStackTrace() })
        )
    }

    private fun setFormData(dynamicData: GroupForm) {
        subscribe.add(
            widgetViewModel.getActionControl(dynamicData.module.moduleID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    actionControls.value = it

                }, { it.printStackTrace() })
        )
    }

    private fun setAccountDetails(data: DynamicDataResponse) {
        for (d in data.display!!) {
            for (e in d.entries) {
                val display = BlockDisplayItemLayoutBinding.inflate(layoutInflater)
                val m = DisplayContent(key = e.key, value = e.value)
                display.data = m
                binding.displayLay.addView(display.root)
            }
        }

    }

    override fun inputData(map: HashMap<String, String>?) {
        for ((key) in this.hashMap) {
            if (key.equals(map?.keys)) {
                this.hashMap.remove(key)
            }
        }
        map!!.forEach { (key, value) ->
            this.hashMap[key] = value
        }
    }


    override fun onForm(formControl: FormControl?, modules: Modules?) {
        hashMap["MerchantID"] = modules?.merchantID!!
        if (hashMap.isNotEmpty()) {
            ConfirmFragment.showDialog(this.supportFragmentManager, hashMap, this)
        }
    }

    private fun onPay(modules: Modules?) {
        LoadingFragment.show(this.supportFragmentManager)
        val json = JSONObject()
        val encrypted = JSONObject()
        if (hashMap.isNotEmpty()) {
            for (d in hashMap.entries) {
                if (BaseClass.nonCaps(d.key) == BaseClass.nonCaps(ControlFormatEnum.PIN.type)
                    || BaseClass.nonCaps(
                        d.key
                    ) == BaseClass.nonCaps(ControlFormatEnum.PIN_NUMBER.type)
                ) {
                    encrypted.put(d.key, BaseClass.newEncrypt(d.value))
                } else {
                    json.put(d.key, d.value)
                }
            }
        }

        json.put("ACCOUNTID", "11111")
        json.put("MerchantID", modules?.merchantID)
        if (hashMap.isNotEmpty())
            subscribe.add(
                paymentViewModel.pay(json, encrypted, this, modules?.moduleID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        LoadingFragment.dismiss(this.supportFragmentManager)
                        if (BaseClass.nonCaps(it.response) != "ok") {
                            val resData = DynamicDataResponseTypeConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    paymentViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    paymentViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            if (BaseClass.nonCaps(resData?.status) == "000") {
                                Log.e("TAg", Gson().toJson(resData))
                            } else {
                                Log.e("TAg", Gson().toJson(resData))
                                AlertDialogFragment.newInstance(
                                    DialogData(
                                        title = R.string.error,
                                        subTitle = resData?.message,
                                        R.drawable.warning_app
                                    ),
                                    this.supportFragmentManager
                                )
                            }
                        }
                    }, { it.printStackTrace() })
            )
    }


    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        binding.lifecycleOwner = this
        binding.callback = this
        binding.storage = widgetViewModel.storageDataSource

    }

    override fun navigateUp() {
        onBackPressed()
    }

    override fun linkedDropDown(formControl: FormControl?, data: StaticDataDetails?) {
        linkedMutable.value = formControl
        staticData.value = data
    }

    override fun linkedInput(view: TextInputEditText?, formControl: FormControl?) {
        linkedMutable.observe(this) {
            if (formControl?.linkedToControl == it.controlID) {
                staticData.observe(this) { s ->
                    if (s.extraField != null || !TextUtils.isEmpty(s.extraField))
                        view?.setText(s.extraField)
                }
            }
        }
    }
}

