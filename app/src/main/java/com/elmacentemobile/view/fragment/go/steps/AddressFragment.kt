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
import androidx.lifecycle.MutableLiveData
import androidx.room.TypeConverter
import com.elmacentemobile.R
import com.elmacentemobile.data.model.address.AddressHelperModel
import com.elmacentemobile.data.model.address.AddressStaticData
import com.elmacentemobile.data.model.converter.DynamicAPIResponseConverter
import com.elmacentemobile.data.source.constants.StatusEnum
import com.elmacentemobile.databinding.FragmentAddressBinding
import com.elmacentemobile.util.*
import com.elmacentemobile.util.BaseClass.verifyEmail
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.dialog.AlertDialogFragment
import com.elmacentemobile.view.dialog.DialogData
import com.elmacentemobile.view.ep.adapter.AddressArrayAdapter
import com.elmacentemobile.view.fragment.go.PagerData
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.elmacentemobile.view.model.WorkStatus
import com.elmacentemobile.view.model.WorkerViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddressFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class AddressFragment : Fragment(), AppCallbacks, View.OnClickListener, OnAlertDialog, PagerData {

    private lateinit var binding: FragmentAddressBinding
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private val workViewModel: WorkerViewModel by viewModels()
    private val addressData = MutableLiveData<MutableList<AddressStaticData>>()
    private lateinit var stateData: AddressState

    private val subscribe = CompositeDisposable()

    private val hashMap = HashMap<String, TwoDMap>()
    private lateinit var regionAdapter: AddressArrayAdapter
    private lateinit var districtAdapter: AddressArrayAdapter
    private lateinit var countyAdapter: AddressArrayAdapter
    private lateinit var subCountyAdapter: AddressArrayAdapter
    private lateinit var parishAdapter: AddressArrayAdapter
    private lateinit var villageAdapter: AddressArrayAdapter
    private lateinit var eaAdapter: AddressArrayAdapter


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
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        setStep()
        setToolbar()
        setOnCountryCode()
        return binding.root.rootView
    }

    private fun setOnCountryCode() {
        binding.codeInput.setText(binding.countryCodeHolder.selectedCountryCode)
        binding.countryCodeHolder.setOnCountryChangeListener {
            binding.codeInput.setText(binding.countryCodeHolder.selectedCountryCode)
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
        widgetViewModel.storageDataSource.setAddressState(stateData)
    }

    override fun onNegative() {
        pagerData?.clearState()
        (requireActivity() as MainActivity).provideNavigationGraph()
            .navigate(widgetViewModel.navigation().navigateLanding())
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(70, true)
    }

    override fun setOnClick() {
        binding.buttonBack.setOnClickListener(this)
        binding.buttonNext.setOnClickListener(this)
    }

    override fun statePersist() {
        stateData = AddressState(
            region = hashMap["Region"],
            district = hashMap["District"],
            county = hashMap["County"],
            subCounty = hashMap["SubCounty"],
            village = hashMap["Village"],
            parish = hashMap["Parish"],
            ea = hashMap["EA"],
            email = binding.emailInput.text.toString(),
            phone = TwoDMap(
                key = binding.countryCodeHolder.selectedCountryCode.toInt(),
                value = binding.editMobile.text.toString()
            ),
            phoneTwo = TwoDMap(
                key = binding.countryCodeHolderTwo.selectedCountryCode.toInt(),
                value = binding.editMobileTwo.text.toString()
            ),
            address = binding.addressInput.text.toString(),
            countryCode = binding.countryCodeHolder.selectedCountryCode,
            city = binding.cityInput.text.toString(),
        )
    }

    override fun setBinding() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            loadAddressData()
            setAddress()
            setState()
            setMobile()
        }, animationDuration.toLong())
    }

    private fun setMobile() {
        val customerType = baseViewModel.dataSource.customerProduct
        if (customerType.value!!.type!!.value == getString(R.string.existing_customer)) {
            binding.editMobile.setText(
                baseViewModel
                    .dataSource.activationData.value?.mobile?.substring(3)
            )
            binding.editMobile.isEnabled = false
            binding.countryCodeHolder.isEnabled = false
        }
    }

    override fun setState() {
        val sData = widgetViewModel.storageDataSource.addressState.value
        AppLogger.instance.appLog("STATE:Saved", Gson().toJson(sData))
        if (sData != null) {
            if (sData.region != null) stateRegion(sData.region!!)
            if (sData.district != null) stateDistrict(sData.district!!)
            if (sData.county != null) stateCounty(sData.county!!)
            if (sData.subCounty != null) stateSubCounty(sData.subCounty!!)
            if (sData.parish != null) stateParish(sData.parish!!)
            if (sData.village != null) stateVillage(sData.village!!)
            if (sData.ea != null) stateEA(sData.ea!!)

            binding.emailInput.setText(sData.email)
            if (sData.phone != null) {
                binding.countryCodeHolder.setCountryForPhoneCode(sData.phone?.key!!)
                binding.editMobile.setText(sData.phone!!.value)
            }
            if (sData.phoneTwo != null) {
                binding.countryCodeHolderTwo.setCountryForPhoneCode(sData.phoneTwo?.key!!)
                binding.editMobileTwo.setText(sData.phoneTwo!!.value)
            }

            binding.addressInput.setText(sData.address)
            binding.cityInput.setText(sData.city)
            binding.codeInput.setText(sData.countryCode)
        }
    }

    private fun stateEA(ea: TwoDMap) {
        binding.autoEA.setText(ea.value, false)
        hashMap["EA"] = ea
    }

    private fun stateVillage(village: TwoDMap) {
        binding.autoVillage.setText(village.value, false)
        hashMap["Village"] = village
    }

    private fun stateParish(parish: TwoDMap) {
        binding.autoParish.setText(parish.value, false)
        hashMap["Parish"] = parish
    }

    private fun stateSubCounty(subCounty: TwoDMap) {
        binding.autoSubCounty.setText(subCounty.value, false)
        hashMap["SubCounty"] = subCounty
    }

    private fun stateCounty(county: TwoDMap) {
        binding.autoCounty.setText(county.value, false)
        hashMap["County"] = county
    }

    private fun stateDistrict(district: TwoDMap) {
        binding.autoDistrict.setText(district.value, false)
        hashMap["District"] = district
    }

    private fun stateRegion(region: TwoDMap) {
        val value = regionAdapter.getItem(region.key!!)
        binding.autoRegion.setText(value?.address, false)
        hashMap["Region"] = region
    }


    private fun loadAddressData() {
        val data = JSONUtil().loadJSONAddress(requireActivity())
        addressData.value = data.address
    }

    private fun setAddress() {
        addressData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) setRegion()
        }
    }

    private fun setRegion() {
        val address = mutableListOf<AddressHelperModel>()
        for (s in addressData.value!!) {
            address.removeIf { it.address == s.region }
            address.add(AddressHelperModel(s.region, s.addressCode))
        }
        address.sortBy { it.address }
        regionAdapter = AddressArrayAdapter(requireContext(), 0, address)
        binding.autoRegion.setAdapter(regionAdapter)
        binding.autoRegion.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                setDistrict(regionAdapter.getItem(p2)?.address)
                hashMap["Region"] = TwoDMap(
                    key = p2,
                    value = regionAdapter.getItem(p2)?.address
                )
            }

    }

    private fun setDistrict(code: String?) {
        binding.autoDistrict.editableText.clear()
        val address = mutableListOf<AddressHelperModel>()
        val filtered = addressData.value!!.filter { it.region == code }
        for (s in filtered) {
            address.removeIf { it.address == s.districtName }
            address.add(AddressHelperModel(s.districtName, s.addressCode))
        }
        address.sortBy { it.address }
        districtAdapter = AddressArrayAdapter(requireContext(), 0, address)
        binding.autoDistrict.setAdapter(districtAdapter)
        binding.autoDistrict.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                setCounty(districtAdapter.getItem(p2)?.address)
                hashMap["District"] = TwoDMap(
                    key = p2,
                    value = districtAdapter.getItem(p2)?.address
                )
            }

    }

    private fun stopShimmer() {
        binding.mainLay.visibility = View.VISIBLE
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }


    private fun setCounty(code: String?) {
        binding.autoCounty.editableText.clear()
        val address = mutableListOf<AddressHelperModel>()
        val filtered = addressData.value!!.filter { it.districtName == code }
        for (s in filtered) {
            address.removeIf { it.address == s.countyName }
            address.add(AddressHelperModel(s.countyName, s.addressCode))
        }
        address.sortBy { it.address }
        countyAdapter = AddressArrayAdapter(requireContext(), 0, address)
        binding.autoCounty.setAdapter(countyAdapter)
        binding.autoCounty.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                setSubCounty(countyAdapter.getItem(p2)?.address)
                hashMap["County"] = TwoDMap(
                    key = p2,
                    value = countyAdapter.getItem(p2)?.address
                )
            }
    }

    private fun setSubCounty(code: String?) {

        binding.autoSubCounty.editableText.clear()
        val address = mutableListOf<AddressHelperModel>()
        val filtered = addressData.value!!.filter { it.countyName == code }
        for (s in filtered) {
            address.removeIf { it.address == s.subCountyName }
            address.add(AddressHelperModel(s.subCountyName, s.addressCode))
        }
        address.sortBy { it.address }
        subCountyAdapter = AddressArrayAdapter(requireContext(), 0, address)
        binding.autoSubCounty.setAdapter(subCountyAdapter)
        binding.autoSubCounty.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                setParish(subCountyAdapter.getItem(p2)?.address)
                hashMap["SubCounty"] = TwoDMap(
                    key = p2,
                    value = subCountyAdapter.getItem(p2)?.address
                )
            }
    }

    private fun setParish(code: String?) {
        binding.autoParish.editableText.clear()
        val address = mutableListOf<AddressHelperModel>()
        val filtered = addressData.value!!.filter { it.subCountyName == code }
        for (s in filtered) {
            address.removeIf { it.address == s.parishName }
            address.add(AddressHelperModel(s.parishName, s.addressCode))
        }
        address.sortBy { it.address }
        parishAdapter = AddressArrayAdapter(requireContext(), 0, address)
        binding.autoParish.setAdapter(parishAdapter)
        binding.autoParish.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                setVillage(parishAdapter.getItem(p2)?.address)
                hashMap["Parish"] = TwoDMap(
                    key = p2,
                    value = parishAdapter.getItem(p2)?.address
                )
            }
    }

    private fun setVillage(code: String?) {
        binding.autoVillage.editableText.clear()
        val address = mutableListOf<AddressHelperModel>()
        val filtered = addressData.value!!.filter { it.parishName == code }
        for (s in filtered) {
            address.removeIf { it.address == s.villageName }
            address.add(AddressHelperModel(s.villageName, s.addressCode))
        }
        address.sortBy { it.address }
        villageAdapter = AddressArrayAdapter(requireContext(), 0, address)
        binding.autoVillage.setAdapter(villageAdapter)
        binding.autoVillage.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                setEA(villageAdapter.getItem(p2)?.address)
                hashMap["Village"] = TwoDMap(
                    key = p2,
                    value = villageAdapter.getItem(p2)?.address
                )
            }

    }

    private fun setEA(code: String?) {
        binding.autoEA.editableText.clear()
        val address = mutableListOf<AddressHelperModel>()
        val filtered = addressData.value!!.filter { it.villageName == code }
        for (s in filtered) {
            address.removeIf { it.address == s.eaName }
            address.add(AddressHelperModel(s.eaName, s.addressCode))
        }
        address.sortBy { it.address }
        eaAdapter = AddressArrayAdapter(requireContext(), 0, address)
        binding.autoEA.setAdapter(eaAdapter)
        binding.autoEA.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                hashMap["EA"] = TwoDMap(
                    key = p2,
                    value = eaAdapter.getItem(p2)?.address,

                    ).apply {
                    this.extra = eaAdapter.getItem(p2)?.code
                }
            }
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.autoRegion.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_region), true)
            false
        } else if (TextUtils.isEmpty(binding.autoDistrict.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_district), true)
            false
        } else if (TextUtils.isEmpty(binding.autoCounty.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_county), true)
            false
        } else if (TextUtils.isEmpty(binding.autoSubCounty.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_sub_county), true)
            false
        } else if (TextUtils.isEmpty(binding.autoParish.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_parish), true)
            false
        } else if (TextUtils.isEmpty(binding.autoVillage.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_village), true)
            false
        } else if (TextUtils.isEmpty(binding.autoEA.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_ea), true)
            false
        } else if (TextUtils.isEmpty(binding.editMobile.text.toString())) {
            ShowToast(requireContext(), getString(R.string.enter_phone_number), true)
            false
        } else if (TextUtils.isEmpty(binding.addressInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.enter_address), true)
            false
        } else if (TextUtils.isEmpty(binding.cityInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.enter_city), true)
            false
        } else if (TextUtils.isEmpty(binding.codeInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.enter_country_code), true)
            false
        } else {
            if (!TextUtils.isEmpty(binding.emailInput.text.toString())) {
                if (verifyEmail(binding.emailInput.text.toString())) true else {
                    ShowToast(requireContext(), getString(R.string.enter_valid_email), true)
                    false
                }
            } else if (binding.editMobile.text.toString().length < 8) {
                ShowToast(requireContext(), getString(R.string.enter_valid_mobile), true)
                false
            } else if (!TextUtils.isEmpty(binding.editMobileTwo.text.toString())) {
                if (binding.editMobileTwo.text.toString().length < 8) {
                    ShowToast(requireContext(), getString(R.string.enter_valid_mobile), true)
                    false
                } else true
            } else true
        }
    }

    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddressFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddressFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = AddressFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    private fun checkUserExist(mobile: String) {
        setLoading(true)
        val json = JSONObject()
        json.put("CUSTOMERMOBILENUMBER", mobile)
        subscribe.add(
            baseViewModel.customerNumberExist(json, requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    try {
                        AppLogger.instance.appLog(
                            "CUSTOMER:EXIST:Response", BaseClass.decryptLatest(
                                it.response,
                                baseViewModel.dataSource.deviceData.value!!.device,
                                true,
                                baseViewModel.dataSource.deviceData.value!!.run
                            )
                        )
                        if (it.response == StatusEnum.ERROR.type) {
                            setLoading(false)
                            AlertDialogFragment.newInstance(
                                DialogData(
                                    title = R.string.error,
                                    subTitle = getString(R.string.something_),
                                    R.drawable.warning_app
                                ),
                                requireActivity().supportFragmentManager
                            )
                        } else {
                            val resData = DynamicAPIResponseConverter().to(
                                BaseClass.decryptLatest(
                                    it.response,
                                    baseViewModel.dataSource.deviceData.value!!.device,
                                    true,
                                    baseViewModel.dataSource.deviceData.value!!.run
                                )
                            )
                            AppLogger.instance.appLog(
                                "CUSTOMER:EXIST:Response",
                                Gson().toJson(resData)
                            )
                            if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                setLoading(false)
                                AlertDialogFragment.newInstance(
                                    DialogData(
                                        title = R.string.error,
                                        subTitle = resData?.message,
                                        R.drawable.warning_app
                                    ),
                                    requireActivity().supportFragmentManager
                                )
                            } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                workViewModel.routeData(viewLifecycleOwner, object : WorkStatus {
                                    override fun workDone(b: Boolean) {
                                        setLoading(false)
                                        if (b) checkUserExist(mobile)
                                    }
                                })
                            } else {
                                setLoading(false)
                                saveState()
                                pagerData?.onNext(9)
                            }
                        }
                    } catch (e: Exception) {
                        setLoading(false)
                        e.printStackTrace()
                        AlertDialogFragment.newInstance(
                            DialogData(
                                title = R.string.error,
                                subTitle = getString(R.string.something_),
                                R.drawable.warning_app
                            ),
                            requireActivity().supportFragmentManager
                        )
                    }
                }, {
                    setLoading(false)
                    it.printStackTrace()
                    AlertDialogFragment.newInstance(
                        DialogData(
                            title = R.string.error,
                            subTitle = getString(R.string.something_),
                            R.drawable.warning_app
                        ),
                        requireActivity().supportFragmentManager
                    )
                })
        )


    }

    private fun setLoading(it: Boolean) {
        if (it) binding.motionContainer.setTransition(
            R.id.loadingState, R.id.userState
        ) else binding.motionContainer.setTransition(
            R.id.userState, R.id.loadingState
        )
    }

    override fun onClick(p: View?) {
        if (p == binding.buttonNext) {
            val customerType = baseViewModel.dataSource.customerProduct
            if (customerType.value!!.type!!.value != getString(R.string.existing_customer)) {
                if (validateFields()) {
                    checkUserExist(
                        binding.countryCodeHolder.selectedCountryCode +
                                binding.editMobile.text.toString()
                    )
                }
            } else pagerData?.onNext(9)

        } else if (p == binding.buttonBack) pagerData?.onBack(7)
    }
}

@Parcelize
data class AddressData(
    @field:SerializedName("address")
    @field:Expose
    var address: String?,
    @field:SerializedName("email")
    @field:Expose
    var email: String?,
    @field:SerializedName("phone")
    @field:Expose
    var phone: String?,
    @field:SerializedName("phoneTwo")
    @field:Expose
    var phoneTwo: String?,
    @field:SerializedName("addressCode")
    @field:Expose
    var addressCode: String?,
    @field:SerializedName("city")
    @field:Expose
    var city: String?,
    @field:SerializedName("countryCode")
    @field:Expose
    var countryCode: String?
) : Parcelable

@Parcelize
data class AddressState(
    @field:SerializedName("region")
    @field:Expose
    var region: TwoDMap?,
    @field:SerializedName("district")
    @field:Expose
    var district: TwoDMap?,
    @field:SerializedName("county")
    @field:Expose
    var county: TwoDMap?,
    @field:SerializedName("subCounty")
    @field:Expose
    var subCounty: TwoDMap?,
    @field:SerializedName("village")
    @field:Expose
    var village: TwoDMap?,
    @field:SerializedName("parish")
    @field:Expose
    var parish: TwoDMap?,
    @field:SerializedName("ea")
    @field:Expose
    var ea: TwoDMap?,
    @field:SerializedName("email")
    @field:Expose
    var email: String?,
    @field:SerializedName("phone")
    @field:Expose
    var phone: TwoDMap?,
    @field:SerializedName("phoneTwo")
    @field:Expose
    var phoneTwo: TwoDMap?,
    @field:SerializedName("address")
    @field:Expose
    var address: String?,
    @field:SerializedName("countryCode")
    @field:Expose
    var countryCode: String?,
    @field:SerializedName("city")
    @field:Expose
    var city: String?

) : Parcelable


class AddressStateTypeConverter {
    @TypeConverter
    fun from(data: AddressState?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, AddressState::class.java)
    }

    @TypeConverter
    fun to(data: String?): AddressState? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, AddressState::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}