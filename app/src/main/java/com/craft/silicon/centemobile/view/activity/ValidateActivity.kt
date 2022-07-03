package com.craft.silicon.centemobile.view.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.control.FormNavigation
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.model.dynamic.DynamicDataResponse
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.ActivityValidateBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.BaseClass.nonCaps
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.LoadingFragment
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.ep.data.LayoutData
import com.craft.silicon.centemobile.view.fragment.dynamic.RecentFragment
import com.craft.silicon.centemobile.view.model.ValidationViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

@AndroidEntryPoint
class ValidateActivity : AppCompatActivity(), AppCallbacks {

    private lateinit var binding: ActivityValidateBinding
    private val subscribe = CompositeDisposable()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private var hashMap = HashMap<String, String>()
    private var dynamicData: DynamicData? = null
    private var actionControls = MutableLiveData<MutableList<ActionControls>>()
    private var actionType = MutableLiveData<ActionControls>()
    private var dynamicResponse = MutableLiveData<DynamicDataResponse>()
    private var moduleDataRes = MutableLiveData<DynamicDataResponse>()
    private val validationViewModel: ValidationViewModel by viewModels()

    private val linkedMutable = MutableLiveData<FormControl>()
    private val staticData = MutableLiveData<StaticDataDetails>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        onModuleData()
        setViewModel()
    }

    private fun validateModule(
        jsonObject: JSONObject,
        modules: Modules?,
        formControl: FormControl?
    ) {
        val action = actionControls.value?.single { a -> a.controlID == formControl?.controlID }

        LoadingFragment.show(this.supportFragmentManager)
        subscribe.add(
            validationViewModel.validation(
                modules?.moduleID, action?.merchantID, jsonObject, this
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LoadingFragment.dismiss(this.supportFragmentManager)

                    try {
                        if (nonCaps(it.response) != "ok") {
                            AppLogger.instance.appLog("VALIDATION", it.response!!)

                            val resData = DynamicDataResponseTypeConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    validationViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    validationViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            AppLogger.instance.appLog("VALIDATION", Gson().toJson(resData))

                            if (nonCaps(resData?.status) == "000") {
                                dynamicResponse.value = resData
                                ShowToast(this, resData?.message)
                                setOnNextModule(formControl, modules)
                            } else {
                                AlertDialogFragment.newInstance(
                                    DialogData(
                                        title = R.string.error,
                                        subTitle = resData?.message!!,
                                        R.drawable.warning_app
                                    ),
                                    this.supportFragmentManager
                                )
                            }
                        }

                    } catch (e: Exception) {
                        AlertDialogFragment.newInstance(
                            DialogData(
                                title = R.string.error,
                                subTitle = getString(R.string.something_),
                                R.drawable.warning_app
                            ),
                            this.supportFragmentManager
                        )
                    }

                }, {
                    LoadingFragment.dismiss(this.supportFragmentManager)
                    it.printStackTrace()
                })
        )

    }

    private fun setOnNextModule(formControl: FormControl?, modules: Modules?) {
        subscribe.add(widgetViewModel.getFormControl(
            modules?.moduleID, formControl?.formSequence!!.toInt().plus(1).toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
               // setFormNavigation(f, modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }

    override fun onMenuItem() {
        RecentFragment.showDialog(this.supportFragmentManager, moduleDataRes.value!!)
    }

    override fun setViewModel() {

    }


    override fun onModuleData() {
        subscribe.add(
            widgetViewModel.layoutData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null) {
                        dynamicData = it.layout
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

    override fun onForm(formControl: FormControl?, modules: Modules?) {
        val json = JSONObject()
        if (hashMap.isNotEmpty()) {
            for (d in hashMap.entries) {
                json.put(d.key, d.value)
            }
        }
        if (hashMap.isNotEmpty()) {
            validateModule(json, modules, formControl)
        }
    }



    private fun onPayment(f: List<FormControl>, modules: Modules?) {
        widgetViewModel.saveLayoutData(
            LayoutData(
                layout = GroupForm(
                    modules!!,
                    f.toMutableList()
                ),
                data = dynamicResponse.value
            )
        )
        val intent = Intent(this, PaymentActivity::class.java)
        startActivity(intent)
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_validate)
        binding.lifecycleOwner = this
        binding.callback = this
        binding.storage = widgetViewModel.storageDataSource
    }

    override fun onRadioCheck(formControl: FormControl?, view: RadioButton?) {
        if (hashMap.isNotEmpty())
            hashMap.clear()
        val finalList = mutableStateListOf<FormControl>()
        val mainList = (dynamicData as GroupForm).form

        val linked = mainList?.filter { a ->
            nonCaps(a.linkedToControl) == nonCaps(formControl?.controlID)
                    && nonCaps(a.controlID) != nonCaps(formControl?.controlID)
                    || TextUtils.isEmpty(a.linkedToControl)
        }
        linked?.let { finalList.addAll(it) }
        finalList.map { it.isChecked = false }
        finalList.find { it.controlID == formControl?.controlID }?.isChecked = true
        binding.data =
            GroupForm(
                module = (dynamicData as GroupForm).module,
                form = finalList,
            )
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

    override fun onRecent(formControl: FormControl?) {
//        actionControls.observe(this) {
//            if (it.isNotEmpty()) {
//                val action = it.first { a ->
//                    a.moduleID == modules?.moduleID && a.merchantID != null
//                }
//                fetchRecent(action)
//            }
//        }

    }

    private fun fetchRecent(action: ActionControls?) {
        subscribe.add(
            widgetViewModel.recentList(
                action?.moduleID, action?.merchantID, this
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (nonCaps(it.response) != "ok") {
                        val moduleData = DynamicDataResponseTypeConverter().to(
                            BaseClass.decryptLatest(
                                it.response,
                                widgetViewModel.storageDataSource.deviceData.value!!.device,
                                true,
                                widgetViewModel.storageDataSource.deviceData.value!!.run
                            )
                        )
                        if (nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                            moduleDataRes.value = moduleData
                        }
                        val recent: MenuItem = binding.toolbar.menu.findItem(R.id.actionRecent)
                        if (!recent.isVisible) {
                            recent.isVisible = true
                            actionControls.removeObservers(this)
                        }
                    }
                }, { it.printStackTrace() })
        )
    }

    override fun linkedDropDown(formControl: FormControl?, data: StaticDataDetails?) {
        linkedMutable.value = formControl
        staticData.value = data
    }

    override fun linkedInput(view: TextInputEditText?, formControl: FormControl?) {
        linkedMutable.observe(this) {
            when (it.controlID) {
                formControl?.linkedToControl -> {
                    view?.setText(staticData.value?.extraField)
                }
                else -> {}
            }
        }
    }

}


