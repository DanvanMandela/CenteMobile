package com.craft.silicon.centemobile.view.fragment.go.steps

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentProductConfirmationBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.callbacks.Confirm
import com.google.gson.annotations.SerializedName

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductConfirmationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductConfirmationFragment : DialogFragment(), AppCallbacks, Confirm {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentProductConfirmationBinding

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
        binding = FragmentProductConfirmationBinding.inflate(inflater, container, false)
        setBinding()
        return binding.root.rootView
    }

    override fun setBinding() {
        binding.callback = this
        binding.data = data
    }

    override fun onConfirm() {
        dialog?.dismiss()
        confirm?.onConfirm()
    }

    companion object {
        private lateinit var data: ProductConfirm
        private var confirm: Confirm? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductConfirmationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductConfirmationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun showDialog(manager: FragmentManager, data: ProductConfirm, callbacks: Confirm) =
            ProductConfirmationFragment().apply {
                this@Companion.data = data
                show(manager, this.tag)
                this@Companion.confirm = callbacks
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

data class ProductConfirm(
    @field:SerializedName("account")
    val account: String?,
    @field:SerializedName("branch")
    val branch: String?,
    @field:SerializedName("currency")
    val currency: String?
)
