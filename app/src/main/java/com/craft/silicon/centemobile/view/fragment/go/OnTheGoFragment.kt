package com.craft.silicon.centemobile.view.fragment.go

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.room.TypeConverter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.converter.DynamicAPIResponseConverter
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.databinding.FragmentOnTheGoBinding
import com.craft.silicon.centemobile.util.*
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.LoadingFragment
import com.craft.silicon.centemobile.view.fragment.go.steps.*
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.craft.silicon.centemobile.view.model.WorkStatus
import com.craft.silicon.centemobile.view.model.WorkerViewModel
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
 * Use the [OnTheGoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class OnTheGoFragment : Fragment(), AppCallbacks, PagerData, OnAlertDialog {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentOnTheGoBinding
    private var adapter: ONTHGFragmentAdapter? = null
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val baseViewModel: BaseViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private val workViewModel: WorkerViewModel by viewModels()

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
        freshStart()
        return binding.root.rootView
    }

    private fun freshStart() {
        val current = widgetViewModel.storageDataSource.currentPosition.value
        if (current == null || current == 0) clearState()
    }


    private fun checkCurrent() {
        val currentPosition = widgetViewModel.storageDataSource.currentPosition.value
        if (currentPosition != null) {
            if (currentPosition != 0)
                ShowAlertDialog().showDialog(
                    requireContext(),
                    getString(R.string.saved_state),
                    getString(R.string.proceed_registration_left),
                    this
                )
        }

    }

    private fun setLoading(it: Boolean) {
        if (it) {
            LoadingFragment.show(requireActivity().supportFragmentManager)
        } else {
            LoadingFragment.dismiss(requireActivity().supportFragmentManager)
        }
    }

    override fun createOtp() {
        (requireActivity() as MainActivity).initSMSBroadCast()
        setLoading(true)
        val userData = widgetViewModel.storageDataSource.addressState.value
        val phoneData = "${userData?.phone?.key}${userData?.phone?.value}"

        val json = JSONObject()
        json.put("MOBILENUMBER", phoneData)
        json.put("EMAILID", userData?.email)
        json.put("SERVICENAME", "SELFRAO")

        subscribe.add(
            baseViewModel.createOTP(json, requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    try {
                        AppLogger.instance.appLog(
                            "OTP:CREATE:Response", BaseClass.decryptLatest(
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
                                "OTP:CREATE:Response",
                                Gson().toJson(resData)
                            )
                            if (BaseClass.nonCaps(resData?.status) == StatusEnum.SUCCESS.type) {
                                setLoading(false)
                                ShowToast(requireContext(), resData?.message)
                            } else if (BaseClass.nonCaps(resData?.status) == StatusEnum.TOKEN.type) {
                                workViewModel.routeData(viewLifecycleOwner, object : WorkStatus {
                                    override fun workDone(b: Boolean) {
                                        setLoading(false)
                                        if (b) createOtp()
                                    }
                                })
                            } else {
                                setLoading(false)
                                AlertDialogFragment.newInstance(
                                    DialogData(
                                        title = R.string.error,
                                        subTitle = resData?.message,
                                        R.drawable.warning_app
                                    ),
                                    requireActivity().supportFragmentManager
                                )
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
                    it.printStackTrace()
                })
        )

    }

    override fun onPositive() {
        val currentPosition = widgetViewModel.storageDataSource.currentPosition.asLiveData()
        currentPosition.observe(viewLifecycleOwner) {
            AppLogger.instance.appLog("Current:step", it.toString())
            binding.pager.setCurrentItem(it!!, false)
            currentPosition.removeObservers(viewLifecycleOwner)
        }

    }

    override fun onNegative() {
        widgetViewModel.storageDataSource.setCurrentPosition(0)
        widgetViewModel.storageDataSource.deleteRecommendState()
        widgetViewModel.storageDataSource.deleteParentDetails()
        widgetViewModel.storageDataSource.deleteCustomerProduct()
        widgetViewModel.storageDataSource.deleteIDDetails()
        widgetViewModel.storageDataSource.deleteAddress()
    }


    override fun setBinding() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setViewPager()
            checkCurrent()
        }, animationDuration.toLong())
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
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
            ONTHGFragmentAdapter(
                requireActivity().supportFragmentManager,
                lifecycle,
                this
            )
        binding.pager.adapter = adapter
        binding.pager.isUserInputEnabled = false
    }


    override fun onNext(pos: Int) {
        if (pos == 11) {
            createOtp()

        }
        moveViewPager(pos)
    }

    override fun onBack(pos: Int) {
        moveViewPager(pos)
    }

    override fun currentPosition() {
        widgetViewModel.storageDataSource
            .setCurrentPosition(binding.pager.currentItem)
    }

    override fun clearState() {
        val state = widgetViewModel.storageDataSource
        state.setCurrentPosition(0)
        state.deleteCustomerProduct()
        state.deleteIDDetails()
        state.deleteParentDetails()
        state.deleteRecommendState()
        state.deleteAddress()
        state.deleteOtherServices()
        state.deleteNOK()
        state.deleteIncomeSource()
    }


    override fun finish() {
        clearState()
        Handler(Looper.getMainLooper()).postDelayed({
            (requireActivity() as MainActivity)
                .provideNavigationGraph().navigateUp()
        }, 200)
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
    private val move: PagerData
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return TABS
    }

    override fun createFragment(position: Int): Fragment {
        return setFragment(position)
    }

    private fun setFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnTheLandingFragment.onStep(move)
            1 -> CustomerProductFragment.onStep(move)
            2 -> TermsAndConditionFragment.newInstance(move)
            3 -> IDFragment.onStep(move)
            4 -> IDDetailsFragment.onStep(move)
            5 -> ParentDetailsFragment.onStep(move)
            6 -> IncomeSourceFragment.onStep(move)
            7 -> NextKinFragment.onStep(move)
            8 -> AddressFragment.onStep(move)
            9 -> OtherServiceFragment.onStep(move)
            10 -> HearAboutFragment.onStep(move)
            11 -> GoOTPFragment.onStep(move)
            else -> {
                throw Exception("Not implemented")
            }
        }
    }


    companion object {
        private const val TABS = 12
    }
}

interface PagerData {
    fun onNext(pos: Int) {
        throw Exception("Not implemented")
    }

    fun onBack(pos: Int) {
        throw Exception("Not implemented")
    }


    fun currentPosition() {
        throw Exception("Not implemented")
    }

    fun clearState() {
        throw Exception("Not implemented")
    }

    fun statePersist() {
        throw Exception("Not implemented")
    }

    fun setState() {
        throw Exception("Not implemented")
    }

    fun saveState() {
        throw Exception("Not implemented")
    }

    fun completeStep() {
        throw Exception("Not implemented")
    }

    fun finish() {
        throw Exception("Not implemented")
    }


    fun otpPager(otpStr: String?, mobileStr: String?) {}


    fun onService(data: OtherService, boolean: Boolean) {
        throw Exception("Not implemented")
    }

    fun createOtp() {

    }
}

@Parcelize
data class OnTheGoData(
    @field:SerializedName("customerProduct")
    @field:Expose
    var customer: CustomerProduct?,
    @field:SerializedName("iDDetails")
    @field:Expose
    var iDDetails: IDDetails?,
    @field:SerializedName("oCRData")
    @field:Expose
    var oCRData: OCRData?,
    @field:SerializedName("parentDetails")
    @field:Expose
    var parentDetails: ParentDetails?,
    @field:SerializedName("nextKinData")
    @field:Expose
    var nextKinData: NextKinData?,
    @field:SerializedName("incomeData")
    @field:Expose
    var incomeData: IncomeData?,
    @field:SerializedName("addressData")
    @field:Expose
    var addressData: AddressData?,
    @field:SerializedName("otherServices")
    @field:Expose
    var otherService: MutableList<String>?,

    ) : Parcelable

class OnTheGoConverter {
    @TypeConverter
    fun from(data: OnTheGoData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, OnTheGoData::class.java)
    }

    @TypeConverter
    fun to(data: String?): OnTheGoData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, OnTheGoData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}


