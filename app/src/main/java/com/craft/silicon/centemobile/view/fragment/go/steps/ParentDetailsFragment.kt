package com.craft.silicon.centemobile.view.fragment.go.steps

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.room.TypeConverter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentParentDetailsBinding
import com.craft.silicon.centemobile.util.OnAlertDialog
import com.craft.silicon.centemobile.util.ShowAlertDialog
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.ep.adapter.AutoTextArrayAdapter
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ParentDetailsFragment : Fragment(), AppCallbacks, View.OnClickListener, OnAlertDialog,
    PagerData {

    private lateinit var binding: FragmentParentDetailsBinding
    private val widgetViewModel: WidgetViewModel by viewModels()

    private lateinit var stateData: ParentDetails
    private lateinit var adapter: AutoTextArrayAdapter
    private var dropPosition: Int? = null

    private val hashMap = HashMap<String, TwoDMap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        activity?.onBackPressedDispatcher?.addCallback(this,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    ShowAlertDialog().showDialog(
//                        requireContext(),
//                        getString(R.string.exit_registration),
//                        getString(R.string.proceed_registration),
//                        this@ParentDetailsFragment
//                    )
//                }
//
//            }
//        )
    }

    companion object {
        private var pagerData: PagerData? = null

        @JvmStatic
        fun onStep(pagerData: PagerData) = ParentDetailsFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    private fun stopShimmer() {
        binding.mainLay.visibility = View.VISIBLE
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentDetailsBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        setToolbar()
        durationWatcher()
        return binding.root.rootView
    }

    private fun durationWatcher() {
        binding.durationGroup.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId != -1) {
                hashMap["Duration"] = TwoDMap(
                    key = i,
                    value = "${binding.homeDisInput.text.toString()}-" +
                            if (binding.radioMonth.isChecked) "Months"
                            else if (binding.radioYear.isChecked) "Years" else ""
                )
            }
        }

        binding.homeDisInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(e: Editable?) {
                if (e != null) {
                    if (e.isNotEmpty())
                        hashMap["Duration"] = TwoDMap(
                            key = binding.durationGroup.checkedRadioButtonId,
                            value = "${binding.durationInput.text.toString()}-" +
                                    if (binding.radioMonth.isChecked) "Months"
                                    else if (binding.radioYear.isChecked) "Years" else ""
                        )
                }
            }
        })
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

    override fun onNegative() {
        pagerData?.clearState()
        (requireActivity() as MainActivity).provideNavigationGraph()
            .navigate(widgetViewModel.navigation().navigateLanding())
    }

    override fun saveState() {
        statePersist()
        widgetViewModel.storageDataSource.setParentDetails(stateData)
    }

    override fun statePersist() {
        stateData = ParentDetails(
            fFName = binding.fFInput.text.toString(),
            fMName = if (TextUtils.isEmpty(binding.fMInput.text.toString())) ""
            else binding.fMInput.text.toString(),
            fLName = binding.fLInput.text.toString(),
            mFName = binding.mFInput.text.toString(),
            mMName = if (TextUtils.isEmpty(binding.mMInput.text.toString())) ""
            else binding.mMInput.text.toString(),
            mLName = binding.mLInput.text.toString(),
            duration = hashMap["Duration"],
            exposed = hashMap["Exposed"],
            autoPosition = dropPosition,
            homeDistrict = binding.homeDisInput.text.toString()
        )
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(40, true)
    }


    override fun setOnClick() {
        binding.buttonBack.setOnClickListener(this)
        binding.buttonNext.setOnClickListener(this)
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.fFInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.f_f_name_required), true)
            false

        } else if (TextUtils.isEmpty(binding.fLInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.f_l_name_required), true)
            false

        } else if (TextUtils.isEmpty(binding.mFInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.m_f_name_required), true)
            false

        } else if (TextUtils.isEmpty(binding.mLInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.m_l_name_required), true)
            false
        } else if (TextUtils.isEmpty(binding.homeDisInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.home_district_required), true)
            false
        } else if (TextUtils.isEmpty(binding.durationInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.home_duration), true)
            false
        } else if (binding.durationGroup.checkedRadioButtonId == -1) {
            ShowToast(requireContext(), getString(R.string.select_duration_of_stay), true)
            false
        } else if (TextUtils.isEmpty(binding.autoPolitically.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_p_e), true)
            false
        } else true
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setStep()
            setPoliticallyExposed()
            setState()
        }, animationDuration.toLong())
    }

    override fun setState() {
        val sData = widgetViewModel.storageDataSource.parentDetails.value
        if (sData != null) {
            binding.fFInput.setText(sData.fFName)
            binding.fMInput.setText(sData.fMName)
            binding.fLInput.setText(sData.fLName)
            binding.mFInput.setText(sData.mFName)
            binding.mMInput.setText(sData.mMName)
            binding.mLInput.setText(sData.mLName)
            binding.homeDisInput.setText(sData.homeDistrict)
            if (sData.duration != null) stateDuration(sData.duration)
            if (sData.exposed != null) stateExposed(sData.exposed)
        }
    }

    private fun stateExposed(exposed: TwoDMap) {
        val value = adapter.getItem(exposed.key!!)
        binding.autoPolitically.setText(value?.description, false)
        hashMap["Exposed"] = exposed
    }

    private fun stateDuration(district: TwoDMap) {
        val value = district.value?.split("-")
        binding.durationInput.setText(value!![0])
        binding.durationGroup.check(district.key!!)
        hashMap["Duration"] = district
    }


    private fun setPoliticallyExposed() {
        val p = widgetViewModel.storageDataSource.staticData.value

        if (p != null) {
            val pData = p.filter { a -> a?.id == "CSPEP" }.toMutableList()
            val sorted = pData.sortedBy { a -> a?.description }
            adapter = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoPolitically.setAdapter(adapter)
            binding.autoPolitically.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["Exposed"] = TwoDMap(
                        key = p2,
                        value = adapter.getItem(p2)?.subCodeID
                    )
                }
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.buttonBack) pagerData?.onBack(4)
        else if (p == binding.buttonNext) {
            if (validateFields()) {
                saveState()
                pagerData?.onNext(6)
            }
        }
    }
}

@Parcelize
data class ParentDetails(
    @field:SerializedName("fFName")
    @field:Expose
    val fFName: String?,
    @field:SerializedName("fMName")
    @field:Expose
    val fMName: String?,
    @field:SerializedName("fLName")
    @field:Expose
    val fLName: String?,
    @field:SerializedName("mFName")
    @field:Expose
    val mFName: String?,
    @field:SerializedName("mMName")
    @field:Expose
    val mMName: String?,
    @field:SerializedName("mLName")
    @field:Expose
    val mLName: String?,
    @field:SerializedName("duration")
    @field:Expose
    val duration: TwoDMap?,
    @field:SerializedName("exposed")
    @field:Expose
    val exposed: TwoDMap?,
    @field:SerializedName("home")
    @field:Expose
    val homeDistrict: String?,
    @field:SerializedName("autoPosition")
    @field:Expose
    val autoPosition: Int?
) : Parcelable

class ParentDetailsConverter {
    @TypeConverter
    fun from(data: ParentDetails?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, ParentDetails::class.java)
    }

    @TypeConverter
    fun to(data: String?): ParentDetails? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, ParentDetails::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}