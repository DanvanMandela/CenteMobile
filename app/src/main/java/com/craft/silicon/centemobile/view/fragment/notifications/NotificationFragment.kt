package com.craft.silicon.centemobile.view.fragment.notifications

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.receiver.NotificationData
import com.craft.silicon.centemobile.databinding.FragmentNotificationBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.ep.adapter.NotificationAdapterItem
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class NotificationFragment : Fragment(), AppCallbacks {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentNotificationBinding
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val disposable = CompositeDisposable()

    private lateinit var adapter: NotificationAdapterItem

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
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        setBinding()
        setToolbar()
        setController()
        getData()
        return binding.root.rootView
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
            setOnMenuItemClickListener { menu ->
                when (menu.itemId) {
                    R.id.deleteNotifications -> {
                        widgetViewModel.deleteNotifications()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun getData() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            disposable.add(
                widgetViewModel.notification
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        stopShimmer()
                        setData(it)
                        setNoData(it.isEmpty())
                    }, { it.printStackTrace() })
            )
        }, animationDuration.toLong())


    }

    private fun setNoData(empty: Boolean) {
        if (empty)
            binding.noData.root.visibility = VISIBLE
        else binding.noData.root.visibility = GONE
    }

    override fun deleteNotification(data: NotificationData?) {
        widgetViewModel.deleteNotification(data!!.id)
    }

    private fun setData(it: List<NotificationData>) {
        adapter.replaceData(it.toMutableList())
    }

    private fun stopShimmer() {
        binding.shimmerContainer.stopShimmer()
        binding.shimmerContainer.visibility = View.GONE
    }

    override fun setBinding() {
        binding.lifecycleOwner = this

    }

    override fun setController() {
        adapter = NotificationAdapterItem(mutableListOf(), this)
        binding.container.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}