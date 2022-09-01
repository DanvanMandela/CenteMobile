package com.craft.silicon.centemobile.view.fragment.go.steps

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.room.TypeConverter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.databinding.FragmentOtherServiceBinding
import com.craft.silicon.centemobile.util.OnAlertDialog
import com.craft.silicon.centemobile.util.ShowAlertDialog
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.ep.controller.OtherServicesController
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OtherServiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class OtherServiceFragment : Fragment(), AppCallbacks, PagerData, View.OnClickListener,
    OnAlertDialog {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentOtherServiceBinding
    private val service = mutableListOf<String>()
    private val stateData = mutableListOf<TwoDMap>()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private lateinit var controller: OtherServicesController


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
        widgetViewModel.storageDataSource.setOtherServices(OtherServiceData(stateData))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOtherServiceBinding.inflate(inflater, container, false)
        setBinding()
        setOnClick()
        setController()
        setToolbar()

        setState()
        return binding.root.rootView
    }

    override fun setController() {
        controller = OtherServicesController(this)
        binding.container.setController(controller)
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

    override fun setState() {
        val sData = widgetViewModel.storageDataSource.otherServices.value
        if (sData != null) {
            stateData.clear()
            sData.services.forEach {
                services.map { a ->
                    if (a.id == it.key) {
                        a.selected = true
                    }
                }
                stateData.add(it)
            }
            controller.setData(services)
        } else controller.setData(services)

    }



    override fun setOnClick() {
        binding.buttonBack.setOnClickListener(this)
        binding.buttonNext.setOnClickListener(this)
    }

    override fun onService(data: OtherService, boolean: Boolean) {
        if (boolean) {
            stateData.removeIf { it.key == data.id }
            stateData.add(TwoDMap(key = data.id, value = getString(data.title)))
        } else {
            stateData.removeIf { it.key == data.id }
        }
    }

    override fun validateFields(): Boolean {
        return if (stateData.isEmpty()) {
            ShowToast(requireContext(), getString(R.string.select_mobile_agent), true)
            false
        } else {
            if (stateData.size < 2) {
                ShowToast(requireContext(), getString(R.string.select_mobile_agent), true)
                false
            } else true
        }
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(80, true)
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setStep()
            setState()
        }, animationDuration.toLong())
    }

    private fun stopShimmer() {
        binding.mainLay.visibility = View.VISIBLE
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }


    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OtherServiceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OtherServiceFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = OtherServiceFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.buttonBack) pagerData?.onBack(8)
        else if (p == binding.buttonNext) {
            if (validateFields()) {
                saveState()
                pagerData?.onNext(10)
            }
        }
    }
}


@Parcelize
data class OtherService(
    @field:SerializedName("id")
    @field:Expose
    val id: Int,
    @field:SerializedName("title")
    @field:Expose
    @StringRes val title: Int,

    @field:SerializedName("icon")
    @field:Expose
    @DrawableRes val icon: Int
) : Parcelable {
    @IgnoredOnParcel
    @field:SerializedName("selected")
    @field:Expose
    var selected: Boolean = false
}

@Parcelize
data class OtherServiceData(
    @field:SerializedName("services")
    @field:Expose
    val services: MutableList<TwoDMap>
) : Parcelable

val services = mutableListOf(
    OtherService(
        id = 1,
        title = R.string.mobile_banking,
        icon = R.drawable.mobile_banking
    ),
    OtherService(
        id = 2,
        title = R.string.agent_banking,
        icon = R.drawable.agent_banking
    )
)

@BindingAdapter("other", "service")
fun MaterialCardView.other(callbacks: PagerData, service: OtherService) {
    val checkBox = this.findViewById(R.id.checked) as CheckBox
    val child = this.findViewById(R.id.child) as MaterialCardView

    if (service.selected) {
        checkBox.isChecked = true
        callbacks.onService(service, true)
        this.setCardBackgroundColor(
            ContextCompat.getColor(
                this.context,
                R.color.app_blue_light
            )
        )
    } else {
        checkBox.isChecked = false
        callbacks.onService(service, false)
        this.setCardBackgroundColor(
            ContextCompat.getColor(
                this.context,
                R.color.white
            )
        )
    }

    child.setOnClickListener {
        checkBox.isChecked = !checkBox.isChecked
    }
    checkBox.setOnCheckedChangeListener { _, checked ->
        if (checked) {
            service.selected = true
            callbacks.onService(service, true)
            this.setCardBackgroundColor(
                ContextCompat.getColor(
                    this.context,
                    R.color.app_blue_light
                )
            )
        } else {
            service.selected = false
            callbacks.onService(service, false)
            this.setCardBackgroundColor(
                ContextCompat.getColor(
                    this.context,
                    R.color.white
                )
            )
        }
    }

}

class OtherServiceConverter {
    @TypeConverter
    fun from(data: OtherServiceData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, OtherServiceData::class.java)
    }

    @TypeConverter
    fun to(data: String?): OtherServiceData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, OtherServiceData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}



