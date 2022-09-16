package com.craft.silicon.centemobile.view.fragment.transaction

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
import com.craft.silicon.centemobile.databinding.FragmentRejectPendingBinding
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RejectPendingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class RejectPendingFragment : DialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentRejectPendingBinding

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
        binding = FragmentRejectPendingBinding.inflate(inflater, container, false)
        setBinding()
        return binding.root.rootView
    }

    override fun setBinding() {
        binding.accept.setOnClickListener {
            if (validateFields()) {
                reject.onReject(binding.child.text.toString())
                dialog?.dismiss()
            }
        }
        binding.cancel.setOnClickListener { dialog?.dismiss() }
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.child.text.toString())) {
            ShowToast(requireContext(), getString(R.string.message_required))
            false
        } else {
            if (binding.child.text.toString().length < 3) {
                ShowToast(requireContext(), getString(R.string.invalid_message))
                false
            } else true
        }

    }

    companion object {
        private lateinit var reject: RejectTransaction

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RejectPendingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RejectPendingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun setData(reject: RejectTransaction) =
            RejectPendingFragment().apply {
                this@Companion.reject = reject
            }

        @JvmStatic
        fun show(reject: RejectTransaction, manager: FragmentManager) =
            RejectPendingFragment().apply {
                this@Companion.reject = reject
                show(manager, this.tag)
            }

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

interface RejectTransaction {
    fun onReject(message: String) {
        throw Exception("not implemented")
    }
}