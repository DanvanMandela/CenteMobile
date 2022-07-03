package com.craft.silicon.centemobile.view.fragment.validation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.compose.runtime.mutableStateListOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.craft.silicon.centemobile.databinding.FragmentValidationBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.binding.setDynamicToolbar
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.LoadingFragment
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.fragment.dynamic.RecentFragment
import com.craft.silicon.centemobile.view.fragment.payment.PurchaseFragment
import com.craft.silicon.centemobile.view.model.ValidationViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [ValidationFragment.setData] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ValidationFragment : Fragment(), AppCallbacks {

    private lateinit var binding: FragmentValidationBinding
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val accountNumber = MutableLiveData<String>()
    private val subscribe = CompositeDisposable()
    private var hashMap = HashMap<String, String>()
    private var dynamicData: DynamicData? = null
    private var actionControls = MutableLiveData<MutableList<ActionControls>>()
    private var actionType = MutableLiveData<ActionControls>()
    private var dynamicResponse = MutableLiveData<DynamicDataResponse>()
    private var moduleDataRes = MutableLiveData<DynamicDataResponse>()
    private val validationViewModel: ValidationViewModel by viewModels()

    private val linkedMutable = MutableLiveData<FormControl>()
    private val staticData = MutableLiveData<StaticDataDetails>()

    private val liveFormData = MutableLiveData<DynamicData>()
    private val fetchRecentLive = MutableLiveData<FormControl>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentValidationBinding.inflate(inflater, container, false)
        setBinding()
        setController()
        setToolbar()
        setFetchRecent()
        return binding.root.rootView
    }

    private fun setFetchRecent() {
        fetchRecentLive.observe(viewLifecycleOwner) {
            if (it != null) {
                actionControls.observe(viewLifecycleOwner) { b ->
                    if (b.isNotEmpty()) {
                        try {
                            val action = b.first { a ->
                                a.merchantID != null
                            }
                            fetchRecent(action)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ShowToast(requireContext(), getString(R.string.error_fetching_recent))
                        }
                        fetchRecentLive.removeObservers(viewLifecycleOwner)
                    }
                }
            }
        }
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

    override fun onRadioCheck(formControl: FormControl?, view: RadioButton?) {
        if (hashMap.isNotEmpty())
            hashMap.clear()
        val finalList = mutableStateListOf<FormControl>()
        val mainList = (navigationData as GroupForm).form

        val linked = mainList?.filter { a ->
            BaseClass.nonCaps(a.linkedToControl) == BaseClass.nonCaps(formControl?.controlID)
                    || TextUtils.isEmpty(a.linkedToControl) || a.linkedToControl == null
                    || a.controlID == formControl?.controlID
        }


        linked?.let { finalList.addAll(it) }
        finalList.map { it.isChecked = false }
        finalList.find { it.controlID == formControl?.controlID }?.isChecked = true
        Log.e("AM I", Gson().toJson(finalList))

        liveFormData.value = GroupForm(
            module = (navigationData as GroupForm).module,
            form = finalList,
        )

    }

    override fun onToggleButton(formControl: FormControl?) {
        if (hashMap.isNotEmpty())
            hashMap.clear()
        val finalList = mutableStateListOf<FormControl>()
        val mainList = (navigationData as GroupForm).form

        val linked = mainList?.filter { a ->
            BaseClass.nonCaps(a.linkedToControl) == BaseClass.nonCaps(formControl?.controlID)
                    || TextUtils.isEmpty(a.linkedToControl) || a.linkedToControl == null
                    || a.controlID == formControl?.controlID
        }
        linked?.let { finalList.addAll(it) }
        finalList.map { it.isChecked = false }
        finalList.find { it.controlID == formControl?.controlID }?.isChecked = true
        liveFormData.value = GroupForm(
            module = (navigationData as GroupForm).module,
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
        requireActivity().onBackPressed()
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun startShimmer() {
        binding.shimmerContainer.startShimmer();
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

    private fun validateModule(
        jsonObject: JSONObject,
        modules: Modules?,
        formControl: FormControl?
    ) {
        val action = actionControls.value?.single { a -> a.controlID == formControl?.controlID }
        try {
            accountNumber.value = jsonObject.getString("ACCOUNTID")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        LoadingFragment.show(requireActivity().supportFragmentManager)
        subscribe.add(
            validationViewModel.validation(
                modules?.moduleID, action?.merchantID, jsonObject, requireContext()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    LoadingFragment.dismiss(requireActivity().supportFragmentManager)

                    try {
                        if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                            AppLogger.instance.appLog(this.tag!!, it.response!!)
                            val resData = DynamicDataResponseTypeConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    validationViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    validationViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            AppLogger.instance.appLog(this.tag!!, Gson().toJson(resData))
                            if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                dynamicResponse.value = resData
                                ShowToast(requireContext(), resData?.message)
                                setOnNextModule(formControl, modules)
                            } else {
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
                    LoadingFragment.dismiss(requireActivity().supportFragmentManager)
                    it.printStackTrace()
                })
        )
    }

    override fun onRecent(formControl: FormControl) {
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

    private fun setOnNextModule(formControl: FormControl?, modules: Modules?) {
        subscribe.add(widgetViewModel.getFormControl(
            modules?.moduleID, formControl?.formSequence!!.toInt().plus(1).toString()
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
                    if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                        try {
                            val moduleData = DynamicDataResponseTypeConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                                    true,
                                    widgetViewModel.storageDataSource.deviceData.value!!.run
                                )
                            )
                            if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                                moduleDataRes.value = moduleData
                            }
                            val recent: MenuItem = binding.toolbar.menu.findItem(R.id.actionRecent)
                            if (!recent.isVisible) {
                                recent.isVisible = true
                                actionControls.removeObservers(this)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ShowToast(requireContext(), getString(R.string.error_fetching_recent))
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

    override fun setFormNavigation(forms: MutableList<FormControl>?, modules: Modules?) {
        val destination = forms?.map { it.formID }?.first()
        when (BaseClass.nonCaps(destination)) {
            BaseClass.nonCaps(FormNavigation.PAYMENT.name) -> onPayment(forms, modules)
            else -> {
                throw Exception("No page implemented")
            }
        }
    }

    private fun onPayment(form: List<FormControl>?, modules: Modules?) {
        PurchaseFragment.setData(
            GroupForm(
                module = modules!!,
                form = form?.toMutableList()
            )
        )
        ((requireActivity()) as MainActivity)
            .provideNavigationGraph()
            .navigate(widgetViewModel.navigation().navigatePurchase(accountNumber.value))
    }

    override fun onMenuItem() {
        RecentFragment.showDialog(childFragmentManager, moduleDataRes.value!!)
    }
}