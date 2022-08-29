package com.craft.silicon.centemobile.view.fragment.go.steps

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.room.TypeConverter
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.ToolbarEnum
import com.craft.silicon.centemobile.data.model.static_data.OnlineAccountProduct
import com.craft.silicon.centemobile.databinding.BlockCustomerTypeLayoutBinding
import com.craft.silicon.centemobile.databinding.BlockOnGoAccountItemBinding
import com.craft.silicon.centemobile.databinding.FragmentCustomerProductBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.OnAlertDialog
import com.craft.silicon.centemobile.util.ShowAlertDialog
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.util.callbacks.Confirm
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.ep.adapter.BranchAdapterItem
import com.craft.silicon.centemobile.view.ep.adapter.NameBaseAdapter
import com.craft.silicon.centemobile.view.ep.data.NameBaseData
import com.craft.silicon.centemobile.view.fragment.go.PagerData
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlin.math.abs


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CustomerProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CustomerProductFragment : Fragment(), AppCallbacks, View.OnClickListener, OnAlertDialog,
    PagerData, Confirm {


    private lateinit var binding: FragmentCustomerProductBinding
    private val baseViewModel: BaseViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()


    private val hashMap = HashMap<String, TwoDMap>()
    private lateinit var stateData: CustomerProduct
    private lateinit var branchAdapter: BranchAdapterItem
    private lateinit var currencyAdapter: NameBaseAdapter

    private var branchData: String? = null
    private var currencyData: String? = null
    private var accountData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        activity?.onBackPressedDispatcher?.addCallback(this,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    ShowAlertDialog().showDialog(
//                        context!!,
//                        getString(R.string.exit_registration),
//                        getString(R.string.proceed_registration),
//                        this@CustomerProductFragment
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
        binding = FragmentCustomerProductBinding.inflate(inflater, container, false)
        setBinding()
        getData()
        setTitle()
        setToolbar()
        setOnClick()
        setController()
        return binding.root.rootView
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
                    binding.collapsedLay.title = getString(R.string.customer_product)

                }
            } else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                if (state !== ToolbarEnum.COLLAPSED) {
                    val title = getString(R.string.customer_product)
                    title.replace("\n", " ")
                    state =
                        ToolbarEnum.COLLAPSED
                    binding.collapsedLay.title = title
                }
            }
        }
    }

    private fun setStep() {
        binding.progressIndicator.setProgress(10, true)
    }

    override fun onPositive() {
        saveState()
        pagerData?.currentPosition()
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
        widgetViewModel.storageDataSource.setCustomerProduct(stateData)
    }

    override fun statePersist() {
        stateData = CustomerProduct(
            type = hashMap["Type"],
            product = hashMap["Product"],
            currency = hashMap["Currency"],
            branch = hashMap["Branch"]
        )
    }

    private fun getData() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            binding.mainLay.visibility = VISIBLE
            setCustomerType()
            setProductType()
            getBranchData()
            setStep()
            setState()
        }, animationDuration.toLong())
    }

    override fun setState() {
        val sData = widgetViewModel.storageDataSource.customerProduct.value
        if (sData != null) {
            if (sData.type != null) stateCustomer(sData.type)
            if (sData.product != null) stateProduct(sData.product, sData)
            if (sData.branch != null) stateBranch(sData.branch)

        }
    }

    private fun stateCurrency(currency: TwoDMap) {
        val value = currencyAdapter.getItem(currency.key!!)
        currencyData = value?.id
        Handler(Looper.getMainLooper()).postDelayed({
            binding.currencyLay.autoEdit.setText(value?.id, false)
        }, 200)
        hashMap["Currency"] = currency
    }

    private fun stateBranch(branch: TwoDMap) {
        val value = branchAdapter.getItem(branch.key!!)
        branchData = value?.description
        binding.branchLay.autoEdit.setText(value?.description, false)
        hashMap["Branch"] = branch
    }

    private fun stateProduct(product: TwoDMap, sData: CustomerProduct) {
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.productLay.parent.getTabAt(product.key!!)?.select()
            }, 200)
            hashMap["Product"] = product
            stateCurrency(sData.currency!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stateCustomer(type: TwoDMap) {
        binding.customerLay.parent.getTabAt(type.key!!)?.select()
        hashMap["Type"] = type
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }


    private fun getBranchData() {
        val branch = widgetViewModel.storageDataSource.branchData.value
        if (branch != null) {
            branchAdapter = BranchAdapterItem(requireContext(), 0, branch.toMutableList())
            binding.branchLay.autoEdit.setAdapter(branchAdapter)
            binding.branchLay.autoEdit.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    hashMap["Branch"] = TwoDMap(
                        key = p2,
                        value = branchAdapter.getItem(p2)?.id
                    )
                    branchData = branchAdapter.getItem(p2)?.description

                }
        }
    }


    private fun setProductType() {
        val tab = binding.productLay.parent
        val param = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        param.setMargins(
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_5dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_5dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_5dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_5dp)
        )
        val products = mutableListOf<OnlineAccountProduct>()
        val mainData = baseViewModel.dataSource.productAccountData.value

        if (mainData != null)
            if (mainData.isNotEmpty()) {
                mainData.forEach {
                    products.removeIf { a -> a.description == it?.description }
                    products.add(it!!)
                }
                products.forEachIndexed { i, p ->
                    val item = tab.newTab()
                    val custom =
                        BlockOnGoAccountItemBinding.inflate(
                            LayoutInflater.from(requireContext()),
                            tab,
                            false
                        )
                    custom.data = p
                    item.tag = i
                    custom.parent.layoutParams = param
                    item.customView = custom.root.rootView
                    setProductSelect(tab, custom, i, mainData, p)
                    tab.addTab(item)
                }
            }
    }

    private fun setProductSelect(
        tab: TabLayout,
        custom: BlockOnGoAccountItemBinding,
        i: Int,
        currency: List<OnlineAccountProduct?>?,
        p: OnlineAccountProduct,
    ) {

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.currencyLay.autoEdit.text.clear()
                val c = currency?.filter { a -> a?.id == p.id }?.map { it?.relationID }
                if (tab?.tag == i) {
                    AppLogger.instance.appLog("Account:product", Gson().toJson(p))
                    hashMap["Product"] = TwoDMap(
                        key = i,
                        value = p.id
                    )
                    accountData = p.description
                    setCurrencyDropDown(c)
                    custom.parent.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.app_blue_dark
                        )
                    )
                    custom.amOne.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )

                    /*custom.existingAvatar.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )*/
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                custom.parent.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                custom.amOne.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dar_color_one
                    )
                )

                /*custom.existingAvatar.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dar_color_one
                    )
                )*/
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

    private fun setCurrencyDropDown(data: List<String?>?) {
        val currency = mutableListOf<NameBaseData>()
        data?.forEach {
            currency.add(
                NameBaseData(
                    text = it,
                    id = it
                )
            )
        }
        currencyAdapter = NameBaseAdapter(requireContext(), 0, currency.toMutableList())
        binding.currencyLay.autoEdit.setAdapter(currencyAdapter)
        binding.currencyLay.autoEdit.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, p2, _ ->
                hashMap["Currency"] = TwoDMap(
                    key = p2,
                    value = currencyAdapter.getItem(p2)?.id
                )
                currencyData = currencyAdapter.getItem(p2)?.id
            }
    }

    private fun setCustomerType() {
        val user = customerType
        val active = widgetViewModel.storageDataSource.activationData.value
        val tab = binding.customerLay.parent
        val param = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        param.setMargins(
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_5dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_5dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_5dp),
            binding.root.context.resources.getDimensionPixelSize(R.dimen.dimens_5dp)
        )
        if (active != null) {
            if (user.size != 1)
                user.removeAt(0)
        }

        user.forEachIndexed { index, type ->
            val item = tab.newTab().setText(type.type)
            val custom =
                BlockCustomerTypeLayoutBinding.inflate(LayoutInflater.from(requireContext()))
            custom.data = type
            item.tag = index
            custom.parent.layoutParams = param
            item.customView = custom.root
            onItemClick(custom, tab, index, type)
            tab.addTab(item)
        }
    }


    private fun onItemClick(
        custom: BlockCustomerTypeLayoutBinding,
        tab: TabLayout,
        index: Int,
        type: CustomerType
    ) {
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {

                if (tab?.tag == index) {
                    hashMap["Type"] = TwoDMap(
                        key = index,
                        value = getString(type.type)
                    )
                    custom.parent.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.app_blue_dark
                        )
                    )
                    custom.amOne.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    custom.newUser.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    custom.existingAvatar.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                custom.parent.setCardBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                custom.amOne.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dar_color_one
                    )
                )
                custom.newUser.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dar_color_one
                    )
                )
                custom.existingAvatar.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dar_color_one
                    )
                )
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun setOnClick() {
        binding.materialButton.setOnClickListener(this)
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

    override fun setController() {

    }

    override fun validateFields(): Boolean {
        return if (TextUtils.isEmpty(binding.currencyLay.autoEdit.editableText.toString())) {
            ShowToast(requireContext(), getString(R.string.select_currency), true)
            false
        } else if (TextUtils.isEmpty(binding.branchLay.autoEdit.text.toString())) {
            ShowToast(requireContext(), getString(R.string.select_branch), true)
            false
        } else true
    }

    override fun onConfirm() {
        saveState()
        pagerData?.onNext(2)
    }

    companion object {
        private var pagerData: PagerData? = null

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CustomerProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CustomerProductFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun onStep(pagerData: PagerData) = CustomerProductFragment().apply {
            this@Companion.pagerData = pagerData
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.materialButton) {
            if (validateFields()) {
                ProductConfirmationFragment.showDialog(
                    this.childFragmentManager, ProductConfirm(
                        account = accountData, branch = branchData, currency = currencyData
                    ), this
                )
            }
        }
    }
}

data class CustomerType(@DrawableRes val avatar: Int, @StringRes val type: Int) {
    val active: Boolean = true
}

val customerType = mutableListOf(
    CustomerType(
        avatar = R.drawable.add_user,
        type = R.string.new_customer
    ),
    CustomerType(
        avatar = R.drawable.existing_user,
        type = R.string.existing_customer
    )
)

@Parcelize
data class CustomerProduct(
    @field:SerializedName("type")
    val type: TwoDMap?,
    @field:SerializedName("product")
    val product: TwoDMap?,
    @field:SerializedName("currency")
    @field:Expose
    val currency: TwoDMap?,
    @field:SerializedName("branch")
    @field:Expose
    val branch: TwoDMap?
) : Parcelable

@Parcelize
data class TwoDMap(
    @field:SerializedName("key")
    @field:Expose
    var key: Int?,
    @field:SerializedName("value")
    @field:Expose
    var value: String?
) : Parcelable {
    @IgnoredOnParcel
    @field:SerializedName("extra")
    @field:Expose
    var extra: String? = null
}

class CustomerProductConverter {
    @TypeConverter
    fun from(data: CustomerProduct?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, CustomerProduct::class.java)
    }

    @TypeConverter
    fun to(data: String?): CustomerProduct? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, CustomerProduct::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().create()
    }
}

