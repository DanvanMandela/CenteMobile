package com.craft.silicon.centemobile.view.fragment.auth.pin

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse
import com.craft.silicon.centemobile.databinding.FragmentPrePinMobileBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.TextHelper
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.LoadingFragment
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.fragment.go.steps.OTP
import com.craft.silicon.centemobile.view.fragment.go.steps.OTPCountDownTimer
import com.craft.silicon.centemobile.view.fragment.levels.LevelOneFragment
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrePinMobileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class PrePinMobileFragment : Fragment(), AppCallbacks, OTP, View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentPrePinMobileBinding

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

    override fun setOnClick() {
        binding.materialButton.setOnClickListener(this)
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
        binding = FragmentPrePinMobileBinding.inflate(inflater, container, false)
        setBinding()
        setToolbar()
        setOnClick()
        setOTP()
        setTextWatchers()
        return binding.root.rootView
    }

    private fun setOTP() {
        baseViewModel.dataSource.setOtp("")
        val otp = baseViewModel.dataSource.otp.asLiveData()
        otp.observe(viewLifecycleOwner) {
            binding.loadingFrame.verificationCodeEditText.setText(it)
        }
    }

    private fun setLoading(b: Boolean) {
        if (b) LoadingFragment.show(this.childFragmentManager)
        else LoadingFragment.dismiss(this.childFragmentManager)
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            if (isOTP) {
                setOtp(false)
            } else requireActivity().onBackPressed()
        }
    }

    private fun setOtp(b: Boolean) {

        isOTP = b
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


    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrePinMobileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrePinMobileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = PrePinMobileFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.materialButton) {
            if (isOTP) {
                if (TextUtils.isEmpty(binding.loadingFrame.verificationCodeEditText.text.toString())) {
                    ShowToast(requireContext(), getString(R.string.otp_required), true)
                } else {
                    if (binding.loadingFrame.verificationCodeEditText.text.toString().length < 6) {
                        ShowToast(requireContext(), getString(R.string.otp_invalid), true)
                    } else resetPin()
                }
            } else {
                createOTP()
            }
        }
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.userFrame.editAccountNumber.text)) {
            ShowToast(
                requireContext(),
                getString(R.string.enter_account_number), true
            )
            false
        } else if (TextUtils.isEmpty(binding.userFrame.editATM.text)) {
            ShowToast(
                requireContext(),
                getString(R.string.enter_atm_number), true
            )
            false
        } else if (TextUtils.isEmpty(binding.userFrame.editATMPin.text)) {
            ShowToast(
                requireContext(),
                getString(R.string.enter_atm_pin), true
            )
            false
        } else {
            if (binding.userFrame.editAccountNumber.length() < 8) {
                ShowToast(
                    requireContext(),
                    getString(R.string.account_number_invalid), true
                )
                false
            } else if (binding.userFrame.editATM.length() < 12) {
                ShowToast(
                    requireContext(),
                    getString(R.string.atm_number_invalid), true
                )
                false
            } else if (binding.userFrame.editATMPin.length() < 4) {
                ShowToast(
                    requireContext(),
                    getString(R.string.atm_pin_invalid), true
                )
                false
            } else true
        }
    }

    private fun setTextWatchers() {
        binding.userFrame.editATM.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (!TextHelper.isInputCorrect(
                        editable,
                        TextHelper.CARD_NUMBER_TOTAL_SYMBOLS,
                        TextHelper.CARD_NUMBER_DIVIDER_MODULO,
                        TextHelper.CARD_NUMBER_DIVIDER
                    )
                ) {
                    editable.replace(
                        0, editable.length,
                        TextHelper.concatString(
                            TextHelper.getDigitArray(
                                editable,
                                TextHelper.CARD_NUMBER_TOTAL_DIGITS
                            ),
                            TextHelper.CARD_NUMBER_DIVIDER_POSITION, TextHelper.CARD_NUMBER_DIVIDER
                        )
                    )
                }
            }
        })
    }

    private fun resetPin() {
        setLoading(true)
        val jsonObject = JSONObject()
        val encrypted = JSONObject()
        try {
            val mobile =
                binding.userFrame.countryCodeHolder.selectedCountryCode +
                        binding.userFrame.editMobile.text.toString()
            jsonObject.put(
                "BANKACCOUNTID", Objects
                    .requireNonNull(binding.userFrame.editAccountNumber.text).toString()
            )
            jsonObject.put(
                "MOBILENUMBER", mobile
            )
            jsonObject.put("OTPKEY", binding.loadingFrame.verificationCodeEditText.text.toString())
            encrypted.put(
                "CARDNUMBER", BaseClass.newEncrypt(
                    Objects.requireNonNull(binding.userFrame.editATM.text)
                        .toString().replace("-", "")
                )
            )
            encrypted.put(
                "CARDPIN", BaseClass
                    .newEncrypt(
                        Objects.requireNonNull(binding.userFrame.editATMPin.text).toString()
                    )
            )
            composite.add(
                baseViewModel.pinResetPre(jsonObject, encrypted, requireContext())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ res: DynamicResponse? ->
                        setOnSuccess(res)
                    }, { obj: Throwable -> obj.printStackTrace() })
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setOnSuccess(res: DynamicResponse?) {
        try {
            AppLogger().appLog(
                "RESET:PIN:Response",
                BaseClass.decryptLatest(
                    res!!.response,
                    baseViewModel.dataSource.deviceData.value!!.device,
                    true,
                    baseViewModel.dataSource.deviceData.value!!.run
                )
            )
            if (BaseClass.nonCaps(res.response) != StatusEnum.ERROR.type) {
                try {
                    val moduleData = DynamicDataResponseTypeConverter().to(
                        BaseClass.decryptLatest(
                            res.response,
                            baseViewModel.dataSource.deviceData.value!!.device,
                            true,
                            baseViewModel.dataSource.deviceData.value!!.run
                        )
                    )
                    AppLogger.instance.appLog(
                        "${LevelOneFragment::class.java.simpleName}:E:ResetPin",
                        Gson().toJson(moduleData)
                    )
                    if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                        setLoading(false)
                        SuccessDialogFragment.showDialog(
                            DialogData(
                                title = R.string.success,
                                subTitle = moduleData?.message!!,
                                R.drawable.success
                            ),
                            requireActivity().supportFragmentManager, this
                        )
                    } else if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.FAILED.type) {
                        setLoading(false)
                        showError(moduleData?.message!!)
                    } else if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.TOKEN.type) {
                        workerViewModel.routeData(viewLifecycleOwner,
                            object : WorkStatus {
                                override fun workDone(b: Boolean) {
                                    if (b) {
                                        setLoading(false)
                                        resetPin()
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
    }


    private fun createOTP() {
        (requireActivity() as MainActivity).initSMSBroadCast()
        binding.loadingFrame.verificationCodeEditText.setText("")
        if (TextUtils.isEmpty(binding.userFrame.editMobile.text.toString())) {
            ShowToast(requireContext(), getString(R.string.mobile_required), true)
        } else if (TextUtils.isEmpty(binding.userFrame.editAccountNumber.text.toString())) {
            ShowToast(requireContext(), getString(R.string.enter_account_number), true)
        } else {
            if (binding.userFrame.editMobile.text?.length!! < 8) {
                ShowToast(requireContext(), getString(R.string.invalid_mobile), true)
            } else {
                if (validateFields()) {
                    setLoading(true)
                    val mobile =
                        binding.userFrame.countryCodeHolder.selectedCountryCode +
                                binding.userFrame.editMobile.text.toString()
                    val json = JSONObject()
                    json.put("MOBILENUMBER", mobile)
                    json.put("BANKACCOUNTID", binding.userFrame.editAccountNumber.text.toString())
                    json.put("SERVICENAME", "PINRESET")

                    composite.add(
                        baseViewModel.createOTP(json, requireContext())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                try {
                                    AppLogger().appLog(
                                        "PRE:PIN:Response",
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
                                                "${LevelOneFragment::class.java.simpleName}:E:PREPIN",
                                                Gson().toJson(moduleData)
                                            )
                                            if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                                                setTimer()
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
                                setLoading(false)
                                showError(getString(R.string.something_))
                                it.printStackTrace()
                            })
                    )
                }
            }
        }
    }

    private fun validateOTP() {
        if (TextUtils.isEmpty(binding.loadingFrame.verificationCodeEditText.text.toString())) {
            ShowToast(requireContext(), getString(R.string.otp_required))
        } else {
            setLoading(true)
            val mobile =
                binding.userFrame.countryCodeHolder.selectedCountryCode +
                        binding.userFrame.editMobile.text.toString()
            val json = JSONObject()
            json.put("MOBILENUMBER", mobile)
            json.put(
                "OTPKEY",
                binding.loadingFrame.verificationCodeEditText.text.toString()
            )
            json.put("SERVICENAME", "PINRESET")
            composite.add(
                baseViewModel.validateOTP(json, JSONObject(), requireContext())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        try {
                            AppLogger().appLog(
                                "PREPIN:VALIDATION:Response",
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
                                        "${LevelOneFragment::class.java.simpleName}:E:PRE",
                                        Gson().toJson(moduleData)
                                    )
                                    if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                                        setLoading(false)
                                        SuccessDialogFragment.showDialog(
                                            DialogData(
                                                title = R.string.success,
                                                subTitle = moduleData?.message!!,
                                                R.drawable.success
                                            ),
                                            requireActivity().supportFragmentManager, this
                                        )
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
        }//3201922463
    }

    override fun navigateUp() {
        pagerData?.completeStep()
    }
}

