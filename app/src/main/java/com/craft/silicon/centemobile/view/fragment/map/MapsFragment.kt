package com.craft.silicon.centemobile.view.fragment.map

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.AtmData
import com.craft.silicon.centemobile.databinding.FragmentMapsBinding
import com.craft.silicon.centemobile.util.ShowToast
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

@AndroidEntryPoint
class MapsFragment : Fragment(), AppCallbacks, OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding
    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private var googleMap: GoogleMap? = null
    private var mapData = MutableLiveData<List<AtmData>>()

    private var atmData = MutableLiveData<List<AtmData>>()
    private var branchData = MutableLiveData<List<AtmData>>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        setBinding()
        setViewModel()
        getMapData()
        setController()
        getData()
        return binding.root.rootView
    }

    private fun getData() {
        val animationDuration = requireContext()
            .resources.getInteger(R.integer.animation_duration)
        Handler(Looper.getMainLooper()).postDelayed({
            val latLng = widgetViewModel.storageDataSource.latLng.value?.latLng
            getAtmData()
            getBranchData()
        }, animationDuration.toLong())
    }

    private fun getBranchData() {
        subscribe.add(
            widgetViewModel.getATMBranch(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNotEmpty()) {
                        branchData.value = it
                    }
                }, { it.printStackTrace() })
        )
    }

    private fun getAtmData() {
        subscribe.add(
            widgetViewModel.getATMBranch(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNotEmpty()) {
                        atmData.value = it
                        setATM()
                    }
                }, { it.printStackTrace() })
        )
    }

//    private fun setBottomSheet() {
//        val coordinator = binding.parent
//        val bottomSheet = binding.bottomSheet
//        val behavior = BottomSheetBehaviorGoogleMapsLike.from(bottomSheet)
//        behavior.state = BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT
//    }


    override fun setBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun getMapData() {
        binding.toggleLay.toggleGroup.check(R.id.atmButton)
        binding.toggleLay.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.atmButton) {
                    setATM()
                } else if (checkedId == R.id.branchButton) {
                    setBranch()
                }
            }
        }

    }

    private fun setBranch() {
        mapData.value = branchData.value
    }

    private fun setATM() {
        mapData.value = atmData.value
    }


    private fun moveCamera(latLng: LatLng, zoom: Float, title: String) {
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom))
        val options = MarkerOptions()
            .position(latLng)
            .title(title)
        googleMap!!.addMarker(options)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mapData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                it.forEach { data ->
                    moveCamera(
                        LatLng(data.latitude!!, data.longitude!!),
                        8f,
                        data.location!!
                    )
                }
            }
        }
    }

    fun setNavigationDialog(data: BranchATMList) {
        val controller = (requireActivity() as MainActivity).provideNavigationGraph()
        if (controller.currentDestination?.id != R.id.mapBottomSheetFragment) {
            MapBottomSheetFragment.setData(data)
            controller.navigate(widgetViewModel.navigation().navigateBottomSheet())
        } else MapBottomSheetFragment.setData(data)
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
