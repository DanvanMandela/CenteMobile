package com.craft.silicon.centemobile.view.fragment.beneficiary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.user.Beneficiary
import com.craft.silicon.centemobile.databinding.FragmentBeneficiaryManagementBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.adapter.TransactionAdapterItem
import com.craft.silicon.centemobile.view.ep.controller.BeneficiaryList
import com.craft.silicon.centemobile.view.ep.controller.MainDisplayController
import com.craft.silicon.centemobile.view.ep.data.Nothing
import com.craft.silicon.centemobile.view.model.BaseViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DATA = "module"

/**
 * A simple [Fragment] subclass.
 * Use the [BeneficiaryManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BeneficiaryManagementFragment : Fragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var data: Modules? = null
    private val baseViewModel: BaseViewModel by viewModels()

    private val subscribe = CompositeDisposable()
    private lateinit var adapter: TransactionAdapterItem

    private lateinit var controller: MainDisplayController

    private lateinit var binding: FragmentBeneficiaryManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable(ARG_DATA)
            AppLogger.instance.appLog(
                this@BeneficiaryManagementFragment::class.java.simpleName,
                Gson().toJson(data)
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBeneficiaryManagementBinding.inflate(inflater, container, false)
        setController()
        setBinding()
        setToolbar()
        return binding.root.rootView
    }

    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner

        binding.callback = this
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            stopShimmer()
            setData()
        }, animationDuration.toLong())

    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    private fun setData() {
        binding.container.setController(controller)
    }
    
    private fun setToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        if (data != null) {
            binding.toolbar.title = data?.moduleName
        }

    }

    override fun deleteBeneficiary(modules: Modules?, beneficiary: Beneficiary?) {

    }

    override fun setController() {
        controller = MainDisplayController(this)
        val staticData = baseViewModel.dataSource.beneficiary.asLiveData()
        staticData.observe(viewLifecycleOwner) {
            setData(it)
        }

    }

    private fun setData(it: List<Beneficiary?>?) {
        if (it!!.isNotEmpty()) controller.setData(Nothing())
        else controller.setData(BeneficiaryList(list = it.toMutableList(), module = data))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param data: Modules
         * @return A new instance of fragment BeneficiaryManagementFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: Modules) =
            BeneficiaryManagementFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, data)
                }
            }
    }
}

