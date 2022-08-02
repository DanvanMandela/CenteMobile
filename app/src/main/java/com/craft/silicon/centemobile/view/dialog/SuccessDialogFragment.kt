package com.craft.silicon.centemobile.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentSuccessDialogBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.google.gson.Gson

private const val ARG_DATA = "data"

/**
 * A simple [Fragment] subclass.
 * Use the [SuccessDialogFragment.showDialog] factory method to
 * create an instance of this fragment.
 */

class SuccessDialogFragment : DialogFragment(), AppCallbacks {
    private lateinit var binding: FragmentSuccessDialogBinding
    private var dialogData: DialogData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            dialogData = arguments!!.getParcelable(ARG_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSuccessDialogBinding.inflate(inflater, container, false)
        setBinding()
        return binding.root.rootView
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.callback = this
        if (dialogData != null) {
            binding.data = dialogData
            Log.e("TAg", Gson().toJson(dialogData))
        }
    }

    companion object {
        private var callBacks: AppCallbacks? = null


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data NavigationData.
         * @return A new instance of fragment SuccessDialogFragment.
         */
        @JvmStatic
        fun showDialog(
            data: DialogData,
            manager: FragmentManager,
            callback: AppCallbacks
        ) =
            SuccessDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, data)
                }
                this@Companion.callBacks = callback
                show(manager, SuccessDialogFragment::class.simpleName)
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
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



    override fun onDialog() {
        dialog?.dismiss()
        callBacks?.navigateUp()
    }
}