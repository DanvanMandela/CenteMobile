package com.craft.silicon.centemobile.view.fragment.map

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.craft.silicon.centemobile.data.model.AtmData
import com.craft.silicon.centemobile.databinding.FragmentMapsBinding
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.maps.android.clustering.ClusterItem
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment(), AppCallbacks {

    private lateinit var binding: FragmentMapsBinding

    private var adapter: MapFragmentAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        setBinding()
        setViewPager()
        setClicks()
        requestPermissions()
        return binding.root.rootView
    }

    private fun setClicks() {
        binding.toggleLay.atmButton.setOnClickListener {
            binding.container.setCurrentItem(1, true)
        }
        binding.toggleLay.branchButton.setOnClickListener {
            binding.container.setCurrentItem(0, true)
        }
    }

    private fun setViewPager() {
        adapter =
            MapFragmentAdapter(
                requireActivity().supportFragmentManager,
                lifecycle
            )
        binding.container.adapter = adapter
        binding.container.isUserInputEnabled = false
    }


    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun requestPermissions() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {

                    }
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        (requireActivity() as MainActivity).requestLocationPermission()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {

            }
            .onSameThread().check()
    }


    inner class MyItem(
        lat: Double,
        lng: Double,
        title: String,
        snippet: String
    ) : ClusterItem {

        private val position: LatLng
        private val title: String
        private val snippet: String

        override fun getPosition(): LatLng {
            return position
        }

        override fun getTitle(): String {
            return title
        }

        override fun getSnippet(): String {
            return snippet
        }

        init {
            position = LatLng(lat, lng)
            this.title = title
            this.snippet = snippet
        }
    }

}

class MapFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
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
            0 -> BranchFragment()
            1 -> AtmFragment()
            else -> {
                throw Exception("Not implemented")
            }
        }
    }


    companion object {
        private const val TABS = 2
    }
}


enum class BranchAtm {
    ATM, BRANCH
}


data class MapData(
    @field:SerializedName("latLng")
    @field:Expose
    var latLng: LatLng
)

data class BranchATMData(
    val data: AtmData?,
    val latLng: LatLng?,
)

data class BranchATMList(val list: MutableList<BranchATMData>)


