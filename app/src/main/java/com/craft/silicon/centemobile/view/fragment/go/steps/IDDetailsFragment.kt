package com.craft.silicon.centemobile.view.fragment.go.steps

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.ToolbarEnum
import com.craft.silicon.centemobile.databinding.FragmentIDDetailsBinding
import com.craft.silicon.centemobile.util.OnAlertDialog
import com.craft.silicon.centemobile.util.ShowAlertDialog
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IDDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class IDDetailsFragment : Fragment(), AppCallbacks, View.OnClickListener, OnAlertDialog, PagerData {

    private lateinit var binding: FragmentIDDetailsBinding
    private val widgetViewModel: WidgetViewModel by viewModels()
    private var stateData: IDDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        activity?.onBackPressedDispatcher?.addCallback(this,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    ShowAlertDialog().showDialog(
//                        requireContext(),
//                        getString(R.string.exit_registration),
//                        getString(R.string.proceed_registration),
//                        this@IDDetailsFragment
//                    )
//                }
//
//            }
//        )
    }

    override fun onPositive() {
        saveState()
        pagerData?.currentPosition()
        Handler(Looper.getMainLooper()).postDelayed({
            (requireActivity() as MainActivity).provideNavigationGraph()
                .navigate(widgetViewModel.navigation().navigateLanding())
        }, 100)
    }

    override fun onNegative() {
        pagerData?.clearState()
        (requireActivity() as MainActivity).provideNavigationGraph()
            .navigate(widgetViewModel.navigation().navigateLanding())
    }

    override fun saveState() {
        widgetViewModel.storageDataSource.setIDDetails(stateData)
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            ShowAlertDialog().showDialog(
                requireContext(),
                getString(R.string.exit_registration),
                getString(R.string.proceed_registration),
                this
            )
        }
    }

    override fun statePersist() {
        if (stateData != null) {
            val sData = stateData
            sData?.data = OCRData(
                names = binding.givenInput.text.toString(),
                surname = binding.surInput.text.toString(),
                idNo = binding.idInput.text.toString(),
                otherName = binding.otherInput.text.toString(),
                dob = binding.dobInput.text.toString()
            )
            stateData = sData

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIDDetailsBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        setToolbar()
        setTitle()
        return binding.root.rootView
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(30, true)
    }



    override fun setState() {
        val sData = widgetViewModel.storageDataSource.onIDDetails.asLiveData()
        sData.observe(viewLifecycleOwner) {
            if (it != null) {
                stateData = it
                val data = it.data
                if (data != null) {
                    binding.idInput.setText(data.idNo)
                    binding.dobInput.setText(data.dob)
                    binding.surInput.setText(data.surname)
                    binding.otherInput.setText(data.otherName)
                    binding.givenInput.setText(data.names)
                }
            }
        }
    }

    override fun setOnClick() {
        binding.buttonNext.setOnClickListener(this)
        binding.buttonBack.setOnClickListener(this)
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.mainLay.visibility = View.VISIBLE
            stopShimmer()
            setStep()
            setState()
        }, animationDuration.toLong())
    }

    private fun setTitle() {
        var state = ToolbarEnum.EXPANDED

        binding.collapsedLay.apply {
            setCollapsedTitleTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.poppins_medium
                )
            )
            setExpandedTitleTypeface(
                ResourcesCompat.getFont(
                    requireContext(),
                    R.font.poppins_bold
                )
            )
        }

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0) {
                if (state !== ToolbarEnum.EXPANDED) {
                    state =
                        ToolbarEnum.EXPANDED
                    binding.collapsedLay.title = getString(R.string.personal_info)

                }
            } else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                if (state !== ToolbarEnum.COLLAPSED) {
                    val title = getString(R.string.personal_info)
                    title.replace("\n", " ")
                    state =
                        ToolbarEnum.COLLAPSED
                    binding.collapsedLay.title = title
                }
            }
        }
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.givenInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.given_name_required), true)
            false
        } else if (TextUtils.isEmpty(binding.surInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.surname_required), true)
            false
        } else if (TextUtils.isEmpty(binding.idInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.id_required), true)
            false
        } else if (TextUtils.isEmpty(binding.dobInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.dob_required), true)
            false
        } else true
    }

    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IDDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IDDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = IDDetailsFragment().apply {
            this@Companion.pagerData = pagerData
        }

    }

    override fun onClick(p: View?) {
        if (p == binding.buttonBack) pagerData?.onBack(3)
        else if (p == binding.buttonNext) {
            if (validateFields()) {
                saveState()
                pagerData?.onNext(5)
            }
        }
    }
}