package com.craft.silicon.centemobile.view.fragment.go.steps

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.room.TypeConverter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentNextKinBinding
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NextKinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class NextKinFragment : Fragment(), AppCallbacks, View.OnClickListener, OnAlertDialog, PagerData {

    private lateinit var binding: FragmentNextKinBinding
    private lateinit var stateData: NextKinData

    private val widgetViewModel: WidgetViewModel by viewModels()
    private var customerKey = 0


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
        widgetViewModel.storageDataSource.setNKData(stateData)
    }

    override fun statePersist() {
        stateData = NextKinData(
            account = if (customerKey == 1) binding.accountInput.text.toString() else "",
            firstName = binding.nKFNameInput.text.toString(),
            middleName = binding.nKMNameInput.text.toString(),
            lastName = binding.nKLNameInput.text.toString(),
            phone = TwoDMap(
                key = binding.countryCodeHolder.selectedCountryCode.toInt(),
                value = binding.editMobile.text.toString()
            ),
            phoneTwo = TwoDMap(
                key = binding.countryCodeHolderTwo.selectedCountryCode.toInt(),
                value = binding.editMobileTwo.text.toString()
            ),
            address = binding.addressInput.text.toString()
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNextKinBinding.inflate(inflater, container, false)
        setOnClick()
        setToolbar()
        setBinding()

        setAccountField()
        return binding.root.rootView
    }

    private fun setAccountField() {
        val customerProduct = widgetViewModel.storageDataSource.customerProduct.asLiveData()
        customerProduct.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.type?.key == 0) {
                    customerKey = it.type.key!!
                    binding.accountLay.visibility = GONE
                }
            }
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

    private fun stopShimmer() {
        binding.mainLay.visibility = VISIBLE
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = GONE
    }

    override fun setBinding() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setStep()
            setState()
        }, animationDuration.toLong())
    }

    override fun setState() {
        val sData = widgetViewModel.storageDataSource.nextOfKin.value
        if (sData != null) {
            binding.accountInput.setText(sData.account)
            binding.nKFNameInput.setText(sData.firstName)
            binding.nKMNameInput.setText(sData.middleName)
            binding.nKLNameInput.setText(sData.lastName)
            binding.countryCodeHolder.setCountryForPhoneCode(sData.phone?.key!!)
            binding.editMobile.setText(sData.phone.value)
            binding.countryCodeHolderTwo.setCountryForPhoneCode(sData.phoneTwo?.key!!)
            binding.editMobileTwo.setText(sData.phoneTwo.value)
            binding.addressInput.setText(sData.address)
        }
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(60, true)
    }

    override fun setOnClick() {
        binding.buttonNext.setOnClickListener(this)
        binding.buttonBack.setOnClickListener(this)
    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.accountInput.text.toString()) && customerKey == 1) {
            ShowToast(requireContext(), getString(R.string.cente_account_required), true)
            false
        } else if (TextUtils.isEmpty(binding.nKFNameInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.n_k_f_name_required), true)
            false
        } else if (TextUtils.isEmpty(binding.nKLNameInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.n_k_l_name_required), true)
            false
        } else if (TextUtils.isEmpty(binding.editMobile.text.toString())) {
            ShowToast(requireContext(), getString(R.string.n_k_phone_required), true)
            false
        } else if (TextUtils.isEmpty(binding.addressInput.text.toString())) {
            ShowToast(requireContext(), getString(R.string.n_k_address_required), true)
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
         * @return A new instance of fragment NextKinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NextKinFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = NextKinFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.buttonBack) pagerData?.onBack(6)
        else if (p == binding.buttonNext) {
            if (validateFields()) {
                saveState()
                pagerData?.onNext(8)
            }
        }
    }
}

@Parcelize
data class NextKinData(
    @field:SerializedName("account")
    @field:Expose
    val account: String?,
    @field:SerializedName("firstName")
    @field:Expose
    val firstName: String?,
    @field:SerializedName("middleName")
    @field:Expose
    val middleName: String?,
    @field:SerializedName("lastName")
    @field:Expose
    val lastName: String?,
    @field:SerializedName("phone")
    @field:Expose
    val phone: TwoDMap?,
    @field:SerializedName("phoneTwo")
    @field:Expose
    val phoneTwo: TwoDMap?,
    @field:SerializedName("address")
    @field:Expose
    val address: String?
) : Parcelable

class NextKinDataConverter {
    @TypeConverter
    fun from(data: NextKinData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, NextKinData::class.java)
    }

    @TypeConverter
    fun to(data: String?): NextKinData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, NextKinData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}