package com.craft.silicon.centemobile.view.fragment.go.steps


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.room.TypeConverter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.databinding.FragmentIncomeSourceBinding
import com.craft.silicon.centemobile.util.NumberTextWatcherForThousand.getDecimalFormattedString
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
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IncomeSourceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class IncomeSourceFragment : Fragment(), AppCallbacks, View.OnClickListener, OnAlertDialog,
    PagerData {

    private lateinit var binding: FragmentIncomeSourceBinding
    private val widgetViewModel: WidgetViewModel by viewModels()


    private val hashMap = HashMap<String, TwoDMap>()
    private lateinit var stateData: IncomeData

    private lateinit var occupationAdapter: AutoTextArrayAdapter
    private lateinit var professionAdapter: AutoTextArrayAdapter


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
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIncomeSourceBinding.inflate(inflater, container, false)
        setOnClick()
        setBinding()
        setToolbar()
        durationWatcher()
        setWatcher()
        return binding.root.rootView
    }

    private fun setWatcher() {
        val decimalSeparator = '.'
        val thousandSeparator = ","
        binding.incomeInput.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
                binding.incomeInput.removeTextChangedListener(this)

                try {
                    var givenstring: String = p0.toString()
                    if (givenstring.contains(thousandSeparator)) {
                        givenstring = givenstring.replace(thousandSeparator.toString(), "")
                    }
                    val doubleVal: Double = givenstring.toDouble()


                    val unusualSymbols = DecimalFormatSymbols()
                    unusualSymbols.decimalSeparator = decimalSeparator
                    unusualSymbols.groupingSeparator = thousandSeparator.single()

                    val formatter = DecimalFormat("#,##0.##", unusualSymbols)
                    formatter.groupingSize = 3
                    val formattedString = formatter.format(doubleVal)

                    binding.incomeInput.setText(formattedString)
                    binding.incomeInput.setSelection(binding.incomeInput.text!!.length)
                    // to place the cursor at the end of text
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.incomeInput.addTextChangedListener(this)

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no need any callback for this.
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // no need any callback for this.
            }

        })
    }


    private fun durationWatcher() {
        binding.durationGroup.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.checkedRadioButtonId != -1) {
                hashMap["Duration"] = TwoDMap(
                    key = i,
                    value = "${binding.durationInput.text.toString()}-" +
                            if (binding.radioMonth.isChecked) "Months"
                            else if (binding.radioYear.isChecked) "Years" else ""
                )
            }
        }

        binding.durationInput.addTextChangedListener(object : TextWatcher {
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
        widgetViewModel.storageDataSource.setIncomeSource(stateData)
    }

    override fun statePersist() {

        stateData = IncomeData(
            occupation = hashMap["Occupation"],
            profession = hashMap["Profession"],
            income = if (!TextUtils.isEmpty(binding.incomeInput.text.toString()))
                getDecimalFormattedString(binding.incomeInput.text.toString()) else "",
            workPlace = binding.workPlaceInput.text.toString(),
            natureBusiness = binding.natureInput.text.toString(),
            duration = hashMap["Duration"],
            employer = binding.employerNameInput.text.toString(),
            natureEmployment = binding.natureEInput.text.toString()
        )
    }

    override fun setBinding() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setStep()
            val p = widgetViewModel.storageDataSource.staticData.value
            setOccupation(p)
            setProfession(p)
            setState()
        }, animationDuration.toLong())
    }

    override fun setState() {
        val sData = widgetViewModel.storageDataSource.incomeSource.value
        if (sData != null) {
            if (sData.occupation != null) stateOccupation(sData.occupation)
            if (sData.profession != null) stateProfession(sData.profession)
            binding.incomeInput.setText(sData.income)
            binding.workPlaceInput.setText(sData.workPlace)
            binding.natureInput.setText(sData.natureBusiness)
            binding.employerNameInput.setText(sData.employer)
            binding.natureEInput.setText(sData.natureEmployment)
            if (sData.duration != null) stateDuration(sData.duration)
        }
    }

    private fun stateDuration(duration: TwoDMap) {
        val value = duration.value?.split("-")
        binding.durationInput.setText(value!![0])
        binding.durationGroup.check(duration.key!!)
    }

    private fun stateProfession(profession: TwoDMap) {
        val value = professionAdapter.getItem(profession.key!!)
        binding.autoProf.setText(value?.description, false)
        hashMap["Profession"] = profession
    }

    private fun stateOccupation(occupation: TwoDMap) {
        val value = occupationAdapter.getItem(occupation.key!!)
        binding.autoOccupation.setText(value?.description, false)
        hashMap["Occupation"] = occupation
    }

    private fun setProfession(p: List<StaticDataDetails?>?) {
        if (p != null) {
            val pData = p.filter { a -> a?.id == "PROFFESSIONSTATUS" }
            val sorted = pData.sortedBy { a -> a?.description }
            professionAdapter = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoProf.setAdapter(professionAdapter)
            binding.autoProf.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["Profession"] = TwoDMap(
                        key = p2,
                        value = professionAdapter.getItem(p2)?.subCodeID
                    )
                }
        }
    }

    private fun stopShimmer() {
        binding.mainLay.visibility = View.VISIBLE
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun setOccupation(p: List<StaticDataDetails?>?) {
        if (p != null) {
            val pData = p.filter { a -> a?.id == "OCCUPATION" }
            val sorted = pData.sortedBy { a -> a?.description }
            occupationAdapter = AutoTextArrayAdapter(requireContext(), 0, sorted)
            binding.autoOccupation.setAdapter(occupationAdapter)
            binding.autoOccupation.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["Occupation"] = TwoDMap(
                        key = p2,
                        value = occupationAdapter.getItem(p2)?.subCodeID
                    )
                }
        }
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(50, true)
    }

    override fun setOnClick() {
        binding.buttonBack.setOnClickListener(this)
        binding.buttonNext.setOnClickListener(this)
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.autoOccupation.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.occupation_required))
            false
        } else if (TextUtils.isEmpty(binding.autoProf.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.prof_status_required))
            false
        } else if (TextUtils.isEmpty(binding.incomeInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.income_per_required))
            false
        } else if (TextUtils.isEmpty(binding.workPlaceInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.work_place_required))
            false
        } else if (TextUtils.isEmpty(binding.natureInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.nature_business_required))
            false
        } else if (TextUtils.isEmpty(binding.durationInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.duration_employ))
            false
        } else if (binding.durationGroup.checkedRadioButtonId == -1) {
            ShowToast(requireContext(), getString(R.string.select_duration_employ))
            false
        } else if (TextUtils.isEmpty(binding.employerNameInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.employer_name))
            false
        } else if (TextUtils.isEmpty(binding.natureEInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.nature_employ_required))
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
         * @return A new instance of fragment IncomeSourceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IncomeSourceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = IncomeSourceFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.buttonNext) {
            if (validateFields()) {
                saveState()
                pagerData?.onNext(7)
            }
        } else if (p == binding.buttonBack) pagerData?.onBack(5)
    }
}

@Parcelize
data class IncomeData(
    @field:SerializedName("occupation")
    @field:Expose
    val occupation: TwoDMap?,
    @field:SerializedName("profession")
    @field:Expose
    val profession: TwoDMap?,
    @field:SerializedName("income")
    @field:Expose
    val income: String?,
    @field:SerializedName("workPlace")
    @field:Expose
    val workPlace: String?,
    @field:SerializedName("natureBusiness")
    @field:Expose
    val natureBusiness: String?,
    @field:SerializedName("duration")
    @field:Expose
    val duration: TwoDMap?,
    @field:SerializedName("employer")
    @field:Expose
    val employer: String?,
    @field:SerializedName("natureEmployment")
    @field:Expose
    val natureEmployment: String?,
) : Parcelable


class IncomeDataConverter {
    @TypeConverter
    fun from(data: IncomeData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, IncomeData::class.java)
    }

    @TypeConverter
    fun to(data: String?): IncomeData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, IncomeData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}