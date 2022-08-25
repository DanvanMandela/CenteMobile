package com.craft.silicon.centemobile.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentNewAlertDialogBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "data"

/**
 * A simple [Fragment] subclass.
 * Use the [NewAlertDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class NewAlertDialogFragment : DialogFragment(), AppCallbacks {

    private var data: MainDialogData? = null

    private lateinit var binding: FragmentNewAlertDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable(ARG_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewAlertDialogBinding.inflate(inflater, container, false)
        setBinding()
        return binding.root.rootView
    }

    override fun setBinding() {
        binding.data = data
        binding.callback = this
    }

    override fun onDialog() {
        requireActivity().onBackPressed()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data: MainDialogData.
         * @return A new instance of fragment NewAlertDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: MainDialogData) =
            NewAlertDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, data)
                }
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
}