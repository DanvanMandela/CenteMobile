package com.craft.silicon.centemobile.view.fragment.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentLandingPageBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.binding.setImageRes
import com.craft.silicon.centemobile.view.ep.data.GroupLanding
import com.craft.silicon.centemobile.view.ep.data.LandingData
import com.craft.silicon.centemobile.view.ep.data.LandingPageEnum
import com.craft.silicon.centemobile.view.ep.data.LandingPageItem
import com.craft.silicon.centemobile.view.model.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LandingPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LandingPageFragment : Fragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLandingPageBinding
    private val authViewModel: AuthViewModel by viewModels()

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
        binding = FragmentLandingPageBinding.inflate(inflater, container, false)
        setBinding()
        setAdvert()

        return binding.root.rootView
    }

    private fun setTimeOfDay() {
        val calendar: Calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR_OF_DAY)

        if (hours < 12) {
            binding.aspectRatioImageView2.setImageRes(R.drawable.noon_one)
            binding.textView7.text = getString(R.string.good_morining)
        } else if (hours in 12..14) {
            binding.aspectRatioImageView2.setImageRes(R.drawable.morning)
            binding.textView7.text = getString(R.string.good_afternoon)
        } else if (hours in 15..19) {
            binding.aspectRatioImageView2.setImageRes(R.drawable.noon)
            binding.textView7.text = getString(R.string.good_evening)
        } else if (hours in 19..24) {
            binding.aspectRatioImageView2.setImageRes(R.drawable.night)
            binding.textView7.text = getString(R.string.good_night)
        }

    }

    private fun setAdvert() {
        val avert = mutableListOf(
            R.drawable.advert, R.drawable.advert, R.drawable.advert
        )
        binding.carouselView.pageCount = avert.size
        binding.carouselView.setImageListener { position, imageView -> imageView?.setImageRes(avert[position]) }
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.data = GroupLanding(LandingData.landingData)
        binding.callback = this
    }

    override fun onStart() {
        super.onStart()
        setTimeOfDay()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LandingPageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LandingPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onLanding(data: LandingPageItem?) {
        when (data?.enum) {
            LandingPageEnum.LOGIN -> setNavigation(authViewModel.navigationDataSource.navigateAuth())
            LandingPageEnum.REGISTRATION -> setNavigation(authViewModel.navigationDataSource.navigateToLogin())
            LandingPageEnum.BRANCH -> setNavigation(authViewModel.navigationDataSource.navigateToLogin())
            else -> {
                throw Exception("No direction implemented")
            }
        }
    }

    private fun setNavigation(direction: NavDirections?) {
        (requireActivity() as MainActivity).provideNavigationGraph().navigate(direction!!)
    }
}