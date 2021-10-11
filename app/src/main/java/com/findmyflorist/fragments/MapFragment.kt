package com.findmyflorist.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.findmyflorist.databinding.FragmentMapBinding
import com.findmyflorist.model.Store
import com.findmyflorist.remote.StoresRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {
    private lateinit var mBinding: FragmentMapBinding
    private var storeLatitude: Double = 0.0
    private var storeLongitude: Double = 0.0
    private val mStoreList : ArrayList<Store> = StoresRepository.getInstance()?.getStoreList!!

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
        if(storeLatitude == 0.0 && storeLongitude == 0.0) {
            mBinding.mapView.getMapAsync { googleMap ->
                for (i in 0 until mStoreList.size) {
                    val store = mStoreList[i]
                    val coordinates = LatLng(store.Latitude, store.Longitude)
                    if(store.isFavorite)
                    googleMap.addMarker(
                        MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).position(coordinates).title(store.storeName)
                    )
                    else{
                        googleMap.addMarker(
                            MarkerOptions().position(coordinates).title(store.storeName))
                    }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 10f))
                    mBinding.mapView.onResume()
                }
            }
        }
            else {
            mBinding.mapView.getMapAsync { googleMap ->
                val coordinates = LatLng(storeLatitude, storeLongitude)
                googleMap.addMarker(
                    MarkerOptions().position(coordinates).title("Address")
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
                mBinding.mapView.onResume()
            }
        }
        mBinding.mapView.onCreate(savedInstanceState)
    }
}