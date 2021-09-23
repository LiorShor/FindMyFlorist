package com.findmyflorist.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.findmyflorist.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {
    private lateinit var mBinding: FragmentMapBinding
    private var storeLatitude: Double = 0.0
    private var storeLongitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = arguments
        if (bundle != null) {
            storeLatitude = bundle.getDouble("lat")
            storeLongitude = bundle.getDouble("lon")
        }
        mBinding = FragmentMapBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.mapView.getMapAsync { googleMap ->
            val coordinates = LatLng(storeLatitude, storeLongitude)
            googleMap.addMarker(
                MarkerOptions().position(coordinates).title("Address")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
            mBinding.mapView.onResume()
        }
        mBinding.mapView.onCreate(savedInstanceState)
    }
}