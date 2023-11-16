package com.elmacentemobile.view.fragment.go.steps

import android.annotation.SuppressLint
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
import com.elmacentemobile.data.model.user.ActivationData
import com.elmacentemobile.databinding.FragmentPlaceOfWorkAddressBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.JSONUtil
import com.elmacentemobile.util.OnAlertDialog
import com.elmacentemobile.util.ShowAlertDialog
import com.elmacentemobile.util.ShowToast
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.activity.MainActivity
import com.elmacentemobile.view.ep.adapter.AddressArrayAdapter
import com.elmacentemobile.view.fragment.go.PagerData
import com.elmacentemobile.view.model.BaseViewModel
import com.elmacentemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize


/**
 * A simple [Fragment] subclass.
 * Use the [PlaceOfWorkAddressFragment.onStep] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class PlaceOfWorkAddressFragment : Fragment(), AppCallbacks, OnAlertDialog, PagerData,
    View.OnClickListener {
    private lateinit var binding: FragmentPlaceOfWorkAddressBinding
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private val addressData = MutableLiveData<MutableList<AddressStaticData>>()
    private lateinit var stateData: WorkAddressState

    private var active: ActivationData? = null
    private val hashMap = HashMap<String, TwoDMap>()
    private lateinit var regionAdapter: AddressArrayAdapter
    private lateinit var districtAdapter: AddressArrayAdapter
    private lateinit var countyAdapter: AddressArrayAdapter
    private lateinit var subCountyAdapter: AddressArrayAdapter
    private lateinit var parishAdapter: AddressArrayAdapter
    private lateinit var villageAdapter: AddressArrayAdapter
    private lateinit var eaAdapter: AddressArrayAdapter
    private var customer: CustomerProduct? = null



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
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlaceOfWorkAddressBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        setStep()
        setToolbar()
        return binding.root.rootView
    }

    override fun setState() {
        val sData = widgetViewModel.storageDataSource.workAddressState.value
        AppLogger.instance.appLog("STATE:Saved", Gson().toJson(sData))
        if (sData != null) {
            if (sData.region != null) stateRegion(sData.region!!)
            if (sData.district != null) stateDistrict(sData.district!!)
            if (sData.county != null) stateCounty(sData.county!!)
            if (sData.subCounty != null) stateSubCounty(sData.subCounty!!)
            if (sData.parish != null) stateParish(sData.parish!!)
            if (sData.village != null) stateVillage(sData.village!!)
            if (sData.ea != null) stateEA(sData.ea!!)

            binding.streetInput.setText(sData.street)
            binding.cityInput.setText(sData.city)
            binding.codeInput.setText(sData.zipCode)
        }
    }

    @SuppressLint("NewApi")
    private fun setStep() {
        binding.progressIndicator.setProgress(50, true)
    }

    override fun setOnClick() {
        binding.buttonBack.setOnClickListener(this)
        binding.buttonNext.setOnClickListener(this)
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


    override fun setBinding() {
        active = widgetViewModel.storageDataSource.activationData.value
        customer = widgetViewModel.storageDataSource.customerProduct.value
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            loadAddressData()
            setAddress()
            setState()
        }, animationDuration.toLong())
    }

    private fun loadAddressData() {
        val data = JSONUtil().loadJSONAddress(requireActivity())
        addressData.value = data.address
    }

    private fun stopShimmer() {
        binding.mainLay.visibility = View.VISIBLE
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun setAddress() {
        addressData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) setRegion()
        }
    }

    @SuppressLint("NewApi")
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

    @SuppressLint("NewApi")
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

    @SuppressLint("NewApi")
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

    @SuppressLint("NewApi")
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

    @SuppressLint("NewApi")
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

    @SuppressLint("NewApi")
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

    @SuppressLint("NewApi")
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
        } else if (TextUtils.isEmpty(binding.streetInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.enter_street_), true)
            false
        } else if (TextUtils.isEmpty(binding.cityInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.enter_city_work_), true)
            false
        } else if (TextUtils.isEmpty(binding.codeInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.enter_zip_code), true)
            false
        } else true

    }

    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment PlaceOfWorkAddressFragment.
         */
        @JvmStatic
        fun onStep(pagerData: PagerData) = PlaceOfWorkAddressFragment().apply {
            this@Companion.pagerData = pagerData
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
        widgetViewModel.storageDataSource.workAddressState(stateData)
    }

    override fun onNegative() {
        pagerData?.clearState()
        (requireActivity() as MainActivity).provideNavigationGraph()
            .navigate(widgetViewModel.navigation().navigateLanding())
    }


    override fun onClick(p: View?) {
        if (p == binding.buttonNext) {
            if (validateFields()) {
                saveState()
                pagerData?.onNext(8)
            }
        } else if (p == binding.buttonBack) pagerData?.onBack(6)
    }

    override fun statePersist() {
        stateData = WorkAddressState(
            region = hashMap["Region"],
            district = hashMap["District"],
            county = hashMap["County"],
            subCounty = hashMap["SubCounty"],
            village = hashMap["Village"],
            parish = hashMap["Parish"],
            ea = hashMap["EA"],
            street = binding.streetInput.text.toString(),
            zipCode = binding.codeInput.text.toString(),
            city = binding.cityInput.text.toString(),
        )
    }
}

@Parcelize
data class WorkAddressState(
    @field:SerializedName("region")
    @field:Expose
    var region: TwoDMap? = null,
    @field:SerializedName("district")
    @field:Expose
    var district: TwoDMap? = null,
    @field:SerializedName("county")
    @field:Expose
    var county: TwoDMap? = null,
    @field:SerializedName("subCounty")
    @field:Expose
    var subCounty: TwoDMap? = null,
    @field:SerializedName("village")
    @field:Expose
    var village: TwoDMap? = null,
    @field:SerializedName("parish")
    @field:Expose
    var parish: TwoDMap? = null,
    @field:SerializedName("ea")
    @field:Expose
    var ea: TwoDMap? = null,
    @field:SerializedName("street")
    @field:Expose
    var street: String? = null,
    @field:SerializedName("zipCode")
    @field:Expose
    var zipCode: String? = null,
    @field:SerializedName("city")
    @field:Expose
    var city: String? = null

) : Parcelable

class WorkAddressStateTypeConverter {
    @TypeConverter
    fun from(data: WorkAddressState?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, WorkAddressState::class.java)
    }

    @TypeConverter
    fun to(data: String?): WorkAddressState? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, WorkAddressState::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}