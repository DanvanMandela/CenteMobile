package com.craft.silicon.centemobile.view.fragment.go.steps

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.converter.DynamicAPIResponseConverter
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.FragmentGoOTPBinding
import com.craft.silicon.centemobile.util.*
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
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
 * Use the [GoOTPFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class GoOTPFragment : Fragment(), AppCallbacks, PagerData, OTP, OnAlertDialog,
    View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentGoOTPBinding
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private var countDownTimer: CountDownTimer? = null
    private val subscribe = CompositeDisposable()
    private val workViewModel: WorkerViewModel by viewModels()

    private val startTime = (120 * 1000).toLong()
    private val interval = (1 * 1000).toLong()

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
        binding = FragmentGoOTPBinding.inflate(inflater, container, false)
        setBinding()
        setToolbar()
        setStep()
        setOnClick()
        setOtp()
        return binding.root.rootView
    }

    private fun setOtp() {
        val otpL = baseViewModel.dataSource.otp.asLiveData()
        otpL.observe(viewLifecycleOwner) {
            if (it != null) setTimer()
            binding.verificationCodeEditText.setText(it)
        }
    }


    private fun validateOtp() {
        setLoading(true)
        val userData = widgetViewModel.storageDataSource.addressState.value
        val phoneData = "${userData?.phone?.key}${userData?.phone?.value}"

        val json = JSONObject()
        val encrypted = JSONObject()
        json.put("MOBILENUMBER", phoneData)
        json.put("EMAILID", userData?.email)
        json.put("SERVICENAME", "SELFRAO")
        json.put("OTPKEY", binding.verificationCodeEditText.text.toString())

        subscribe.add(
            baseViewModel.validateOTP(json, encrypted, requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    try {
                        AppLogger.instance.appLog(
                            "OTP:Validate:Response", BaseClass.decryptLatest(
                                it.response,
                                baseViewModel.dataSource.deviceData.value!!.device,
                                true,
                                baseViewModel.dataSource.deviceData.value!!.run
                            )
                        )



                        if (it.response == StatusEnum.ERROR.type ||
                            it.response.isNullOrBlank()
                        ) {
                            setLoading(false)
                            showError(getString(R.string.something_))
                        } else {
                            val resData = DynamicAPIResponseConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    baseViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    baseViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            AppLogger.instance.appLog(
                                "OTP:Validate:Response",
                                Gson().toJson(resData)
                            )
                            if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                setLoading(false)
                                ShowToast(requireContext(), resData?.message)
                                completeStep()
                            } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                workViewModel.routeData(viewLifecycleOwner, object : WorkStatus {
                                    override fun workDone(b: Boolean) {
                                        setLoading(false)
                                        if (b) validateOtp()
                                    }
                                })
                            } else {
                                setLoading(false)
                                showError(resData?.message)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        setLoading(false)
                    }
                }, {
                    setLoading(false)
                    showError(getString(R.string.something_))
                    it.printStackTrace()
                })
        )

    }

    override fun completeStep() {
        setLoading(true)
        val productData = widgetViewModel.storageDataSource.customerProduct.value
        val idData = widgetViewModel.storageDataSource.onIDDetails.value
        val parentData = widgetViewModel.storageDataSource.parentDetails.value
        val income = widgetViewModel.storageDataSource.incomeSource.value
        val nok = widgetViewModel.storageDataSource.nextOfKin.value
        val address = widgetViewModel.storageDataSource.addressState.value
        val services = widgetViewModel.storageDataSource.otherServices.value
        val recommend = widgetViewModel.storageDataSource.recommendState.value

        val json = JSONObject()

        json.put("INFOFIELD1", productData?.type?.value)
        json.put("INFOFIELD2", productData?.product?.value)
        json.put("INFOFIELD3", idData?.data?.names)
        json.put("INFOFIELD4", idData?.data?.otherName)
        json.put("INFOFIELD5", idData?.data?.surname)
        json.put("INFOFIELD6", idData?.data?.dob)
        json.put("INFOFIELD7", idData?.data?.idNo)
        json.put("INFOFIELD8", "${address?.phone?.key}${address?.phone?.value}")
        json.put("INFOFIELD9", "${address?.phoneTwo?.key}${address?.phoneTwo?.value}")
        json.put("INFOFIELD10", address?.email)
        json.put("INFOFIELD11", idData?.data?.gender)
        json.put("INFOFIELD12", idData?.title)
        json.put("INFOFIELD13", productData?.currency?.value)
        json.put("INFOFIELD14", productData?.branch?.value)
        json.put("INFOFIELD15", productData?.product?.value)
        json.put("INFOFIELD16", parentData?.fFName)
        json.put("INFOFIELD17", parentData?.fMName)
        json.put("INFOFIELD18", parentData?.fLName)
        json.put("INFOFIELD19", parentData?.mFName)
        json.put("INFOFIELD20", parentData?.fMName)
        json.put("INFOFIELD21", parentData?.fLName)
        json.put("INFOFIELD22", recommend?.location)
        json.put("INFOFIELD23", address?.address)
        json.put("INFOFIELD24", parentData?.homeDistrict)
        json.put("INFOFIELD25", parentData?.duration?.extra)
        json.put("INFOFIELD26", parentData?.exposed?.value)
        json.put("INFOFIELD27", address?.city)
        json.put("INFOFIELD28", address?.countryCode)
        json.put("INFOFIELD29", NumberTextWatcherForThousand.trimCommaOfString(income?.income))
        json.put("INFOFIELD30", income?.profession?.value)
        json.put("INFOFIELD31", income?.occupation?.value)
        json.put("INFOFIELD32", income?.workPlace)
        json.put("INFOFIELD33", income?.natureBusiness)
        json.put("INFOFIELD34", income?.duration?.extra)
        json.put("INFOFIELD35", income?.employer)
        json.put("INFOFIELD36", income?.natureEmployment)
        json.put("INFOFIELD37", nok?.firstName)
        json.put("INFOFIELD38", nok?.middleName)
        json.put("INFOFIELD39", nok?.lastName)
        json.put("INFOFIELD40", "${nok?.phone?.key}${nok?.phone?.value}")
        json.put("INFOFIELD41", "${nok?.phoneTwo?.key}${nok?.phoneTwo?.value}")
        json.put("INFOFIELD42", nok?.address)
        json.put(
            "INFOFIELD43", "${services!!.services[0].value},${services.services[1].value}"
        )
        json.put("INFOFIELD44", recommend?.data?.extra)
        json.put("INFOFIELD45", idData?.id?.image)
        json.put("INFOFIELD46", idData?.signature?.image)
        json.put("INFOFIELD47", idData?.selfie?.image)
        json.put("INFOFIELD48", address?.ea?.extra)
        json.put("ACCOUNTID", nok?.account)

        subscribe.add(
            baseViewModel.registrationOnGO(json, requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    AppLogger.instance.appLog(
                        "CUSTOMER:EXIST:Response", BaseClass.decryptLatest(
                            it.response,
                            baseViewModel.dataSource.deviceData.value!!.device,
                            true,
                            baseViewModel.dataSource.deviceData.value!!.run
                        )
                    )
                    try {
                        if (it.response == StatusEnum.ERROR.type
                            || it.response.isNullOrBlank()
                        ) {
                            setLoading(false)
                            ShowToast(requireContext(), getString(R.string.something_))
                        } else {
                            val resData = DynamicAPIResponseConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    baseViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    baseViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                setLoading(false)
                                GoSuccessFragment.showDialog(
                                    this.childFragmentManager,
                                    pagerData,
                                    resData?.formField
                                )
                            } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                workViewModel.routeData(viewLifecycleOwner, object : WorkStatus {
                                    override fun workDone(b: Boolean) {
                                        setLoading(false)
                                        if (b) completeStep()
                                    }
                                })
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        setLoading(false)
                        ShowToast(requireContext(), getString(R.string.something_))
                    }

                }, {
                    setLoading(false)
                    ShowToast(requireContext(), getString(R.string.something_))
                    it.printStackTrace()
                })
        )


    }


    private fun showError(message: String?) {
        AlertDialogFragment.newInstance(
            DialogData(
                title = R.string.error,
                subTitle = message,
                R.drawable.warning_app
            ),
            requireActivity().supportFragmentManager
        )
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            ShowAlertDialog().showDialog(
                requireContext(),
                getString(R.string.exit_registration),
                getString(R.string.proceed_registration),
                this
            )
        }
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                ShowAlertDialog().showDialog(
                    requireContext(),
                    getString(R.string.exit_registration),
                    getString(R.string.proceed_registration),
                    this
                )
                true
            } else false
        }
    }

    override fun setOnClick() {
        binding.resendButton.setOnClickListener(this)
        binding.buttonBack.setOnClickListener(this)
        binding.buttonNext.setOnClickListener(this)
    }

    private fun setTimer() {
        binding.resendLay.visibility = VISIBLE
        val sData = widgetViewModel.storageDataSource.otpState.value
        countDownTimer = if (sData != null) {
            if (sData != 0L) {
                OTPCountDownTimer(startTime = sData, interval = interval, this)
            } else OTPCountDownTimer(startTime = startTime, interval = interval, this)
        } else OTPCountDownTimer(startTime = startTime, interval = interval, this)

        timerControl(true)
        done(false)
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(100, true)
    }


    private fun setLoading(it: Boolean) {
        if (it) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }

    override fun onPositive() {
        saveState()
        pagerData?.currentPosition()
        Handler(Looper.getMainLooper()).postDelayed({
            (requireActivity() as MainActivity).provideNavigationGraph()
                .navigate(widgetViewModel.navigation().navigateLanding())
        }, 100)
    }

    override fun onNegative() {
        pagerData?.clearState()
        (requireActivity() as MainActivity).provideNavigationGraph()
            .navigate(widgetViewModel.navigation().navigateLanding())
    }

    override fun saveState() {
//        val state = binding.otpTimer.text.toString()
//        val s = state.replace(":", "").toInt()
//        val minutes: Long = TimeUnit.MILLISECONDS
//            .toMinutes(uptime)
//
//
//
//        widgetViewModel.storageDataSource.setOTPState(state)
    }


    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.verificationCodeEditText.text.toString())) {
            ShowToast(requireContext(), getString(R.string.otp_required), true)
            false
        } else true
    }

    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GoOTPFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GoOTPFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = GoOTPFragment().apply {
            this@Companion.pagerData = pagerData
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
        binding.otpTimer.text = str
    }

    override fun done(boolean: Boolean) {
        if (boolean) {
            timerControl(false)
            binding.resendButton.visibility = View.VISIBLE
        } else binding.resendButton.visibility = View.GONE
    }

    override fun onClick(p: View?) {
        if (p == binding.resendButton) resendOTP()
        else if (p == binding.buttonBack) pagerData?.onBack(10)
        else if (p == binding.buttonNext) {
            if (validateFields()) validateOtp()
        }
    }

    private fun resendOTP() {
        setTimer()
        pagerData?.createOtp()
    }


}

class OTPCountDownTimer(
    startTime: Long, interval: Long,
    private val otp: OTP
) :
    CountDownTimer(startTime, interval) {
    override fun onFinish() {
        otp.done(true)
    }

    override fun onTick(millisUntilFinished: Long) {
        val currentTime = millisUntilFinished / 1000
        otp.timer(
            "" + currentTime / 60 + " : " +
                    if (currentTime % 60 >= 10) currentTime % 60 else "0" + currentTime % 60
        )
    }
}


interface OTP {
    fun timer(str: String)
    fun done(boolean: Boolean)
    fun otp(str: String) {
        throw Exception("Not implemented")
    }
}