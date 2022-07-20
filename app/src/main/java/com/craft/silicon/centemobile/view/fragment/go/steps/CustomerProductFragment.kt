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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.static_data.OnlineAccountProduct
import com.craft.silicon.centemobile.databinding.BlockCustomerTypeLayoutBinding
import com.craft.silicon.centemobile.databinding.BlockOnGoAccountItemBinding
import com.craft.silicon.centemobile.databinding.FragmentCustomerProductBinding
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.ep.adapter.BranchAdapterItem
import com.craft.silicon.centemobile.view.ep.adapter.NameBaseAdapter
import com.craft.silicon.centemobile.view.fragment.go.MovePager
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import kotlinx.parcelize.Parcelize


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
class CustomerProductFragment : Fragment(), AppCallbacks, View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCustomerProductBinding
    private val baseViewModel: BaseViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()

    private val customer = MutableLiveData<String>()
    private val product = MutableLiveData<String>()
    private val branchData = MutableLiveData<String>()

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
        binding = FragmentCustomerProductBinding.inflate(inflater, container, false)
        setBinding()
        getData()
        setOnClick()
        setController()
        setOnClick()
        getBranchData()
        return binding.root.rootView
    }

    private fun getData() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            binding.mainLay.visibility = VISIBLE
            setCustomerType()
            setProductType()
        }, animationDuration.toLong())
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }


    private fun getBranchData() {
        val branch = widgetViewModel.storageDataSource.branchData.value
        if (branch != null) {
            val adapter = BranchAdapterItem(requireContext(), 0, branch.toMutableList())
            binding.branchLay.autoEdit.setAdapter(adapter)

            binding.branchLay.autoEdit.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                    branchData.value = adapter.getItem(p2)?.id
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
                val c = currency?.filter { a -> a?.id == p.id }?.map { it?.relationID }
                if (tab?.tag == i) {
                    product.value = p.description
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

    private fun setCurrencyDropDown(currency: List<String?>?) {
        binding.currencyLay.autoEdit.text.clear()
        val adapter = NameBaseAdapter(requireContext(), 0, currency!!.toMutableList())
        binding.currencyLay.autoEdit.setAdapter(adapter)
    }

    private fun setCustomerType() {


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
        customerType.forEachIndexed { index, type ->
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
                    customer.value = getString(type.type)
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
        binding.toolbar.setNavigationOnClickListener {
            (requireActivity() as MainActivity).provideNavigationGraph().navigateUp()
        }
        binding.materialButton.setOnClickListener(this)
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

    companion object {
        private var movePager: MovePager? = null

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
        fun onStep(movePager: MovePager) = CustomerProductFragment().apply {
            this@Companion.movePager = movePager
        }
    }

    override fun onClick(p: View?) {
        if (p == binding.materialButton) {
            if (validateFields()) {
                val data = CustomerProduct(
                    type = customer.value!!,
                    product = product.value!!,
                    currency = binding.currencyLay.autoEdit.editableText.toString(),
                    branch = branchData.value!!
                )
                movePager?.customerProduct(data = data)
                movePager?.onNext(2)

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
    val type: String,
    @field:SerializedName("type")
    val product: String,
    @field:SerializedName("currency")
    @field:Expose
    val currency: String,
    @field:SerializedName("branch")
    @field:Expose
    val branch: String
) : Parcelable

