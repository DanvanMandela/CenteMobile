package com.elmacentemobile.view.fragment.global

import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.elmacentemobile.R
import com.elmacentemobile.data.model.action.ActionControls
import com.elmacentemobile.data.model.control.ControlTypeEnum
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.databinding.BlockDisplayItemLayoutBinding
import com.elmacentemobile.databinding.FragmentGlobalOTPBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.BaseClass
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.util.callbacks.Confirm
import com.elmacentemobile.view.ep.data.DisplayContent
import com.elmacentemobile.view.fragment.go.steps.OTP
import com.elmacentemobile.view.fragment.go.steps.OTPCountDownTimer
import com.elmacentemobile.view.model.WidgetViewModel
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
 * Use the [GlobalOTPFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class GlobalOTPFragment : BottomSheetDialogFragment(), AppCallbacks, OTP {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentGlobalOTPBinding

    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()

    private val startTime = (120 * 1000).toLong()
    private val interval = (1 * 1000).toLong()

    private var countDownTimer: CountDownTimer? = null

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
        binding = FragmentGlobalOTPBinding.inflate(inflater, container, false)
        setViewModel()
        setOnClick()
        setOtpListener()
        setTimer()
        return binding.root.rootView
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


    private fun setTimer() {
        binding.resendLay.visibility = View.VISIBLE
        countDownTimer = OTPCountDownTimer(startTime = startTime, interval = interval, this)
        timerControl(true)
        done(false)
    }

    private fun timerControl(startTimer: Boolean) {
        if (startTimer) {
            countDownTimer!!.start()
        } else {
            countDownTimer!!.cancel()
        }
    }


    override fun userInput(inputData: InputData?) {
        data.removeIf { a -> a.key == inputData?.key }
        data.add(inputData!!)
    }

    private fun setOtpListener() {
        val otp = widgetViewModel.storageDataSource.otp.asLiveData()
        otp.observe(viewLifecycleOwner) {
            if (it!!.isNotEmpty())
                binding.otp.setText(it)
        }
    }


    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.otp.text.toString())) {
            ShowToast(requireContext(), getString(R.string.otp_required))
            false
        } else {
            if (binding.otp.text.toString().length < 6) {
                ShowToast(requireContext(), getString(R.string.otp_invalid))
                false
            } else {
                encrypted?.put("TrxOTP", BaseClass.newEncrypt(binding.otp.text.toString()))
                true
            }
        }
    }

    override fun setOnClick() {


        AppLogger.instance.appLog("DATA", Gson().toJson(data))

        binding.confirm.setOnClickListener {
            if (validateFields()) {
                dialog?.dismiss()

                confirm?.onPay(
                    json = json,
                    inputList = data,
                    action = action,
                    module = module,
                    encrypted = encrypted,
                    formControl = form
                )
            }
        }
        binding.cancel.setOnClickListener { dialog?.dismiss() }
        binding.resendButton.setOnClickListener {
            dialog?.dismiss()
            confirm?.onPay(
                json = json,
                inputList = data,
                action = action,
                module = module,
                encrypted = encrypted,
                formControl = form
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
    }

    override fun setViewModel() {
        subscribe.add(
            widgetViewModel.getFormControlNoSq(ControlTypeEnum.CONFORM.type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNotEmpty()) {
                        setLayout(it)
                    }
                }, { it.printStackTrace() })
        )
    }

    private fun setLayout(it: List<FormControl>) {
        it.toMutableList()
        it.reversed()
        for (t in it) {
            for (e in data) {
                if (t.controlID == e.key) {
                    val display = BlockDisplayItemLayoutBinding.inflate(layoutInflater)
                    val m = DisplayContent(key = t.controlText!!, value = e.value!!)
                    display.data = m
                    binding.container.addView(display.root)
                }
            }
        }
    }

    companion object {
        private val TAG = GlobalOTPFragment::class.simpleName
        private var data = mutableListOf<InputData>()
        private var confirm: Confirm? = null
        private var json: JSONObject? = null
        private var encrypted: JSONObject? = null
        private var module: Modules? = null
        private var action: ActionControls? = null
        private var form: FormControl? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GlobalOTPFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GlobalOTPFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun setData(
            json: JSONObject?,
            encrypted: JSONObject?,
            inputList: MutableList<InputData>,
            module: Modules?,
            action: ActionControls?,
            formControl: FormControl?,
            confirm: Confirm
        ) =
            GlobalOTPFragment().apply {
                this@Companion.data = inputList
                this@Companion.confirm = confirm
                this@Companion.encrypted = encrypted
                this@Companion.json = json
                this@Companion.module = module
                this@Companion.action = action
                this@Companion.form = formControl
                //show(manager, TAG)
            }

        @JvmStatic
        fun show(
            json: JSONObject?,
            encrypted: JSONObject?,
            inputList: MutableList<InputData>,
            module: Modules?,
            action: ActionControls?,
            formControl: FormControl?,
            confirm: Confirm,
            manager: FragmentManager
        ) =
            GlobalOTPFragment().apply {
                this@Companion.data = inputList
                this@Companion.confirm = confirm
                this@Companion.encrypted = encrypted
                this@Companion.json = json
                this@Companion.module = module
                this@Companion.action = action
                this@Companion.form = formControl
                show(manager, TAG)
            }
    }

    override fun getTheme(): Int {
        return R.style.ThemeOverlay_Demo_BottomSheetDialog
    }
}