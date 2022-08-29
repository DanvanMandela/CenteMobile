package com.craft.silicon.centemobile.view.fragment.beneficiary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.action.ActionControls
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse
import com.craft.silicon.centemobile.databinding.FragmentBeneficiaryManagementBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.navigate
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.InfoFragment
import com.craft.silicon.centemobile.view.dialog.MainDialogData
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment
import com.craft.silicon.centemobile.view.ep.adapter.TransactionAdapterItem
import com.craft.silicon.centemobile.view.ep.controller.BeneficiaryList
import com.craft.silicon.centemobile.view.ep.controller.MainDisplayController
import com.craft.silicon.centemobile.view.ep.data.Nothing
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
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "module"

/**
 * A simple [Fragment] subclass.
 * Use the [BeneficiaryManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BeneficiaryManagementFragment : Fragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var data: Modules? = null
    private val baseViewModel: BaseViewModel by viewModels()

    private val subscribe = CompositeDisposable()
    private lateinit var adapter: TransactionAdapterItem

    private lateinit var controller: MainDisplayController

    private val widgetViewModel: WidgetViewModel by viewModels()

    private lateinit var binding: FragmentBeneficiaryManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable(ARG_DATA)
            AppLogger.instance.appLog(
                this@BeneficiaryManagementFragment::class.java.simpleName,
                Gson().toJson(data)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBeneficiaryManagementBinding.inflate(inflater, container, false)
        setController()
        setBinding()
        setToolbar()
        return binding.root.rootView
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner

        binding.callback = this
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setData()
        }, animationDuration.toLong())

    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun setData() {
        binding.container.setController(controller)
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        if (data != null) {
            binding.toolbar.title = data?.moduleName
        }

    }

    override fun deleteBeneficiary(modules: Modules?, beneficiary: Beneficiary?) {
        MaterialAlertDialogBuilder(requireContext())
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

    private fun setLoading(b: Boolean) {
        if (b) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }

    private fun showError(data: MainDialogData) {
        navigate(baseViewModel.navigationData.navigateToAlertDialog(data))

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
                requireActivity(),
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
                            InfoFragment.showDialog(this.childFragmentManager)
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

    override fun setController() {
        controller = MainDisplayController(this)
        val staticData = baseViewModel.dataSource.beneficiary.asLiveData()
        staticData.observe(viewLifecycleOwner) {
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
            requireActivity().supportFragmentManager, callback
        )
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data: Modules
         * @return A new instance of fragment BeneficiaryManagementFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: Modules) =
            BeneficiaryManagementFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, data)
                }
            }
    }

    override fun navigateUp() {
        requireActivity().onBackPressed()
    }
}

