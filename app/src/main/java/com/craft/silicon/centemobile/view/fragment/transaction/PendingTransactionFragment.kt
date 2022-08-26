package com.craft.silicon.centemobile.view.fragment.transaction

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.source.remote.callback.DynamicResponse
import com.craft.silicon.centemobile.databinding.FragmentPendingTransactionBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
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
 * Use the [PendingTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class PendingTransactionFragment : Fragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var data: Modules? = null

    private val baseViewModel: BaseViewModel by viewModels()
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()

    private lateinit var binding: FragmentPendingTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable(ARG_DATA)
            AppLogger.instance.appLog(
                this@PendingTransactionFragment::class.java.simpleName,
                Gson().toJson(data)
            )
        }
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

    }

    private fun setNoData(b: Boolean) {
        if (b) binding.noData.root.visibility = View.VISIBLE
        else binding.noData.root.visibility = View.GONE

    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPendingTransactionBinding.inflate(inflater, container, false)
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data: Modules
         * @return A new instance of fragment PendingTransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: Modules) =
            PendingTransactionFragment().apply {
                arguments = Bundle().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_DATA, data)
                    }
                }
            }
    }
}