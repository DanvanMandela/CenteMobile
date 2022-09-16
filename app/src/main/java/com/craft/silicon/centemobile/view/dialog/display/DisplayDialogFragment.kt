package com.craft.silicon.centemobile.view.dialog.display

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.databinding.FragmentDisplayDialogBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.controller.DisplayData
import com.craft.silicon.centemobile.view.ep.controller.MainDisplayController
import com.craft.silicon.centemobile.view.ep.data.Nothing
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "data"

/**
 * A simple [Fragment] subclass.
 * Use the [DisplayDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DisplayDialogFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private lateinit var binding: FragmentDisplayDialogBinding
    private var displayData: DisplayData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            displayData = it.getParcelable(ARG_DATA)
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
        binding.toolbar.setNavigationOnClickListener { dialog?.dismiss() }
        if (displayData != null) {
            binding.toolbar.title = displayData?.modules?.moduleName
        }
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
        binding.container.setController(controller)
        if (data != null)
            if (data!!.isNotEmpty()) {
                controller.setData(DisplayData(data, form, module))
            } else controller.setData(Nothing())
        else controller.setData(Nothing())

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
        private var form: FormControl? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param displayData: DisplayData.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DisplayDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(displayData: DisplayData) =
            DisplayDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, displayData)
                }
            }

        @JvmStatic
        fun showDialog(
            manager: FragmentManager,
            data: MutableList<HashMap<String, String>>?,
            modules: Modules?,
            controller: FormControl?
        ) =
            DisplayDialogFragment().apply {
                this@Companion.data = data
                this@Companion.module = modules
                this@Companion.form = controller
                show(manager, DialogFragment::class.java.simpleName)
            }

        @JvmStatic
        fun setData(
            data: MutableList<HashMap<String, String>>?,
        ) = DisplayDialogFragment().apply {
            this@Companion.data = data
        }

        @JvmStatic
        fun show(
            data: MutableList<HashMap<String, String>>?, manager: FragmentManager
        ) = DisplayDialogFragment().apply {
            this@Companion.data = data
            show(manager, this.tag)
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