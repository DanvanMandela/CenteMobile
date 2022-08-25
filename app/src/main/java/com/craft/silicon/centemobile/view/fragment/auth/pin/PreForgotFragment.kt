package com.craft.silicon.centemobile.view.fragment.auth.pin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentPreForgotBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PreForgotFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class PreForgotFragment : Fragment(), AppCallbacks, PagerData {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentPreForgotBinding
    private var adapter: PreLoginPinFragmentAdapter? = null

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
        binding = FragmentPreForgotBinding.inflate(inflater, container, false)
        setBinding()
        return binding.root.rootView
    }

    override fun setBinding() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({

        }, animationDuration.toLong())
        setViewPager()
    }

    private fun setViewPager() {
        adapter =
            PreLoginPinFragmentAdapter(
                requireActivity().supportFragmentManager,
                lifecycle,
                this
            )
        binding.pager.adapter = adapter
        binding.pager.isUserInputEnabled = false
    }


    companion object {
        var otp: String? = null
        var mobile: String? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PreForgotFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PreForgotFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onNext(pos: Int) {
        moveViewPager(pos)
    }

    override fun onBack(pos: Int) {
        moveViewPager(pos)
    }

    private fun moveViewPager(pos: Int) {
        binding.pager.postDelayed(
            { binding.pager.setCurrentItem(pos, true) },
            100
        )
    }


    override fun otpPager(otpStr: String?, mobileStr: String?) {
        otp = otpStr
        mobile = mobileStr
    }

    override fun completeStep() {
        (requireActivity()).onBackPressed()
    }
}

class PreLoginPinFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val move: PagerData
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return TABS
    }

    override fun createFragment(position: Int): Fragment {
        return setFragment(position)
    }

    private fun setFragment(position: Int): Fragment {
        return when (position) {
            0 -> PrePinMobileFragment.onStep(move)
            1 -> PrePinPanFragment.onStep(move)//->REMOVED FROM FLOW
            else -> {
                throw Exception("Not implemented")
            }
        }
    }


    companion object {
        private const val TABS = 2
    }
}