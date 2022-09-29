package com.elmacentemobile.view.fragment.go.steps

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.elmacentemobile.R
import com.elmacentemobile.data.model.SmartLifeConverter
import com.elmacentemobile.data.model.SmartLifeResponseTypeConverter
import com.elmacentemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.databinding.FragmentSmartLifeBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.dialog.AlertDialogFragment
import com.elmacentemobile.view.dialog.DialogData
import com.elmacentemobile.view.dialog.LoadingFragment

import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.WorkStatus
import com.elmacentemobile.view.model.WorkerViewModel
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
 * Use the [SmartLifeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class SmartLifeFragment : BottomSheetDialogFragment(), AppCallbacks, OTP {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSmartLifeBinding

    private val baseViewModel: BaseViewModel by viewModels()
    private val workerViewModel: WorkerViewModel by viewModels()
    private val composite = CompositeDisposable()

    private val startTime = (120 * 1000).toLong()
    private val interval = (1 * 1000).toLong()

    private var countDownTimer: CountDownTimer? = null

    private var isOTP = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private fun timerControl(startTimer: Boolean) {
        if (startTimer) {
            countDownTimer!!.start()
        } else {
            countDownTimer!!.cancel()
        }
    }

    override fun timer(str: String) {
        binding.loadingFrame.otpTimer.text = str
    }

    override fun done(boolean: Boolean) {
        if (boolean) {
            timerControl(false)
            binding.loadingFrame.resendButton.visibility = View.VISIBLE
        } else binding.loadingFrame.resendButton.visibility = View.GONE
    }

    private fun setTimer() {
        binding.loadingFrame.resendLay.visibility = View.VISIBLE
        countDownTimer = OTPCountDownTimer(startTime = startTime, interval = interval, this)
        timerControl(true)
        done(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSmartLifeBinding.inflate(inflater, container, false)
        setBinding()
        setToolbar()
        setOnClick()
        return binding.root.rootView
    }

    override fun setOnClick() {
        binding.button.setOnClickListener {
            if (isOTP) {
                validateOTP()
            } else {
                createOTP()
            }
        }

        binding.loadingFrame.resendButton.setOnClickListener {
            createOTP()
        }
    }

    private fun createOTP() {
        if (TextUtils.isEmpty(binding.userFrame.nInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.nssf_number_required))
        } else {
            setLoading(true)
            val json = JSONObject()
            json.put("ACCOUNTID", binding.userFrame.nInput.text.toString())
            json.put("INFOFIELD1", "REQUESTOTP")

            composite.add(
                baseViewModel.createNSSFOTP(json, requireContext())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        try {
                            AppLogger().appLog(
                                "NSSF:OTP:Response",
                                BaseClass.decryptLatest(
                                    it.response,
                                    baseViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    baseViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                                try {
                                    val moduleData = DynamicDataResponseTypeConverter().to(
                                        BaseClass.decryptLatest(
                                            it.response,
                                            baseViewModel.dataSource.deviceData.value!!.device,
                                            true,
                                            baseViewModel.dataSource.deviceData.value!!.run
                                        )
                                    )
                                    AppLogger.instance.appLog(
                                        "${SmartLifeFragment::class.java.simpleName}:E:NSSF",
                                        Gson().toJson(moduleData)
                                    )
                                    if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                                        setTimer()
                                        setProgress(80)
                                        isOTP = true
                                        setOtp(true)
                                        setLoading(false)
                                    } else if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.FAILED.type) {
                                        setLoading(false)
                                        showError(moduleData?.message!!)
                                    } else if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.TOKEN.type) {
                                        workerViewModel.routeData(viewLifecycleOwner,
                                            object : WorkStatus {
                                                override fun workDone(b: Boolean) {
                                                    if (b) {
                                                        setLoading(false)
                                                        createOTP()
                                                    }
                                                }
                                            })
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } catch (e: Exception) {
                            showError(getString(R.string.something_))
                            setLoading(false)
                        }
                    }, {
                        showError(getString(R.string.something_))
                        setLoading(false)
                        it.printStackTrace()
                    })
            )
        }
    }

    private fun validateOTP() {
        if (TextUtils.isEmpty(binding.loadingFrame.verificationCodeEditText.text.toString())) {
            ShowToast(requireContext(), getString(R.string.otp_required))
        } else {
            setLoading(true)
            val json = JSONObject()
            json.put("ACCOUNTID", binding.userFrame.nInput.text.toString())
            json.put(
                "INFOFIELD2",
                binding.loadingFrame.verificationCodeEditText.text.toString()
            )
            json.put("INFOFIELD1", "VALIDATEACCOUNT")
            composite.add(
                baseViewModel.createNSSFOTP(json, requireContext())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        try {
                            AppLogger().appLog(
                                "NSSF:VALIDATION:Response",
                                BaseClass.decryptLatest(
                                    it.response,
                                    baseViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    baseViewModel.dataSource.deviceData.value!!.run
                                )
                            )

                            if (BaseClass.nonCaps(it.response) != StatusEnum.ERROR.type) {
                                try {
                                    val moduleData = SmartLifeResponseTypeConverter().to(
                                        BaseClass.decryptLatest(
                                            it.response,
                                            baseViewModel.dataSource.deviceData.value!!.device,
                                            true,
                                            baseViewModel.dataSource.deviceData.value!!.run
                                        )
                                    )
                                    AppLogger.instance.appLog(
                                        "${SmartLifeFragment::class.java.simpleName}:E:NSSF",
                                        Gson().toJson(moduleData)
                                    )
                                    val sRes = SmartLifeConverter().to(moduleData?.returnObject)
                                    if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                                        var parentDetails =
                                            baseViewModel.dataSource.parentDetails.value

                                        if (parentDetails == null) {
                                            parentDetails = ParentDetails(
                                                fFName = sRes?.fatherSurname,
                                                fMName = null,
                                                fLName = null,
                                                mFName = sRes?.motherSurname,
                                                mMName = null,
                                                mLName = null,
                                                duration = null,
                                                exposed = null,
                                                homeDistrict = null,
                                                autoPosition = null
                                            )
                                            baseViewModel.dataSource.setParentDetails(parentDetails)
                                        }

                                        callbacks.onOCR(
                                            OCRData(
                                                names = sRes?.name,
                                                surname = sRes?.surname,
                                                idNo = sRes?.number,
                                                dob = sRes?.dob,
                                                otherName = sRes?.othername,
                                                gender = sRes?.gender
                                            )
                                        )
                                        setProgress(100)
                                        setLoading(false)
                                        navigateUp()

                                    } else if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.FAILED.type) {
                                        setLoading(false)
                                        showError(moduleData?.message!!)
                                    } else if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.TOKEN.type) {
                                        workerViewModel.routeData(viewLifecycleOwner,
                                            object : WorkStatus {
                                                override fun workDone(b: Boolean) {
                                                    if (b) {
                                                        setLoading(false)
                                                        createOTP()
                                                    }
                                                }
                                            })
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } catch (e: Exception) {
                            showError(getString(R.string.something_))
                            setLoading(false)
                        }
                    }, {
                        showError(getString(R.string.something_))
                        setLoading(false)
                        it.printStackTrace()
                    })
            )
        }
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            if (isOTP) {
                setProgress(50)
                isOTP = false
                setOtp(false)
            } else dialog?.dismiss()
        }
    }

    private fun setOtp(b: Boolean) {
        if (b) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }

    private fun showError(string: String) {
        AlertDialogFragment.newInstance(
            DialogData(
                R.string.error,
                string,
                R.drawable.warning_app
            ), childFragmentManager
        )
    }

    private fun setLoading(b: Boolean) {
        if (b) LoadingFragment.show(this.childFragmentManager)
        else LoadingFragment.dismiss(this.childFragmentManager)
    }

    override fun setBinding() {
        setProgress(50)
    }

    private fun setProgress(int: Int) {
        binding.progressIndicator.progress = int
    }

    companion object {
        private lateinit var callbacks: AppCallbacks

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SmartLifeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SmartLifeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun showDialog(manager: FragmentManager, appCallbacks: AppCallbacks) =
            SmartLifeFragment().apply {
                this@Companion.callbacks = appCallbacks
                show(manager, this.tag)
            }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setCanceledOnTouchOutside(false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
    }

    override fun navigateUp() {
        dialog?.dismiss()
    }
}