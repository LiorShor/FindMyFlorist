package com.findmyflorist.fragments

import com.findmyflorist.model.Store
import com.google.android.gms.maps.model.LatLng

interface ICommunicator {
    fun changeFragmentWithData(
        userName: String
    )
    fun changeFragmentToMapFragment(latLng : LatLng)
    fun changeFragmentToStoreSearch()
    fun changeFragmentToFavorites()
    fun changeFragmentToAboutUs()
    fun changeFragmentToStoreDetails(store: Store)
    fun openWaze(latitude: Double, longitude: Double)
    fun openWebsite(url : String)

}