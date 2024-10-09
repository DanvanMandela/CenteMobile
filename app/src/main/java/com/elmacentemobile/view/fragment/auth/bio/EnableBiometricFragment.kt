package com.elmacentemobile.view.fragment.auth.bio

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.elmacentemobile.R
import com.elmacentemobile.data.model.control.PasswordEnum
import com.elmacentemobile.databinding.FragmentEnableBiometricBinding
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.model.WidgetViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EnableBiometricFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class EnableBiometricFragment : DialogFragment(), AppCallbacks, View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentEnableBiometricBinding
    private val widgetViewModel: WidgetViewModel by viewModels()

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
        binding = FragmentEnableBiometricBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        setPinType()
        return binding.root.rootView
    }

    private fun setPinType() {
        val pinType = widgetViewModel.storageDataSource.passwordType.value
        if (!pinType.isNullOrBlank()) {
            when (pinType) {
                PasswordEnum.TEXT_PASSWORD.type -> {
                    binding.editPin.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
                PasswordEnum.NUMERIC_PASSWORD.type -> {
                    binding.editPin.inputType =
                        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
                }
                PasswordEnum.WEB_PASSWORD.type -> {
                    binding.editPin.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
                }
            }
        }
    }

    override fun setBinding() {
        val state = widgetViewModel.storageDataSource.bio.value
        if (state != null) {
            if (state) {
                binding.materialButton.text = getString(R.string.disable_)
                binding.title.text = getText(R.string.enter_pin_disable_touch)
            } else {
                binding.materialButton.text = getString(R.string.enable_)
                binding.title.text = getText(R.string.enter_pin_enable_touch)
            }
        }
    }


    override fun setOnClick() {
        binding.materialButton.setOnClickListener(this)
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.editPin.text.toString())) {
            ShowToast(requireContext(), getString(R.string.pin_required), true)
            false
        } else true

    }

    companion object {
        private var bio: BioInterface? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EnableBiometricFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EnableBiometricFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun showDialog(
            bio: BioInterface,
            manager: FragmentManager,
        ) =
            EnableBiometricFragment().apply {
                this@Companion.bio = bio
                show(manager, this.tag)
            }
    }

    override fun onClick(p: View?) {
        if (p == binding.materialButton) {
            if (validateFields()) setBiometric()
        }
    }

    private fun setBiometric() {
        bio?.onPin(binding.editPin.text.toString())
        dialog?.dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.attributes.windowAnimations = R.style.MyDialogAnimation
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

}

