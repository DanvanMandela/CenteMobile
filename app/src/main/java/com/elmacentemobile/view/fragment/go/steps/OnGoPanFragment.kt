package com.elmacentemobile.view.fragment.go.steps

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.elmacentemobile.R
import com.elmacentemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.elmacentemobile.data.source.constants.Constants.removeLeadingZero
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.data.source.remote.callback.DynamicResponse
import com.elmacentemobile.databinding.FragmentOnGoPanBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.TextHelper
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.activity.level.FalconHeavyActivity
import com.elmacentemobile.view.binding.isOnline
import com.elmacentemobile.view.composable.keyboard.CustomKeyData
import com.elmacentemobile.view.composable.keyboard.CustomKeyboard
import com.elmacentemobile.view.composable.keyboard.KeyFunctionEnum
import com.elmacentemobile.view.dialog.AlertDialogFragment
import com.elmacentemobile.view.dialog.DialogData
import com.elmacentemobile.view.dialog.LoadingFragment
import com.elmacentemobile.view.dialog.SuccessDialogFragment
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
import org.json.JSONException
import org.json.JSONObject
import java.util.Objects
import java.util.Stack

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnGoPanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class OnGoPanFragment : BottomSheetDialogFragment(), AppCallbacks, OTP, View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentOnGoPanBinding

    private val baseViewModel: BaseViewModel by viewModels()
    private val workerViewModel: WorkerViewModel by viewModels()
    private val composite = CompositeDisposable()

    private var active: Boolean? = null

    private val startTime = (120 * 1000).toLong()
    private val interval = (1 * 1000).toLong()

    private var countDownTimer: CountDownTimer? = null

    private var isOTP = false

    private lateinit var pinStack: Stack<String>

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

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (isOTP) {
                    setOtp(false)
                } else dialog?.dismiss()
                true
            } else false
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

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            if (isOTP) {
                setOtp(false)
            } else dialog?.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnGoPanBinding.inflate(inflater, container, false)
        setToolbar()
        setTextWatchers()
        setOnClick()
        setOTP()
        setKeyboard()
        return binding.root.rootView
    }

    private fun setOTP() {
        baseViewModel.dataSource.setOtp("")
        val otp = baseViewModel.dataSource.otp.asLiveData()
        otp.observe(viewLifecycleOwner) {
            binding.loadingFrame.verificationCodeEditText.setText(it)
        }
    }


    override fun setOnClick() {
        binding.materialButton.setOnClickListener(this)
    }


    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.editAccountNumber.text)) {
            ShowToast(
                requireContext(),
                getString(R.string.enter_account_number), true
            )
            false
        } else if (TextUtils.isEmpty(binding.editATM.text)) {
            ShowToast(
                requireContext(),
                getString(R.string.enter_atm_number), true
            )
            false
        } else if (TextUtils.isEmpty(binding.editATMPin.text)) {
            ShowToast(
                requireContext(),
                getString(R.string.enter_atm_pin), true
            )
            false
        } else if (active == null && TextUtils.isEmpty(binding.editMobile.text.toString())) {
            ShowToast(
                requireContext(),
                getString(R.string.please_enter_your_mobile), true
            )
            false
        } else {
            if (binding.editAccountNumber.length() < 8) {
                ShowToast(
                    requireContext(),
                    getString(R.string.account_number_invalid), true
                )
                false
            } else if (active == null && binding.editMobile.text.toString().length < 8) {
                ShowToast(
                    requireContext(),
                    getString(R.string.enter_valid_mobile), true
                )
                false
            } else if (binding.editATM.length() < 12) {
                ShowToast(
                    requireContext(),
                    getString(R.string.atm_number_invalid), true
                )
                false
            } else if (binding.editATMPin.length() < 4) {
                ShowToast(
                    requireContext(),
                    getString(R.string.atm_pin_invalid), true
                )
                false
            } else true
        }
    }

    private fun setTextWatchers() {
        active = baseViewModel.dataSource.isActivated.value
        if (active == false) {
            binding.frameLayout.visibility = VISIBLE
        }
        binding.editATM.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

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
                            TextHelper.CARD_NUMBER_DIVIDER_POSITION,
                            TextHelper.CARD_NUMBER_DIVIDER
                        )
                    )
                }
            }
        })
    }

    private fun showError(string: String) {
        AlertDialogFragment.setCallback(
            this,
            DialogData(
                R.string.error,
                string,
                R.drawable.warning_app
            ), childFragmentManager
        )
    }

    override fun navigateUp() {
        checkCustomerProduct()
    }

    private fun checkCustomerProduct() {
        setLoading(true)
        val data = JSONObject()
        val prod = baseViewModel.dataSource.customerProduct.value
        val prods = baseViewModel.dataSource.productAccountData.value
        val p = prods?.find { it?.id == prod?.product?.value }?.description
        AppLogger.instance.appLog("PROD: ", "$p")
        data.put(
            "BANKACCOUNTID",
            binding.editAccountNumber.text.toString()
        )
        data.put("INFOFIELD1", prod?.product?.value)
        data.put("INFOFIELD2", p)
        data.put("INFOFIELD3", prod?.currency?.value)
        composite.add(
            baseViewModel.checkProductExist(data, requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res: DynamicResponse? ->
                    setOnProductSuccess(res)
                }, { obj: Throwable -> obj.printStackTrace() })
        )
    }

    private fun setOnProductSuccess(res: DynamicResponse?) {
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
                        "${OnGoPanFragment::class.java.simpleName}:E:ValidPin",
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
                            childFragmentManager, object : AppCallbacks {
                                override fun navigateUp() {
                                    dialog?.dismiss()
                                    callbacks.onSuccess()
                                }
                            }
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
                                        checkCustomerProduct()
                                    }
                                }
                            })
                    } else {
                        setLoading(false)
                        showError(moduleData?.message!!)
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

    private fun setLoading(b: Boolean) {
        if (b) LoadingFragment.show(this.childFragmentManager)
        else LoadingFragment.dismiss(this.childFragmentManager)
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


    companion object {
        private lateinit var callbacks: AppCallbacks

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnGoPanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnGoPanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun showDialog(manager: FragmentManager, appCallbacks: AppCallbacks) =
            OnGoPanFragment().apply {
                this@Companion.callbacks = appCallbacks
                show(manager, this.tag)
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
                    } else if (requireActivity().isOnline()) validateCard() else ShowToast(
                        requireContext(),
                        getString(R.string.no_connection),
                        true
                    )
                }
            } else {
                if (requireActivity().isOnline()) {
                    createOTP()
                } else ShowToast(
                    requireContext(),
                    getString(R.string.no_connection),
                    true
                )
            }
        }
    }

    private fun validateCard() {
        setLoading(true)
        val jsonObject = JSONObject()
        val encrypted = JSONObject()
        try {
            val mobile =
                if (active == false) binding.countryCodeHolder.selectedCountryCode +
                        removeLeadingZero(binding.editMobile.text.toString())
                else baseViewModel.dataSource.activationData.value?.mobile

            jsonObject.put(
                "BANKACCOUNTID", Objects
                    .requireNonNull(binding.editAccountNumber.text).toString()
            )
            jsonObject.put(
                "MOBILENUMBER", mobile
            )
            jsonObject.put("OTPKEY", binding.loadingFrame.verificationCodeEditText.text.toString())
            encrypted.put(
                "CARDNUMBER", BaseClass.newEncrypt(
                    Objects.requireNonNull(binding.editATM.text)
                        .toString().replace("-", "")
                )
            )
            encrypted.put(
                "CARDPIN", BaseClass
                    .newEncrypt(
                        Objects.requireNonNull(binding.editATMPin.text).toString()
                    )
            )
            composite.add(
                baseViewModel.validateCardOnTheGo(jsonObject, encrypted, requireContext())
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
                        "${OnGoPanFragment::class.java.simpleName}:E:ValidPin",
                        Gson().toJson(moduleData)
                    )
                    if (BaseClass.nonCaps(moduleData?.status) == StatusEnum.SUCCESS.type) {
                        setLoading(false)
                        baseViewModel.dataSource.setNKData(
                            NextKinData(account = binding.editAccountNumber.text.toString())
                        )

                        val mobile =
                            if (active == false) {
                                binding.countryCodeHolder.selectedCountryCode +
                                        removeLeadingZero(binding.editMobile.text.toString())
                            } else baseViewModel.dataSource.activationData.value?.mobile
                        val code = mobile?.substring(0, 3)
                        val phone = mobile?.substring(3)

                        AppLogger.instance.appLog("MOBILE", "$phone")
                        AppLogger.instance.appLog("CODE", "$code")



                        baseViewModel.dataSource.setAddressState(
                            AddressState(
                                phone = TwoDMap(key = code?.toInt(), value = phone)
                            )
                        )

                        AppLogger.instance.appLog(
                            "ADDRESS",
                            Gson().toJson(baseViewModel.dataSource.addressState.value)
                        )

                        SuccessDialogFragment.showDialog(
                            DialogData(
                                title = R.string.success,
                                subTitle = moduleData?.message!!,
                                R.drawable.success
                            ),
                            childFragmentManager, this
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
                                        validateCard()
                                    }
                                }
                            })
                    } else {
                        setLoading(false)
                        showError(moduleData?.message!!)
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

    override fun onDialog() {
        if (isOTP)
            setOtp(false)
        else dialog?.dismiss()
    }

    private fun createOTP() {

        if (validateFields()) {
            binding.loadingFrame.verificationCodeEditText.setText("")
            (requireActivity() as MainActivity).initSMSBroadCast()
            setLoading(true)
            val mobile =
                if (active == false) {
                    baseViewModel.mobile.value =
                        removeLeadingZero(binding.editMobile.text.toString())
                    binding.countryCodeHolder.selectedCountryCode +
                            removeLeadingZero(binding.editMobile.text.toString())
                } else baseViewModel.dataSource.activationData.value?.mobile
            val json = JSONObject()
            json.put("MOBILENUMBER", mobile)
            json.put("BANKACCOUNTID", binding.editAccountNumber.text.toString())
            json.put("SERVICENAME", "CENTEONTHEGO")

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
                                        "${OnGoPanFragment::class.java.simpleName}:E:PREPIN",
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

    private fun setOtp(b: Boolean) {
        isOTP = b
        if (b) {
            binding.toolbar.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back_24)
            binding.motionContainer.setTransition(
                R.id.loadingState, R.id.userState
            )
        } else {
            binding.toolbar.navigationIcon =
                ContextCompat.getDrawable(
                    requireContext(),
                    com.seedlotfi.towerinfodialog.R.drawable.ic_close_white_24dp
                )
            binding.motionContainer.setTransition(
                R.id.userState, R.id.loadingState
            )
        }
    }

    override fun onType(data: CustomKeyData?) {
        when (data?.type) {
            KeyFunctionEnum.Push -> {
                pinStack.push(data.str)
                baseViewModel.pin.value = pinStack
            }

            KeyFunctionEnum.Pop -> {
                if (pinStack.isNotEmpty()) {
                    pinStack.pop()
                    baseViewModel.pin.value = pinStack
                }

            }

            KeyFunctionEnum.Clear -> {
                if (pinStack.isNotEmpty()) {
                    pinStack.clear()
                    baseViewModel.pin.value = pinStack
                }
            }

            else -> {
                AppLogger.instance.appLog(
                    FalconHeavyActivity::class.java.simpleName,
                    "Nothing to do"
                )
            }
        }
    }

    private fun setKeyboard() {
        baseViewModel.pin.observe(viewLifecycleOwner) {
            val builder = StringBuilder()
            pinStack = it
            for (s in pinStack) {
                if (builder.length <= 4)
                    builder.append(s)
            }
            binding.editATMPin.setText(builder)
        }
        binding.editATMPin.setOnClickListener {
            CustomKeyboard.instanceExtra(
                childFragmentManager,
                this, 4
            )
        }
    }

}