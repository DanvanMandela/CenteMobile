package com.craft.silicon.centemobile.view.fragment.go

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentOnTheGoBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.fragment.go.steps.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnTheGoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class OnTheGoFragment : Fragment(), AppCallbacks, MovePager {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentOnTheGoBinding
    private var adapter: ONTHGFragmentAdapter? = null

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
        binding = FragmentOnTheGoBinding.inflate(inflater, container, false)
        setBinding()
        return binding.root.rootView
    }

    override fun setBinding() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setViewPager()
        }, animationDuration.toLong())
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun startShimmer() {
        binding.shimmerContainer.visibility = View.VISIBLE
        binding.shimmerContainer.startShimmer()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OnTheGoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OnTheGoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setViewPager() {
        adapter =
            ONTHGFragmentAdapter(requireActivity().supportFragmentManager, lifecycle, this)
        binding.pager.adapter = adapter
        binding.pager.isUserInputEnabled = false
        binding.pager.setPageTransformer { page, position ->

//            val slide: Animation =
//                AnimationUtils.loadAnimation(requireContext(), R.anim.anim_slide_out_right)
//            page.animation = slide
        }
    }


    override fun onNext(pos: Int) {
        moveViewPager(pos)
    }

    override fun onBack(pos: Int) {
        moveViewPager(pos)
    }

    override fun customerProduct(data: CustomerProduct) {

    }

    override fun idDetails(data: IDDetails) {

    }

    private fun moveViewPager(pos: Int) {
        binding.pager.postDelayed(
            { binding.pager.setCurrentItem(pos, true) },
            100
        )
    }


}

class ONTHGFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val move: MovePager
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return OnTheLandingFragment.onStep(move)
            1 -> return CustomerProductFragment.onStep(move)
            2 -> return TermsAndConditionFragment.newInstance(move)
            3 -> return IDFragment.onStep(move)
        }
        return OnTheLandingFragment()
    }

    companion object {
        private const val TABS = 4
    }
}

interface MovePager {
    fun onNext(pos: Int)
    fun onBack(pos: Int)

    fun customerProduct(data: CustomerProduct) {
        throw Exception("Not implemented")
    }

    fun idDetails(data: IDDetails) {
        throw Exception("Not implemented")
    }
}


