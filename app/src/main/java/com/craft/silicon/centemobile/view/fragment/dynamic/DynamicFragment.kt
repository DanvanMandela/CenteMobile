package com.craft.silicon.centemobile.view.fragment.dynamic

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.databinding.FragmentDynamicBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val DATA_TAG = "data"


/**
 * A simple [Fragment] subclass.
 * Use the [DynamicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DynamicFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()

    private var dynamicData: DynamicData? = null


    private lateinit var binding: FragmentDynamicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dynamicData = it.getParcelable(DATA_TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDynamicBinding.inflate(inflater, container, false)
        setBinding()
        return binding.root.rootView
    }


    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = dynamicData
        binding.callback = this
        getChildren()
    }

    private fun getChildren() {

    }

    override fun onModule(modules: Modules?) {
        onAppCallbacks?.onModule(modules)
    }


    companion object {
        private val TAG = DynamicFragment::class.simpleName
        private var onAppCallbacks: AppCallbacks? = null


        @JvmStatic
        fun newInstance() = DynamicFragment()

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data DynamiCData.
         * @param manager: FragmentManager.
         * @return A new instance of fragment DynamicFragment.
         */
        @JvmStatic
        fun showDialog(manager: FragmentManager, data: DynamicData, onAppCallbacks: AppCallbacks) =
            DynamicFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(DATA_TAG, data)
                }
                this@Companion.onAppCallbacks = onAppCallbacks
                show(manager, TAG)
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
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onChildren(linearLayout: LinearLayout?) {

        for (c in 1..linearLayout!!.childCount) {

        }
    }


    override fun navigateUp() {
        dialog?.dismiss()
    }
}