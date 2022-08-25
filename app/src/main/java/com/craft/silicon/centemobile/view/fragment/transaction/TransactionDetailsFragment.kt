package com.craft.silicon.centemobile.view.fragment.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.dynamic.TransactionData
import com.craft.silicon.centemobile.databinding.FragmentTransactionDetailsBinding
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "transaction"


/**
 * A simple [Fragment] subclass.
 * Use the [TransactionDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class TransactionDetailsFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var data: TransactionData? = null
    private lateinit var binding: FragmentTransactionDetailsBinding

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
        binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
        setBinding()
        setToolbar()
        return binding.root.rootView
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.shareAction -> {
                        shareTransaction()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun shareTransaction() {
        BaseClass.shareBitmap(
            requireActivity(),
            BaseClass.takeScreenshot(
                binding.receipt
            )
        )
    }

    override fun setBinding() {
        binding.data = data

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param transactionData: TransactionData
         * @return A new instance of fragment TransactionDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(transactionData: TransactionData?) =
            TransactionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, transactionData)
                }
            }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        dialog!!.window!!.attributes.windowAnimations = R.style.MyDialogAnimation
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if (dialog != null && dialog!!.window != null) {
//            dialog!!.window!!.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//        }
//
//    }


}

