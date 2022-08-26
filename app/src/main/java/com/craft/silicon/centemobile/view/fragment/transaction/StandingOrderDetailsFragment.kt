package com.craft.silicon.centemobile.view.fragment.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.craft.silicon.centemobile.data.model.StandingOrder
import com.craft.silicon.centemobile.databinding.FragmentStandingOrderDetailsBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "data"


/**
 * A simple [Fragment] subclass.
 * Use the [StandingOrderDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class StandingOrderDetailsFragment : BottomSheetDialogFragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var data: StandingOrder? = null

    private lateinit var binding: FragmentStandingOrderDetailsBinding

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
        binding = FragmentStandingOrderDetailsBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        return binding.root.rootView
    }

    override fun setOnClick() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun setBinding() {
        if (data != null)
            binding.data = data
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data: StandingOrder.
         * @return A new instance of fragment StandingOrderDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: StandingOrder) =
            StandingOrderDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, data)
                }
            }
    }
}