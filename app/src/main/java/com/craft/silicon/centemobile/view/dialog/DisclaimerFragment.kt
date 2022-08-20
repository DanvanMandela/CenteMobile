package com.craft.silicon.centemobile.view.dialog

import android.app.Dialog
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.converter.DynamicAPIResponseConverter
import com.craft.silicon.centemobile.data.source.constants.Constants
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.FragmentDisclaimerBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.model.AuthViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DisclaimerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DisclaimerFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDisclaimerBinding
    private val compositeDisposable = CompositeDisposable()
    private val authViewModel: AuthViewModel by viewModels()

    private val workerViewModel: WorkerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDisclaimerBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        setBinding()
        return binding.root.rootView
    }

    override fun setOnClick() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.materialButton2.setOnClickListener {
            saveDeviceData()
        }
    }

    private fun saveDeviceData() {
        val json = JSONObject()
        json.put("DeviceID", Constants.getIMEIDeviceId(requireContext()))
        json.put("DeviceName", Build.BRAND)
        json.put("DeviceMake", Build.BRAND)
        json.put("DeviceModel", Build.MODEL)
        json.put(
            "DeviceOSVersion",
            VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name
        )
        compositeDisposable.add(
            authViewModel.deviceRegister(json, requireActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    try {
                        AppLogger.instance.appLog(
                            "Device:REG:Response", BaseClass.decryptLatest(
                                it.response,
                                authViewModel.storage.deviceData.value!!.device,
                                true,
                                authViewModel.storage.deviceData.value!!.run
                            )
                        )
                        if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                            val resData = DynamicAPIResponseConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    authViewModel.storage.deviceData.value!!.device,
                                    true,
                                    authViewModel.storage.deviceData.value!!.run
                                )
                            )
                            AppLogger.instance.appLog("DeviceREg", Gson().toJson(resData))
                            if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                (requireActivity()).onBackPressed()
                            } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                workerViewModel.routeData(viewLifecycleOwner, object : WorkStatus {
                                    override fun workDone(b: Boolean) {
                                        if (b) saveDeviceData()
                                    }
                                })
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
                        setLoading(false)
                        AlertDialogFragment.newInstance(
                            DialogData(
                                title = R.string.error,
                                subTitle = getString(R.string.something_),
                                R.drawable.warning_app
                            ),
                            requireActivity().supportFragmentManager
                        )
                    }
                }, { it.printStackTrace() })
        )
        compositeDisposable.add(
            authViewModel.loading.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setLoading(it) }, { it.printStackTrace() })
        )
    }

    private fun setLoading(b: Boolean) {
        if (b) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }

    override fun setBinding() {
        binding.deviceMake.text = Build.BRAND
        binding.deviceModel.text = Build.MODEL
        binding.deviceOS.text =
            VERSION_CODES::class.java.fields[Build.VERSION.SDK_INT].name
    }

    companion object {
        private lateinit var callbacks: AppCallbacks

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DisclaimerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisclaimerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { data ->
                val behaviour = BottomSheetBehavior.from(data)
                setupFullHeight(data)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.setDraggable(false)
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}