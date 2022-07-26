package com.craft.silicon.centemobile.view.fragment.go.steps

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.room.TypeConverter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.ToolbarEnum
import com.craft.silicon.centemobile.databinding.*
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.OnAlertDialog
import com.craft.silicon.centemobile.util.ShowAlertDialog
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HearAboutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HearAboutFragment : Fragment(), AppCallbacks, View.OnClickListener, OnAlertDialog, PagerData {

    private lateinit var binding: FragmentHearAboutBinding
    private var hearAbout: HearAbout? = null
    private val param = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private val widgetViewModel: WidgetViewModel by viewModels()

    private lateinit var agentLayoutBinding: BlockAgentLayoutBinding
    private lateinit var customerLayoutBinding: BlockCurrentCustomerLayoutBinding
    private lateinit var staffLayoutBinding: BlockBankStaffLayoutBinding
    private lateinit var mediaLayoutBinding: BlockSocialMediaLayoutBinding
    private lateinit var tvRadioLayoutBinding: BlockTvRadioLayoutBinding


    private lateinit var stateData: HearAboutState


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        activity?.onBackPressedDispatcher?.addCallback(this,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    ShowAlertDialog().showDialog(
//                        requireContext(),
//                        getString(R.string.exit_registration),
//                        getString(R.string.proceed_registration),
//                        this@HearAboutFragment
//                    )
//                }
//
//            }
//        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHearAboutBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        setToolbar()
        setTitle()
        return binding.root.rootView
    }

    private fun stopShimmer() {
        binding.mainLay.visibility = View.VISIBLE
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
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

    override fun onPositive() {
        pagerData?.currentPosition()
        saveState()
        (requireActivity() as MainActivity).provideNavigationGraph()
            .navigate(widgetViewModel.navigation().navigateLanding())
    }

    override fun saveState() {
        statePersist()
        widgetViewModel.storageDataSource.setRecommendState(stateData)
    }

    override fun onNegative() {
        pagerData?.clearState()
        (requireActivity() as MainActivity).provideNavigationGraph()
            .navigate(widgetViewModel.navigation().navigateLanding())
    }

    override fun setBinding() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setViewControl()
            setStep()
            setState()
        }, animationDuration.toLong())

    }


    override fun statePersist() {
        when (hearAbout) {
            HearAbout.TV -> {
                stateData = HearAboutState(
                    data = TwoDMap(
                        key = binding.hearAbout.parent.checkedRadioButtonId,
                        value = TVRadioConverter().from(
                            TVRadio(tv = tvRadioLayoutBinding.tvInput.text.toString())
                        )
                    ).apply {
                        this.extra = "TV/Radio-" +
                                tvRadioLayoutBinding.tvInput.text.toString()
                    },
                    location = binding.currentInput.text.toString()
                )


            }
            HearAbout.SOCIAl -> {
                stateData = HearAboutState(
                    data = TwoDMap(
                        key = binding.hearAbout.parent.checkedRadioButtonId,
                        value = SocialMediaConverter().from(
                            SocialMedia(radio = mediaLayoutBinding.parent.checkedRadioButtonId)
                        )
                    ).apply {
                        this.extra = "Social Media-" +
                                when (mediaLayoutBinding.parent.checkedRadioButtonId) {
                                    R.id.radioFace -> getString(R.string.face_book)
                                    R.id.radioTwitter -> getString(R.string.twitter)
                                    R.id.radioInsta -> getString(R.string.insta)
                                    else -> ""
                                }
                    },
                    location = binding.currentInput.text.toString()
                )
            }
            HearAbout.BANK -> {
                stateData = HearAboutState(
                    data = TwoDMap(
                        key = binding.hearAbout.parent.checkedRadioButtonId,
                        value = BankStaffConverter().from(
                            BankStaff(
                                name = staffLayoutBinding.nameInput.text.toString(),
                                branch = staffLayoutBinding.branchInput.text.toString()
                            )
                        )
                    ).apply {
                        this.extra = "Bank Staff-" +
                                "${staffLayoutBinding.nameInput.text.toString()}-" +
                                staffLayoutBinding.branchInput.text.toString()
                    },
                    location = binding.currentInput.text.toString()
                )
            }
            HearAbout.CUSTOMER -> {
                stateData = HearAboutState(
                    data = TwoDMap(
                        key = binding.hearAbout.parent.checkedRadioButtonId,
                        value = CustomerConverter().from(
                            Customer(
                                name = customerLayoutBinding.nameInput.text.toString(),
                                mobile = customerLayoutBinding.editMobile.text.toString(),
                                code = customerLayoutBinding.countryCodeHolder
                                    .selectedCountryCode
                            )
                        )
                    ).apply {
                        this.extra = "Cente Customer" +
                                "${customerLayoutBinding.nameInput.text.toString()}-" +
                                customerLayoutBinding.countryCodeHolder
                                    .selectedCountryCode +
                                customerLayoutBinding.editMobile.text.toString()
                    },
                    location = binding.currentInput.text.toString()
                )
            }
            HearAbout.AGENT -> {
                stateData = HearAboutState(
                    data = TwoDMap(
                        key = binding.hearAbout.parent.checkedRadioButtonId,
                        value = AgentConverter().from(
                            Agent(
                                name = agentLayoutBinding.nameInput.text.toString()
                            )
                        )
                    ).apply {
                        this.extra = "Agent-" +
                                agentLayoutBinding.nameInput.text.toString()
                    },
                    location = binding.currentInput.text.toString()
                )
            }
            else -> {
                throw Exception("Not implemented")
            }
        }
    }

    override fun setState() {
        val data = widgetViewModel.storageDataSource.recommendState.value
        AppLogger.instance.appLog("STATE:DATA", Gson().toJson(data))
        try {
            if (data != null) {
                if (data.data != null) {
                    when (data.data.key) {
                        R.id.radioTv -> setRadioTvState(data)
                        R.id.radioSocialMedia -> setSocialState(data)
                        R.id.radioBank -> setBankState(data)
                        R.id.radioCustomer -> setCustomerState(data)
                        R.id.radioAgent -> setAgentState(data)
                    }
                    binding.currentInput.setText(data.location)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                    binding.collapsedLay.title = getString(R.string.how_you_hear)

                }
            } else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                if (state !== ToolbarEnum.COLLAPSED) {
                    val title = getString(R.string.how_you_hear)
                    title.replace("\n", " ")
                    state =
                        ToolbarEnum.COLLAPSED
                    binding.collapsedLay.title = title
                }
            }
        }
    }

    private fun setAgentState(data: HearAboutState) {
        binding.hearAbout.parent.check(data.data?.key!!)
        val agent = AgentConverter().to(data.data.value)
        agentLayoutBinding.nameInput.setText(agent?.name)
        stateData = data
    }

    private fun setCustomerState(data: HearAboutState) {
        binding.hearAbout.parent.check(data.data?.key!!)
        val customer = CustomerConverter().to(data.data.value)
        customerLayoutBinding.nameInput.setText(customer?.name)
        customerLayoutBinding.countryCodeHolder.setCountryForPhoneCode(customer?.code!!.toInt())
        customerLayoutBinding.editMobile.setText(customer.mobile)
        stateData = data
    }

    private fun setBankState(data: HearAboutState) {
        binding.hearAbout.parent.check(data.data?.key!!)
        val bank = BankStaffConverter().to(data.data.value)
        staffLayoutBinding.nameInput.setText(bank?.name)
        staffLayoutBinding.branchInput.setText(bank?.branch)
        stateData = data

    }

    private fun setSocialState(data: HearAboutState) {
        binding.hearAbout.parent.check(data.data?.key!!)
        val socialMedia = SocialMediaConverter().to(data.data.value)
        mediaLayoutBinding.parent.check(socialMedia?.radio!!)
        stateData = data
    }

    private fun setRadioTvState(data: HearAboutState) {
        binding.hearAbout.parent.check(data.data?.key!!)
        val radioTv = TVRadioConverter().to(data.data.value)
        tvRadioLayoutBinding.tvInput.setText(radioTv?.tv)
        stateData = data
    }

    private fun setViewControl() {
        param.setMargins(
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_24dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_16dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_24dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_0dp)
        )
        binding.hearAbout.parent.setOnCheckedChangeListener { _, p ->
            when (p) {
                R.id.radioTv -> setTvRadioLay()
                R.id.radioSocialMedia -> setSocialMedia()
                R.id.radioBank -> setBankStaff()
                R.id.radioCustomer -> setCustomer()
                R.id.radioAgent -> setAgent()
            }
        }
    }

    override fun validateFields(): Boolean {
        return if (binding.hearAbout.parent.checkedRadioButtonId == -1) {
            ShowToast(
                requireContext(), getString(R.string.medium_required),
                true
            )
            false
        } else if (TextUtils.isEmpty(binding.currentInput.text.toString())) {
            ShowToast(
                requireContext(), getString(R.string.current_location_required),
                true
            )
            false
        } else {
            when (hearAbout) {
                HearAbout.TV -> {
                    if (TextUtils.isEmpty(tvRadioLayoutBinding.tvInput.text.toString())) {
                        ShowToast(
                            requireContext(), getString(R.string.tv_radio_required),
                            true
                        )
                        false
                    } else true

                }
                HearAbout.SOCIAl -> {
                    if (mediaLayoutBinding.parent.checkedRadioButtonId == -1) {
                        ShowToast(
                            requireContext(), getString(R.string.social_media_required),
                            true
                        )
                        false
                    } else true


                }
                HearAbout.BANK -> {
                    if (TextUtils.isEmpty(staffLayoutBinding.nameInput.text.toString())) {
                        ShowToast(
                            requireContext(), getString(R.string.bank_staff_name_required),
                            true
                        )
                        false
                    } else if (TextUtils.isEmpty(staffLayoutBinding.branchInput.text.toString())) {
                        ShowToast(
                            requireContext(), getString(R.string.staff_branch_required),
                            true
                        )
                        false
                    } else true

                }
                HearAbout.CUSTOMER -> {
                    if (TextUtils.isEmpty(customerLayoutBinding.nameInput.text.toString())) {
                        ShowToast(
                            requireContext(), getString(R.string.customer_name_required),
                            true
                        )
                        false
                    } else if (TextUtils.isEmpty(
                            customerLayoutBinding
                                .editMobile.text.toString()
                        )
                    ) {
                        ShowToast(
                            requireContext(), getString(R.string.customer_mobile_required),
                            true
                        )
                        false
                    } else true

                }
                HearAbout.AGENT -> {
                    if (TextUtils.isEmpty(agentLayoutBinding.nameInput.text.toString())) {
                        ShowToast(
                            requireContext(), getString(R.string.agent_name_number_required),
                            true
                        )
                        false
                    } else true
                }

                else -> true
            }
        }

    }

    private fun setAgent() {
        hearAbout = HearAbout.AGENT
        binding.hearAbout.child.removeAllViews()
        agentLayoutBinding =
            BlockAgentLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        binding.hearAbout.child.addView(agentLayoutBinding.root)
    }

    private fun setCustomer() {
        hearAbout = HearAbout.CUSTOMER
        binding.hearAbout.child.removeAllViews()
        customerLayoutBinding =
            BlockCurrentCustomerLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        binding.hearAbout.child.addView(customerLayoutBinding.root)
    }

    private fun setBankStaff() {
        hearAbout = HearAbout.BANK
        binding.hearAbout.child.removeAllViews()
        staffLayoutBinding =
            BlockBankStaffLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        binding.hearAbout.child.addView(staffLayoutBinding.root)
    }

    private fun setSocialMedia() {
        hearAbout = HearAbout.SOCIAl
        binding.hearAbout.child.removeAllViews()
        mediaLayoutBinding =
            BlockSocialMediaLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        mediaLayoutBinding.mainLay.layoutParams = param
        binding.hearAbout.child.addView(mediaLayoutBinding.root)
    }

    private fun setTvRadioLay() {
        hearAbout = HearAbout.TV
        binding.hearAbout.child.removeAllViews()
        tvRadioLayoutBinding =
            BlockTvRadioLayoutBinding.inflate(LayoutInflater.from(requireContext()))
        tvRadioLayoutBinding.inputLay.layoutParams = param
        binding.hearAbout.child.addView(tvRadioLayoutBinding.root)

    }

    private fun setStep() {
        binding.progressIndicator.setProgress(90, true)
    }

    override fun setOnClick() {
        binding.buttonBack.setOnClickListener(this)
        binding.buttonNext.setOnClickListener(this)
    }

    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HearAboutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HearAboutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = HearAboutFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.buttonNext) {
            if (validateFields()) {
                saveState()
                pagerData?.onNext(11)
            }
        } else if (p == binding.buttonBack) pagerData?.onBack(9)
    }

}

enum class HearAbout {
    TV, SOCIAl, BANK, CUSTOMER, AGENT
}


@Parcelize
data class HearAboutState(
    @field:SerializedName("data")
    @field:Expose
    val data: TwoDMap?,
    @field:SerializedName("location")
    @field:Expose
    val location: String?

) : Parcelable

@Parcelize
data class SocialMedia(
    @field:SerializedName("radio")
    @field:Expose
    @IdRes val radio: Int
) : Parcelable

@Parcelize
data class TVRadio(
    @field:SerializedName("tv")
    @field:Expose
    val tv: String?
) : Parcelable


@Parcelize
data class BankStaff(
    @field:SerializedName("name")
    @field:Expose
    val name: String?,
    @field:SerializedName("branch")
    @field:Expose
    val branch: String?
) : Parcelable

@Parcelize
data class Customer(
    @field:SerializedName("name")
    @field:Expose
    val name: String?,
    @field:SerializedName("mobile")
    @field:Expose
    val mobile: String?,
    @field:SerializedName("code")
    @field:Expose
    val code: String?
) : Parcelable

@Parcelize
data class Agent(
    @field:SerializedName("name")
    @field:Expose
    val name: String?
) : Parcelable


class HearAboutStateConverter {
    @TypeConverter
    fun from(data: HearAboutState?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, HearAboutState::class.java)
    }

    @TypeConverter
    fun to(data: String?): HearAboutState? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, HearAboutState::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}

class CustomerConverter {
    @TypeConverter
    fun from(data: Customer?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, Customer::class.java)
    }

    @TypeConverter
    fun to(data: String?): Customer? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, Customer::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}

class BankStaffConverter {
    @TypeConverter
    fun from(data: BankStaff?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, BankStaff::class.java)
    }

    @TypeConverter
    fun to(data: String?): BankStaff? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, BankStaff::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}

class TVRadioConverter {
    @TypeConverter
    fun from(data: TVRadio?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, TVRadio::class.java)
    }

    @TypeConverter
    fun to(data: String?): TVRadio? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, TVRadio::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}

class SocialMediaConverter {
    @TypeConverter
    fun from(data: SocialMedia?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, SocialMedia::class.java)
    }

    @TypeConverter
    fun to(data: String?): SocialMedia? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, SocialMedia::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}

class AgentConverter {
    @TypeConverter
    fun from(data: Agent?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, Agent::class.java)
    }

    @TypeConverter
    fun to(data: String?): Agent? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, Agent::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}


