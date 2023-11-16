package com.elmacentemobile.view.fragment.registration.self.steps

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.lifecycle.asLiveData
import com.elmacentemobile.R
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.databinding.FragmentDialogOtpBinding
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.fragment.go.steps.OTPCountDownTimer
import com.elmacentemobile.view.model.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OTPDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class OTPDialogFragment : BottomSheetDialogFragment(), AppCallbacks,
    com.elmacentemobile.view.fragment.go.steps.OTP, View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDialogOtpBinding
    private val baseViewModel: BaseViewModel by viewModels()

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
        binding = FragmentDialogOtpBinding.inflate(inflater, container, false)
        setOnClick()
        setToolbar()
        setOtp()
        setTimer()
        return binding.root.rootView
    }

    private fun setOtp() {
        disableOpt()
        binding.userFrame.resendButton.visibility = View.GONE
        (requireActivity() as MainActivity).initSMSBroadCast()
        val otp = baseViewModel.dataSource.otp.asLiveData()
        otp.observe(viewLifecycleOwner) {
            binding.userFrame.verificationCodeEditText.setText(it)
        }
    }

    private fun disableOpt() {
        if (!mobileNumber.isNullOrBlank()) {
            val code: String = mobileNumber!!.substring(0, 3)
            if (code.contains("256") && !Constants.Data.TEST) {
                if (!Constants.Data.AUTO_OTP)
                    binding.userFrame
                        .verificationCodeEditText.isEnabled = false
            }
        }
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun setTimer() {
        binding.userFrame.resendLay.visibility = View.VISIBLE
        countDownTimer = OTPCountDownTimer(startTime = startTime, interval = interval, this)
        timerControl(true)
        done(false)
    }


    override fun timer(str: String) {
        binding.userFrame.otpTimer.text = str
    }

    private fun timerControl(startTimer: Boolean) {
        if (startTimer) {
            countDownTimer!!.start()
        } else {
            countDownTimer!!.cancel()
        }
    }

    override fun done(boolean: Boolean) {
        if (boolean) {
            timerControl(false)
            binding.userFrame.resendButton.visibility = View.VISIBLE
        } else binding.userFrame.resendButton.visibility = View.GONE
    }

    override fun setOnClick() {
        binding.materialButton.setOnClickListener(this)
        binding.userFrame.resendButton.setOnClickListener {
            dialog?.dismiss()
            callback?.resendOTP()
        }
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.userFrame.verificationCodeEditText.text)) {
            ShowToast(requireContext(), getString(R.string.otp_required), true)
            false
        } else {
            if (binding.userFrame.verificationCodeEditText.text?.length!! < 6) {
                ShowToast(requireContext(), getString(R.string.otp_invalid), true)
                false
            } else true
        }
    }


    companion object {
        private var callback: OTP? = null
        private var mobileNumber: String? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OTPDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OTPDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun showDialog(
            callbacks: OTP,
            manager: FragmentManager,
            mobile: String?
        ) = OTPDialogFragment().apply {
            this@Companion.callback = callbacks
            this@Companion.mobileNumber = mobile
            show(manager, FragmentManager::class.java.simpleName)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.attributes.windowAnimations = R.style.MyDialogAnimation
    }

    override fun onClick(view: View?) {
        if (view == binding.materialButton) {
            if (validateFields()) {
                dialog?.dismiss()
                callback?.otp(binding.userFrame.verificationCodeEditText.text.toString())
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

interface OTP {
    fun otp(string: String) {}
    fun resendOTP() {}
}