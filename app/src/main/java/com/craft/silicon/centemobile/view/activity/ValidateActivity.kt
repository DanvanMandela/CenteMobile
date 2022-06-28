package com.craft.silicon.centemobile.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.ControlFormatEnum
import com.craft.silicon.centemobile.data.model.control.ControlTypeEnum
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.control.FormNavigation
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.databinding.ActivityValidateBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.model.PaymentViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.HashMap

@AndroidEntryPoint
class ValidateActivity : AppCompatActivity(), AppCallbacks {

    private lateinit var binding: ActivityValidateBinding
    private val subscribe = CompositeDisposable()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val paymentViewModel: PaymentViewModel by viewModels()
    private var hashMap = HashMap<String, String>()
    private val staticDataLive = MutableLiveData<MutableList<StaticDataDetails>>()
    private var dynamicData: DynamicData? = null
    private lateinit var actionType: ActionControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        onModuleData()
    }


    private fun onActionControl(modules: Modules, formControl: FormControl) {
        subscribe.add(
            widgetViewModel.getActionControlByFM(modules.moduleID, formControl.formID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNotEmpty()) {
                        actionType = it.single()
                        fetchRecent(actionType)
                    }
                }, { it.printStackTrace() })
        )
    }

    private fun fetchRecent(actionType: ActionControls?) {
        Log.e("TAg", Gson().toJson(actionType?.merchantID))
    }

    override fun onModuleData() {
        dynamicData = intent.getParcelableExtra("data")
        binding.callback = this
        binding.data = dynamicData
    }

    override fun onForm(formControl: FormControl?, modules: Modules?) {
        onActionControl(modules!!, formControl!!)
        subscribe.add(widgetViewModel.getFormControl(
            modules.moduleID, formControl.formSequence!!.toInt().plus(1).toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f, modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }

    private fun setFormNavigation(f: List<FormControl>, modules: Modules?) {
        val destination = f.map { it.formID }.first()
        when (BaseClass.nonCaps(destination)) {
            BaseClass.nonCaps(FormNavigation.VALIDATE.name) -> throw Exception("Already on validation")
            BaseClass.nonCaps(FormNavigation.PAYMENT.name) -> onPayment(f, modules)
            else -> {
                throw Exception("No page implemented")
            }

        }
    }

    private fun onPayment(f: List<FormControl>, modules: Modules?) {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra(
            "data",
            GroupForm(
                modules!!,
                f.toMutableList(),
                widgetViewModel.storageStaticData().value!!
            )
        )
        startActivity(intent)
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_validate)
        binding.lifecycleOwner = this
    }

    override fun onRadioCheck(formControl: FormControl?) {
        dynamicData as GroupForm
        formControl?.isChecked = true
        val newData = mutableListOf<FormControl>()
        val linked =
            (dynamicData as GroupForm).form.filter { a -> a.linkedToControl == formControl?.controlID }
                .sortedBy { it.displayControl }

        val container =
            (dynamicData as GroupForm).form.single { a ->
                BaseClass.nonCaps(a.controlType) == BaseClass.nonCaps(
                    ControlTypeEnum.CONTAINER.type
                ) && BaseClass.nonCaps(a.controlFormat) == BaseClass.nonCaps(ControlFormatEnum.RADIO_GROUPS.type)
            }

        val radio = (dynamicData as GroupForm).form.single { a ->
            BaseClass.nonCaps(a.controlType) == BaseClass.nonCaps(
                ControlTypeEnum.R_BUTTON.type
            ) && BaseClass.nonCaps(a.controlID) != BaseClass.nonCaps(formControl?.controlID)
        }

        val button = (dynamicData as GroupForm).form.single { a ->
            BaseClass.nonCaps(a.controlType) == BaseClass.nonCaps(
                ControlTypeEnum.BUTTON.type
            )
        }
        radio.isChecked = false
        newData.add(container)
        newData.add(formControl!!)
        newData.add(radio)
        newData.addAll(linked)
        newData.add(button)
        newData.sortBy { it.displayControl }
        binding.data =
            GroupForm(
                module = (dynamicData as GroupForm).module,
                newData,
                widgetViewModel.storageStaticData().value!!
            )

    }

    override fun navigateUp() {
        onBackPressed()
    }

}