package com.craft.silicon.centemobile.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.ModuleCategory
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.databinding.ActivityDynamicBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.*
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.HashMap

@AndroidEntryPoint
class DynamicActivity : AppCompatActivity(), AppCallbacks {
    private lateinit var binding: ActivityDynamicBinding
    private val subscribe = CompositeDisposable()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private var hashMap = HashMap<String, String>()
    private val staticDataLive = MutableLiveData<MutableList<StaticDataDetails>>()
    private var dynamicData: DynamicData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        setData()
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dynamic)
    }


    private fun setData() {
        dynamicData = intent.getParcelableExtra("data")
        binding.data = dynamicData
        binding.callback = this
        binding.storage = widgetViewModel.storageDataSource
    }


    override fun setViewModel() {

    }


    override fun onChildren(linearLayout: LinearLayout?) {

    }

    override fun onModule(modules: Modules?) {
        if (modules!!.ModuleCategory == ModuleCategory.BLOCK.type) {
            subscribe.add(widgetViewModel.getModules(modules.moduleID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ f: List<Modules> ->
                    val intent = Intent(this, DynamicActivity::class.java)
                    intent.putExtra("data", GroupModule(modules, f.toMutableList()))
                    startActivity(intent)
                }) { obj: Throwable -> obj.printStackTrace() })
        } else getFormControl(modules)
    }

    private fun getFormStaticData(modules: Modules) {
        subscribe.add(
            widgetViewModel.staticData
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    staticDataLive.value = it
                    getFormControl(modules)
                }, { it.printStackTrace() })
        )
    }

    private fun getFormControl(modules: Modules) {
        subscribe.add(widgetViewModel.getFormControl(modules.moduleID, "1")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f, modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }


    override fun onForm(formControl: FormControl?, modules: Modules) {
        subscribe.add(widgetViewModel.getFormControl(
            modules.moduleID, formControl?.formSequence.plus(1)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f, modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }

//    private fun setFormNavigation(f: List<FormControl>, modules: Modules) {
//        val destination = f.map { it.formID }.first()
//        when (BaseClass.nonCaps(destination)) {
//            BaseClass.nonCaps(FormNavigation.VALIDATE.name) -> onValidate(f, modules)
//            BaseClass.nonCaps(FormNavigation.PAYMENT.name) -> onPayment(f, modules)
//            else -> {
//                throw Exception("No page implemented")
//            }
//
//        }
//    }

    private fun onPayment(f: List<FormControl>, modules: Modules) {
        widgetViewModel.saveLayoutData(
            LayoutData(
                layout = GroupForm(
                    modules,
                    f.toMutableList(),
                ),
                data = null
            )
        )
        val intent = Intent(this, PaymentActivity::class.java)
        startActivity(intent)
    }

    private fun onValidate(f: List<FormControl>, modules: Modules) {


//        widgetViewModel.saveLayoutData(
//            LayoutData(
//                layout = GroupForm(
//                    modules,
//                    f.toMutableList(),
//                    widgetViewModel.storageStaticData().value!!,
//                    widgetViewModel.beneficiaryData().value!!
//                ),
//                data = null
//            )
//        )
//        val intent = Intent(this, ValidateActivity::class.java)
//        startActivity(intent)
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


    override fun navigateUp() {
        onBackPressed()
    }
}