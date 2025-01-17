package com.craft.silicon.centemobile.view.dialog.receipt

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentReceiptBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.callbacks.Confirm
import com.craft.silicon.centemobile.view.ep.controller.ReceiptFormController
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.ReceiptList

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_DATA = "receiptData"

/**
 * A simple [Fragment] subclass.
 * Use the [ReceiptFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ReceiptFragment : BottomSheetDialogFragment(), AppCallbacks {

    private lateinit var binding: FragmentReceiptBinding
    private var controller = ReceiptFormController()
    private var receipts: DynamicData? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            receipts = it.getParcelable(ARG_DATA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReceiptBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        return binding.root.rootView
    }

    override fun setOnClick() {
        binding.confirm.setOnClickListener {
            dialog?.dismiss()
            confirm?.onCancel()
        }
        binding.shareButton.setOnClickListener {
            shareData()
        }
    }

    private fun shareData() {
        if (data?.notification != null) {
            val share = data?.notification
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    share?.find {
                        BaseClass.nonCaps(it.notifyType) ==
                                BaseClass.nonCaps("APP")
                    }?.notifyText
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        } else ShowToast(requireContext(), getString(R.string.nothing_to_share), true)


    }

    override fun setBinding() {
        if (data != null)
            controller.setData(data)
        binding.container.setController(controller)
    }

    companion object {
        private var confirm: Confirm? = null
        private var data: ReceiptList? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param confirm Confirm 1.
         * @return A new instance of fragment ReceiptFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(confirm: Confirm, data: ReceiptList) =
            ReceiptFragment().apply {
                this@Companion.confirm = confirm
                this@Companion.data = data

            }

        @JvmStatic
        fun show(confirm: Confirm, data: ReceiptList, manager: FragmentManager) =
            ReceiptFragment().apply {
                this@Companion.confirm = confirm
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