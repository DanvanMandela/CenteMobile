package com.craft.silicon.centemobile.view.fragment.transaction

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.converter.DynamicAPIResponseConverter
import com.craft.silicon.centemobile.data.model.converter.GlobalResponseTypeConverter
import com.craft.silicon.centemobile.data.model.dynamic.TransactionData
import com.craft.silicon.centemobile.data.model.dynamic.TransactionResponseResponseConverter
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.source.constants.StatusEnum
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse
import com.craft.silicon.centemobile.databinding.FragmentTransactionCenterBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.BaseClass
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.binding.navigate
import com.craft.silicon.centemobile.view.binding.setDynamic
import com.craft.silicon.centemobile.view.dialog.AlertDialogFragment
import com.craft.silicon.centemobile.view.dialog.DialogData
import com.craft.silicon.centemobile.view.dialog.InfoFragment
import com.craft.silicon.centemobile.view.dialog.SuccessDialogFragment
import com.craft.silicon.centemobile.view.dialog.display.DisplayDialogFragment
import com.craft.silicon.centemobile.view.dialog.receipt.ReceiptFragment
import com.craft.silicon.centemobile.view.ep.adapter.TransactionAdapterItem
import com.craft.silicon.centemobile.view.ep.data.DynamicData
import com.craft.silicon.centemobile.view.ep.data.ReceiptList
import com.craft.silicon.centemobile.view.fragment.global.GlobalOTPFragment
import com.craft.silicon.centemobile.view.fragment.levels.LevelOneFragment
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "module"


/**
 * A simple [Fragment] subclass.
 * Use the [TransactionCenterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TransactionCenterFragment : Fragment(), AppCallbacks {
    private var data: Modules? = null
    private lateinit var binding: FragmentTransactionCenterBinding
    private val baseViewModel: BaseViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private lateinit var adapter: TransactionAdapterItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable(ARG_DATA)
            AppLogger.instance.appLog(
                this@TransactionCenterFragment::class.java.simpleName,
                Gson().toJson(data)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionCenterBinding.inflate(inflater, container, false)
        setController()
        setBinding()
        setToolbar()
        return binding.root.rootView
    }

    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

    }

    override fun setController() {
        adapter = TransactionAdapterItem(mutableListOf(), this)
        binding.container.adapter = adapter
    }


    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.toolbar.title = data?.moduleName

        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            fetchTransaction()
        }, animationDuration.toLong())
    }

    private fun fetchTransaction() {
        subscribe.add(
            baseViewModel.transactionCenter(data, requireContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    stopShimmer()
                    onData(it)
                }, { it.printStackTrace() })
        )
    }

    private fun onData(it: DynamicResponse?) {

        try {
            AppLogger.instance.appLog(
                "${TransactionCenterFragment::class.java.simpleName}:D:Transaction",
                BaseClass.decryptLatest(
                    it?.response,
                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                    true,
                    widgetViewModel.storageDataSource.deviceData.value!!.run
                )
            )
            AppLogger.instance.logTXT(
                BaseClass.decryptLatest(
                    it?.response,
                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                    true,
                    widgetViewModel.storageDataSource.deviceData.value!!.run
                ), requireActivity()
            )
            val response = TransactionResponseResponseConverter().to(
                BaseClass.decryptLatest(
                    it?.response,
                    widgetViewModel.storageDataSource.deviceData.value!!.device,
                    true,
                    widgetViewModel.storageDataSource.deviceData.value!!.run
                )
            )
            if (BaseClass.nonCaps(it?.response) != StatusEnum.ERROR.type) {
                if (BaseClass.nonCaps(response?.status)
                    == BaseClass.nonCaps(StatusEnum.SUCCESS.type)
                ) {
                    setNoData(response?.data.isNullOrEmpty())
                    adapter.replaceData(response?.data!!)
                } else if (BaseClass.nonCaps(response?.status)
                    == StatusEnum.TOKEN.type
                ) {
                    InfoFragment.showDialog(this.childFragmentManager)
                }
            } else {
                ShowToast(requireContext(), getString(R.string.error_fetching_transactions))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setNoData(b: Boolean) {
        if (b) binding.noData.root.visibility = VISIBLE
        else binding.noData.root.visibility = GONE

    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = GONE
    }

    private fun startShimmer() {
        binding.shimmerContainer.startShimmer()
    }

    override fun navigateUp() {
        requireActivity().onBackPressed()
    }


    override fun onTransactionDetails(data: TransactionData?) {
        navigate(widgetViewModel.navigation().navigateToTransactionDetails(data))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionCenterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: Modules) =
            TransactionCenterFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, data)
                }
            }
    }
}