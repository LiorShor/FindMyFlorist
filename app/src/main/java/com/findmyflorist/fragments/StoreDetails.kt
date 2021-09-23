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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.findmyflorist.remote.StoresRepository

class StoreDetails : Fragment() {
    private lateinit var mBinding: FragmentStoreDetailsBinding
    private lateinit var store: Store
    private lateinit var communicator: ICommunicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = arguments
        if (bundle != null) {
            store = (bundle.getSerializable("Store") as? Store)!!
        }

        mBinding = FragmentStoreDetailsBinding.inflate(inflater, container, false)
//        inflater.inflate(R.layout.fragment_store_details, container, false)
        communicator = activity as ICommunicator
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateFavoriteUI()
        mBinding.storeTitle.text = store.storeName
        mBinding.mapView.getMapAsync { googleMap ->
            val coordinates = LatLng(store.Latitude, store.Longitude)
            googleMap.addMarker(
                MarkerOptions().position(coordinates).title(store.Address)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15f))
            googleMap.setOnMapClickListener {
                val latLng = LatLng(store.Latitude, store.Longitude)
                communicator.changeFragmentToMapFragment(latLng)
            }
            mBinding.mapView.onResume()
        }
        mBinding.wazeButton.setOnClickListener {
            communicator.openWaze(store.Latitude, store.Longitude)
        }
        mBinding.websiteButton.setOnClickListener {
            communicator.openWebsite(store.website)
        }
        mBinding.favoriteButton.setOnClickListener {
            store.isFavorite = !store.isFavorite
            StoresRepository.getInstance()?.getStoreList?.stream()?.forEach { storeFromStoresList -> if(storeFromStoresList.storeID== store.storeID)
            {
                storeFromStoresList.isFavorite = store.isFavorite
            }}
            updateFavoriteUI()
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

    private fun updateFavoriteUI() {
        if (store.isFavorite) {
            mBinding.favoriteButton.setImageResource(R.drawable.ic_in_favorite)
        } else {
            mBinding.favoriteButton.setImageResource(R.drawable.ic_not_in_favorite)
        }
    }

    companion object
}