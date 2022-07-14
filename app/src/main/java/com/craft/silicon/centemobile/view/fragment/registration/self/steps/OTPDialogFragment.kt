package com.craft.silicon.centemobile.view.fragment.registration.self.steps

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentDialogOtpBinding
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
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
class OTPDialogFragment : DialogFragment(), AppCallbacks, View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDialogOtpBinding

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
        return binding.root.rootView
    }

    override fun setOnClick() {
        binding.materialButton.setOnClickListener(this)
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.verificationCodeEditText.text)) {
            ShowToast(requireContext(), getString(R.string.otp_required), true)
            false
        } else {
            if (binding.verificationCodeEditText.text?.length!! < 6) {
                ShowToast(requireContext(), getString(R.string.otp_invalid), true)
                false
            } else true
        }
    }

    companion object {
        private var callback: OTP? = null

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
        fun showDialog(callbacks: OTP, manager: FragmentManager) = OTPDialogFragment().apply {
            this@Companion.callback = callbacks
            show(manager, FragmentManager::class.java.simpleName)
        }
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
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
                callback?.otp(binding.verificationCodeEditText.text.toString())
            }
        }
    }
}

interface OTP {
    fun otp(string: String) {}
}