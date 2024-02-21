package com.elmacentemobile.view.fragment.go.steps

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.room.TypeConverter
import com.elmacentemobile.R
import com.elmacentemobile.data.model.static_data.StaticDataDetails
import com.elmacentemobile.databinding.FragmentAdditionalInfoBinding
import com.elmacentemobile.util.OnAlertDialog
import com.elmacentemobile.util.ShowAlertDialog
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.ep.adapter.AutoTextArrayAdapter
import com.elmacentemobile.view.fragment.go.PagerData
import com.elmacentemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AdditionalInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class AdditionalInfoFragment : Fragment(), AppCallbacks, View.OnClickListener, OnAlertDialog,
    PagerData {

    private lateinit var binding: FragmentAdditionalInfoBinding
    private val widgetViewModel: WidgetViewModel by viewModels()

    private val hashMap = HashMap<String, TwoDMap>()
    private lateinit var stateData: AdditionalInfoData

    private lateinit var workAdapter: AutoTextArrayAdapter
    private lateinit var sourceFund: AutoTextArrayAdapter
    private lateinit var accountTurnover: AutoTextArrayAdapter
    private lateinit var citizenship: AutoTextArrayAdapter
    private lateinit var residence: AutoTextArrayAdapter
    private lateinit var openingDeposit: AutoTextArrayAdapter
    private lateinit var monthlyIncome: AutoTextArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun stopShimmer() {
        binding.mainLay.visibility = View.VISIBLE
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    override fun setState() {

    }
    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                ShowAlertDialog().showDialog(
                    requireContext(),
                    getString(R.string.exit_registration),
                    getString(R.string.proceed_registration),
                    this
                )
                true
            } else false
        }
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(95, true)
    }

    override fun setOnClick() {
        binding.buttonBack.setOnClickListener(this)
        binding.buttonNext.setOnClickListener(this)
    }

    override fun setBinding() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setStep()
            val p = widgetViewModel.storageDataSource.staticData.value
            setWork(p)
            setSourceFund(p)
            setAccountTurnover(p)
            setCitizenship(p)
            setResidence(p)
            setOpeningDeposit(p)
            setMonthlyIncome(p)
            setState()
        }, animationDuration.toLong())
    }

    private fun setMonthlyIncome(p: List<StaticDataDetails?>?) {
        if (p != null) {
            val pData = p.filter { a -> a?.id == "OCCUPATION" }
            val sorted = pData.sortedBy { a -> a?.description }
            monthlyIncome = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoMonth.setAdapter(monthlyIncome)
            binding.autoMonth.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["monthlyIncome"] = TwoDMap(
                        key = p2,
                        value = monthlyIncome.getItem(p2)?.subCodeID
                    )
                }
        }
    }

    private fun setOpeningDeposit(p: List<StaticDataDetails?>?) {
        if (p != null) {
            val pData = p.filter { a -> a?.id == "OCCUPATION" }
            val sorted = pData.sortedBy { a -> a?.description }
            openingDeposit = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoOpening.setAdapter(openingDeposit)
            binding.autoOpening.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["openingDeposit"] = TwoDMap(
                        key = p2,
                        value = openingDeposit.getItem(p2)?.subCodeID
                    )
                }
        }
    }

    private fun setResidence(p: List<StaticDataDetails?>?) {
        if (p != null) {
            val pData = p.filter { a -> a?.id == "OCCUPATION" }
            val sorted = pData.sortedBy { a -> a?.description }
            residence = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoResidence.setAdapter(residence)
            binding.autoResidence.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["residence"] = TwoDMap(
                        key = p2,
                        value = residence.getItem(p2)?.subCodeID
                    )
                }
        }
    }

    private fun setCitizenship(p: List<StaticDataDetails?>?) {
        if (p != null) {
            val pData = p.filter { a -> a?.id == "OCCUPATION" }
            val sorted = pData.sortedBy { a -> a?.description }
            citizenship = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoCitizen.setAdapter(citizenship)
            binding.autoCitizen.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["citizenship"] = TwoDMap(
                        key = p2,
                        value = citizenship.getItem(p2)?.subCodeID
                    )
                }
        }
    }

    private fun setAccountTurnover(p: List<StaticDataDetails?>?) {
        if (p != null) {
            val pData = p.filter { a -> a?.id == "TODO" }
            val sorted = pData.sortedBy { a -> a?.description }
            accountTurnover = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoTurnover.setAdapter(accountTurnover)
            binding.autoTurnover.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["accountTurnover"] = TwoDMap(
                        key = p2,
                        value = accountTurnover.getItem(p2)?.subCodeID
                    )
                }
        }
    }

    private fun setSourceFund(p: List<StaticDataDetails?>?) {
        if (p != null) {
            val pData = p.filter { a -> a?.id == "TODO" }
            val sorted = pData.sortedBy { a -> a?.description }
            sourceFund = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoIncome.setAdapter(sourceFund)
            binding.autoIncome.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["sourceFund"] = TwoDMap(
                        key = p2,
                        value = sourceFund.getItem(p2)?.subCodeID
                    )
                }
        }

    }

    private fun setWork(p: List<StaticDataDetails?>?) {
        if (p != null) {
            val pData = p.filter { a -> a?.id == "TODO" }
            val sorted = pData.sortedBy { a -> a?.description }
            workAdapter = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoWork.setAdapter(workAdapter)
            binding.autoWork.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["work"] = TwoDMap(
                        key = p2,
                        value = workAdapter.getItem(p2)?.subCodeID
                    )
                }
        }
    }

    override fun saveState() {
        statePersist()
        //widgetViewModel.storageDataSource.additionalData(stateData)
    }

    override fun statePersist() {
        stateData = AdditionalInfoData(
            work = hashMap["work"],
            sourceFund = hashMap["sourceFund"],
            accountTurnover = hashMap["accountTurnover"],
            citizenship = hashMap["citizenship"],
            residence = hashMap["residence"],
            openingDeposit = hashMap["openingDeposit"],
            monthlyIncome = hashMap["monthlyIncome"]
        )
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdditionalInfoBinding.inflate(inflater, container, false)
        setOnClick()
        setBinding()
        setToolbar()
        return binding.root
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.autoWork.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_work_location_))
            false
        } else if (TextUtils.isEmpty(binding.autoIncome.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_source_fund))
            false
        } else if (TextUtils.isEmpty(binding.autoTurnover.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_account_tunover_))
            false
        } else if (TextUtils.isEmpty(binding.autoCitizen.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_citizenship_))
            false
        } else if (TextUtils.isEmpty(binding.autoResidence.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_residence_))
            false
        } else if (TextUtils.isEmpty(binding.autoOpening.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_opening_account_))
            false
        } else if (TextUtils.isEmpty(binding.autoMonth.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_monthly_income_))
            false
        } else true
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



    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AdditionalInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdditionalInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = AdditionalInfoFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.buttonNext) {
            if (validateFields()) {
                saveState()
                pagerData?.onNext(13)
            }
        } else if (p == binding.buttonBack) pagerData?.onBack(11)
    }
}

@Parcelize
data class AdditionalInfoData(
    @field:SerializedName("work")
    @field:Expose
    val work: TwoDMap?,
    @field:SerializedName("sourceFund")
    @field:Expose
    val sourceFund: TwoDMap?,
    @field:SerializedName("accountTurnover")
    @field:Expose
    val accountTurnover: TwoDMap?,
    @field:SerializedName("citizenship")
    @field:Expose
    val citizenship: TwoDMap?,
    @field:SerializedName("residence")
    @field:Expose
    val residence: TwoDMap?,
    @field:SerializedName("openingDeposit")
    @field:Expose
    val openingDeposit: TwoDMap?,
    @field:SerializedName("monthlyIncome")
    @field:Expose
    val monthlyIncome: TwoDMap?,
) : Parcelable

class AdditionalInfoDataConverter {
    @TypeConverter
    fun convert(data: AdditionalInfoData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, AdditionalInfoData::class.java)
    }

    @TypeConverter
    fun convert(data: String?): AdditionalInfoData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, AdditionalInfoData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}