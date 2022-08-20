package com.craft.silicon.centemobile.view.fragment.dynamic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.control.FormNavigation
import com.craft.silicon.centemobile.data.model.module.ModuleCategory
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.databinding.FragmentDynamicBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.binding.navigate
import com.craft.silicon.centemobile.view.binding.setDynamic
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.GroupForm
import com.craft.silicon.centemobile.view.ep.data.GroupModule
import com.craft.silicon.centemobile.view.fragment.auth.bio.BiometricFragment
import com.craft.silicon.centemobile.view.fragment.payment.PurchaseFragment
import com.craft.silicon.centemobile.view.fragment.validation.ValidationFragment
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * A simple [Fragment] subclass.
 * Use the [DynamicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DynamicFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private lateinit var binding: FragmentDynamicBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDynamicBinding.inflate(inflater, container, false)
        setBinding()
        setController()
        return binding.root.rootView
    }

    override fun setBinding() {
        startShimmer()
        binding.callback = this
        binding.data = navigationData
    }


    override fun setController() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            binding.container.setDynamic(
                callbacks = this,
                dynamic = navigationData,
                storage = widgetViewModel.storageDataSource
            )
        }, animationDuration.toLong())
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun startShimmer() {
        binding.shimmerContainer.startShimmer()
    }

    companion object {
        private var navigationData: DynamicData? = null

        @JvmStatic
        fun newInstance() = DynamicFragment()

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data NavigationData.
         * @return A new instance of fragment DynamicFragment.
         */
        @JvmStatic
        fun setData(data: DynamicData) =
            DynamicFragment().apply {
                this@Companion.navigationData = data
            }
    }

    override fun setFormNavigation(forms: MutableList<FormControl>?, modules: Modules?) {
        val destination = forms?.map { it.formID }?.first()
        when (BaseClass.nonCaps(destination)) {
            BaseClass.nonCaps(FormNavigation.VALIDATE.name) -> {
                when (BaseClass.nonCaps(modules?.moduleID)) {
                    BaseClass.nonCaps(FormNavigation.FINGERPRINT.name) -> onBiometric(
                        forms,
                        modules
                    )
                    else -> onValidate(forms, modules)
                }
            }

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
            ),
            response = null,
            map = null
        )
        navigate(widgetViewModel.navigation().navigatePurchase())
    }

    private fun onValidate(form: List<FormControl>?, modules: Modules?) {
        ValidationFragment.setData(
            GroupForm(
                module = modules!!,
                form = form?.toMutableList()
            )
        )
        navigate(widgetViewModel.navigation().navigateValidation())
    }

    override fun navigateUp() {
        (requireActivity() as MainActivity).onBackPressed()
    }

    override fun onModule(modules: Modules?) {
        if (modules?.available!!)
            if (modules.moduleURLTwo != null) {
                if (!TextUtils.isEmpty(modules.moduleURLTwo)) {
                    openUrl(modules.moduleURLTwo)
                } else navigateTo(modules)
            } else navigateTo(modules)
        else ShowToast(requireContext(), modules.message)
    }

    private fun getFormControl(modules: Modules) {
        subscribe.add(widgetViewModel.getFormControl(modules.moduleID, "1")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ f: List<FormControl> ->
                setFormNavigation(f.toMutableList(), modules)
            }) { obj: Throwable -> obj.printStackTrace() })
    }

    override fun openUrl(url: String?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun onBiometric(forms: MutableList<FormControl>?, modules: Modules?) {
        BiometricFragment.setData(
            form = forms,
            module = modules
        )
        navigate(widgetViewModel.navigation().navigationBio())
    }

    private fun navigateTo(modules: Modules?) {
        AppLogger.instance.appLog("Module", Gson().toJson(modules))
        if (modules!!.ModuleCategory == ModuleCategory.BLOCK.type) {
            subscribe.add(widgetViewModel.getModules(modules.moduleID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ f: List<Modules> ->
                    filterModules(f, modules)
                }) { obj: Throwable -> obj.printStackTrace() })
        } else getFormControl(modules)
    }

    private fun filterModules(f: List<Modules>, modules: Modules) {
        val filterModule = mutableListOf<Modules>()
        val moduleList =
            widgetViewModel.storageDataSource.disableModule.value
        if (moduleList != null) {
            if (moduleList.isNotEmpty()) {
                f.forEach { s ->
                    moduleList.forEach { i ->
                        if (s.moduleID == i?.id) {
                            s.available = false
                            s.message = i?.message
                        }
                    }
                    filterModule.add(s)
                }
                navigate(
                    widgetViewModel.navigation()
                        .navigateToLevelOne(GroupModule(modules, filterModule))
                )
            } else navigate(
                widgetViewModel.navigation()
                    .navigateToLevelOne(GroupModule(modules, f.toMutableList()))
            )
        } else navigate(
            widgetViewModel.navigation()
                .navigateToLevelOne(GroupModule(modules, f.toMutableList()))
        )
    }


}