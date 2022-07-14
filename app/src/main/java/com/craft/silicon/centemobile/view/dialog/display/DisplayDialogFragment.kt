package com.craft.silicon.centemobile.view.dialog.display

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.dynamic.DynamicDataResponse
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.databinding.FragmentDisplayDialogBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.controller.DisplayData
import com.craft.silicon.centemobile.view.ep.controller.MainDisplayController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DisplayDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DisplayDialogFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDisplayDialogBinding

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
        binding = FragmentDisplayDialogBinding.inflate(inflater, container, false)
        setBinding()
        setController()
        return binding.root.rootView
    }

    override fun setBinding() {
        binding.toolbar.title = module?.moduleName
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
        val controller = MainDisplayController(this)
        controller.setData(data?.let { DisplayData(it) })
        binding.container.setController(controller)
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun startShimmer() {
        binding.shimmerContainer.startShimmer()
    }

    companion object {
        private var data: MutableList<HashMap<String, String>>? = null
        private var module: Modules? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DisplayDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DisplayDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun showDialog(
            manager: FragmentManager,
            data: MutableList<HashMap<String, String>>?,
            modules: Modules?
        ) =
            DisplayDialogFragment().apply {
                this@Companion.data = data
                this@Companion.module = modules
                show(manager, DialogFragment::class.java.simpleName)
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