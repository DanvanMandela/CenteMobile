package com.craft.silicon.centemobile.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentMiniStatementBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.adapter.MiniAdapterItem
import com.craft.silicon.centemobile.view.ep.data.MiniList
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
 * Use the [MiniStatementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class MiniStatementFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentMiniStatementBinding

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
        binding = FragmentMiniStatementBinding.inflate(inflater, container, false)
        setBinding()
        setController()
        return binding.root.rootView
    }

    override fun setBinding() {
        binding.toolbar.setNavigationOnClickListener { dialog?.dismiss() }
    }

    override fun setController() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setDisplayData()
        }, animationDuration.toLong())
    }

    private fun setDisplayData() {
        val adapter = MiniAdapterItem(data!!.miniList, this)
        binding.container.adapter = adapter
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    companion object {
        private var data: MiniList? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MiniStatementFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MiniStatementFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun setData(data: MiniList) = MiniStatementFragment().apply {
            this@Companion.data = data
        }
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
}