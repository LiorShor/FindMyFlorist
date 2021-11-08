package com.findmyflorist.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.findmyflorist.R
import com.findmyflorist.databinding.FragmentMapBinding
import com.findmyflorist.model.Store
import com.findmyflorist.remote.StoresRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
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
                        MarkerOptions().icon(context?.let { bitmapDescriptorFromVector(it,R.drawable.ic_flower_favorite) }).position(coordinates).title(store.storeName)
                    )
                    else{
                        googleMap.addMarker(
                            MarkerOptions().icon(context?.let { bitmapDescriptorFromVector(it,R.drawable.ic_florist) }).position(coordinates).title(store.storeName))
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
                    MarkerOptions().icon(context?.let { bitmapDescriptorFromVector(it,R.drawable.ic_florist) }).position(coordinates).title("Address")
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
                mBinding.mapView.onResume()
            }
        }
        mBinding.mapView.onCreate(savedInstanceState)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}