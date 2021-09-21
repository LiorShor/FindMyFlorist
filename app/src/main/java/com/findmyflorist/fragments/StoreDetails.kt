package com.findmyflorist.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.findmyflorist.R
import com.findmyflorist.databinding.FragmentStoreDetailsBinding
import com.findmyflorist.model.Store
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri


class StoreDetails : Fragment(), OnMapReadyCallback {
    private lateinit var mBinding: FragmentStoreDetailsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var store: Store
    private lateinit var communicator: ICommunicator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = arguments
        if (bundle != null) {
            store = (bundle.getSerializable("Store") as? Store)!!
        }

        mBinding = FragmentStoreDetailsBinding.inflate(inflater, container, false)
//        val map = requireFragmentManager().findFragmentById(R.id.map) as SupportMapFragment?
        inflater.inflate(R.layout.fragment_store_details, container, false)
        communicator = activity as ICommunicator
//        val mapFragment = mBinding.map as SupportMapFragment
//        val mapFragment = requireFragmentManager().findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(this)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateFavorite()
        mBinding.storeTitle.text = store.storeName
        mBinding.mapView.getMapAsync(OnMapReadyCallback { googleMap ->
            val coordinates = LatLng(store.storeLatitude, store.storeLongitude)
            googleMap.addMarker(
                MarkerOptions().position(coordinates).title(store.Address)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
            mBinding.mapView.onResume()
        })
        mBinding.wazeButton.setOnClickListener {
            communicator.openWaze(store.storeLatitude, store.storeLongitude)
        }
        mBinding.websiteButton.setOnClickListener {
            communicator.openWebsite(store.website)
        }
        mBinding.favoriteButton.setOnClickListener {
            store.isFavorite = !store.isFavorite
            updateFavorite()
        }
        mBinding.phoneButton.setOnClickListener {
            if (context?.let { it1 ->
                    ContextCompat.checkSelfPermission(
                        it1,
                        android.Manifest.permission.CALL_PHONE
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf(android.Manifest.permission.CALL_PHONE),
                    101
                )

            } else {
// else block means user has already accepted.And make your phone call here.
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + store.phone))
                startActivity(intent)
            }
        }
        mBinding.mapView.onCreate(savedInstanceState)

    }

    private fun updateFavorite(){
        if (store.isFavorite) {
            mBinding.favoriteButton.setImageResource(R.drawable.ic_in_favorite)
        } else {
            mBinding.favoriteButton.setImageResource(R.drawable.ic_not_in_favorite)
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    companion object
}