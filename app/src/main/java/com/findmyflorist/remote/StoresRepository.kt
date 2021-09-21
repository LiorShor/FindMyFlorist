package com.findmyflorist.remote

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.findmyflorist.fragments.ICommunicator
import com.findmyflorist.fragments.StoreSearch
import com.findmyflorist.model.SelfLocation
import com.findmyflorist.model.Store
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Exception
import java.util.*
import kotlin.math.log

class StoresRepository {
    private lateinit var mCommunicator: ICommunicator
    private var mStoresList: ArrayList<Store>? = null
    private lateinit var mSelfLocation: SelfLocation
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    val getStoreList: ArrayList<Store>?
        get() = mStoresList

    companion object {
        private var mInstance: StoresRepository? = null

        @Synchronized
        fun getInstance(): StoresRepository? {
            if (mInstance == null) // if instance is not created before
            {
                mInstance = StoresRepository()
            }
            return mInstance
        }
    }

    fun init(activity: ICommunicator, context: Context) {
        mCommunicator = activity
        mStoresList = ArrayList<Store>()
        getUserLocation(context)
    }

    private fun getUserLocation(context: Context) {
        val requestQueue: RequestQueue? =
            context.let { VolleySingleton.getInstance(it)?.requestQueue }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (context.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED)
            mFusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        mSelfLocation = SelfLocation(location.latitude, location.longitude)
                        if (requestQueue != null) {
                            fetchStores(requestQueue)
                        }
                    }
                }
                .addOnFailureListener { exception: Exception ->
                    Log.d("Error", exception.toString())
                }
    }

    private fun fetchStores(requestQueue: RequestQueue) {
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${mSelfLocation.latitudeDistance}, ${mSelfLocation.longitudeDistance}&radius=3000&types=florist&key=AIzaSyABHDVGoOtqs1P1-N_jOYFud-rQH8F0WpM"
        val stringReq = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                var storeIsOpen: String
                val jsonObject = response.getJSONArray("results")
                for (i in 0 until jsonObject.length()) {
                    val storeName = jsonObject.getJSONObject(i).getString("name")
                    val storeAddress = jsonObject.getJSONObject(i).getString("vicinity")
                    val storeID = jsonObject.getJSONObject(i).getString("place_id")
                    storeIsOpen = try {
                        jsonObject.getJSONObject(i).getJSONObject("opening_hours")
                            .getString("open_now")
                    } catch (exception: Exception) {
                        "false"
                    }
                    val storeLatitude =
                        jsonObject.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location")
                            .getString("lat").toDouble()
                    val storeLongitude =
                        jsonObject.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location")
                            .getString("lng").toDouble()
                    val storeDistanceFromUser = StoreSearch.calculateDistanceInKilometer(
                        mSelfLocation.latitudeDistance,
                        mSelfLocation.longitudeDistance,
                        storeLatitude,
                        storeLongitude
                    )
                    val store = Store(
                        storeID,
                        storeName,
                        storeLatitude,
                        storeLongitude,
                        storeAddress,
                        storeIsOpen,
                        storeDistanceFromUser
                    )
                    mStoresList!!.add(store)
                }
                mCommunicator.changeFragmentToStoreSearch()
            }, {
                Log.d("Error", "Could not fetch data from Places API")
            })
        requestQueue.add(stringReq)
    }

    fun fetchStoreDetails(context: Context, storeID: String) {
        var store: Store?
        val url =
            "https://maps.googleapis.com/maps/api/place/details/json?place_id=${storeID}&fields=name%2Cformatted_phone_number%2Copening_hours%2Cphotos%2Cprice_level%2Creviews%2Cwebsite%2Cvicinity%2Cgeometry%2Crating&key=AIzaSyABHDVGoOtqs1P1-N_jOYFud-rQH8F0WpM"
        val requestQueue: RequestQueue? =
            context.let { VolleySingleton.getInstance(it)?.requestQueue }
        val stringReq = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                val jsonObjectResponse = response.getJSONObject("result")
                val storeName = jsonObjectResponse.getString("name")
                val storePhone = jsonObjectResponse.getString("formatted_phone_number")
                var storeWebsite = ""
                var storeAddress = ""
                var storeIsOpen = "Closed"
                var storeRating = 0.0
                try {
                    storeWebsite = jsonObjectResponse.getString("website")
                    storeAddress = jsonObjectResponse.getString("vicinity")
                    storeIsOpen = if (jsonObjectResponse.getJSONObject("opening_hours")
                            .getBoolean("open_now")
                    ) "Open" else "Closed"
                    storeRating = jsonObjectResponse.getDouble("rating")
                } catch (e: Exception) {
                    Log.d("Error", e.toString())
                }
//                val storePricing = jsonObjectResponse.getDouble("???")
                val storeLatitude =
                    jsonObjectResponse.getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat")
                val storeLongitude =
                    jsonObjectResponse.getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng")
                store = Store(
                    storeID,
                    storeName,
                    storeLatitude,
                    storeLongitude,
                    storeAddress,
                    storeIsOpen,
                    0.0,
                    storePhone,
                    false,
                    storeWebsite
                )
                mCommunicator.changeFragmentToStoreDetails(store!!)
            },
            {})
        requestQueue?.add(stringReq)
    }
}
