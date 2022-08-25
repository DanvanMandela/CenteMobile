package com.craft.silicon.centemobile.view.fragment.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.AtmData
import com.craft.silicon.centemobile.data.source.constants.Constants
import com.craft.silicon.centemobile.databinding.FragmentAtmBinding
import com.craft.silicon.centemobile.util.AppLogger
import com.craft.silicon.centemobile.util.callbacks.AppCallbacks
import com.craft.silicon.centemobile.view.activity.MainActivity
import com.craft.silicon.centemobile.view.model.WidgetViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
 * Use the [AtmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class AtmFragment : Fragment(), AppCallbacks, OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val widgetViewModel: WidgetViewModel by viewModels()
    private val subscribe = CompositeDisposable()
    private var googleMap: GoogleMap? = null
    private var fusedLocationProvider: FusedLocationProviderClient? = null

    private lateinit var binding: FragmentAtmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        fusedLocationProvider =
            LocationServices.getFusedLocationProviderClient(requireActivity() as MainActivity)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAtmBinding.inflate(inflater, container, false)
        return binding.root.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun getAtmData() {
        subscribe.add(
            widgetViewModel.getATMBranch(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isNotEmpty()) {
                        setMarkers(it)
                    }
                }, { it.printStackTrace() })
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AtmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AtmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMapReady(p: GoogleMap) {
        this.googleMap = p
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap?.isMyLocationEnabled = true
        getAtmData()
        getCurrentLocation()
    }


    private fun setMarkers(it: List<AtmData>) {
        it.forEach { lg ->
            googleMap?.addMarker(
                MarkerOptions().position(
                    LatLng(lg.latitude!!, lg.longitude!!)
                ).title(lg.location)
            )
        }

    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest: LocationRequest = LocationRequest.create().apply {
                interval = Constants.MapsConstants.interval
                fastestInterval = Constants.MapsConstants.fastestInterval
                priority = Priority.PRIORITY_HIGH_ACCURACY
                maxWaitTime = Constants.MapsConstants.maxWaitTime
            }
            val locationCallback: LocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val locationList = locationResult.locations
                    if (locationList.isNotEmpty()) {
                        val location = locationList.last()
                        AppLogger.instance.appLog(
                            "USER:LOCATION",
                            "lat:${location.latitude} lng:${location.longitude}"
                        )
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLng(
                                LatLng(location.latitude, location.longitude)
                            )
                        )
                        googleMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                15.1f
                            )
                        )
                    }
                }
            }
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }


    }
}