package com.findmyflorist.fragments

import com.findmyflorist.model.Store

interface ICommunicator {
    fun changeFragmentWithData(
        userName: String
    )

    fun changeFragmentToStoreSearch()
    fun changeFragmentToFavorites()
    fun changeFragmentToAboutUs()
    fun changeFragmentToStoreDetails(store: Store)
    fun openWaze(latitude: Double, longitude: Double)
    fun openWebsite(url : String)

}