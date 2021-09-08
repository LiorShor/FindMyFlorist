package com.findmyflorist.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.findmyflorist.R
import com.findmyflorist.VolleySingleton
import com.findmyflorist.databinding.FragmentStoreSearchBinding
import org.json.JSONObject
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.textrecognition.view.fragments.ICommunicator
import com.findmyflorist.adapters.StoreAdapter
import com.findmyflorist.model.SelfLocation
import com.findmyflorist.model.Store
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Exception
import kotlin.math.*

class StoreSearch : Fragment() {
    private lateinit var communicator: ICommunicator
    private lateinit var binding: FragmentStoreSearchBinding
    private lateinit var storesList: ArrayList<Store>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var selfLocation: SelfLocation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        selfLocation = SelfLocation(location.latitude, location.longitude)
                        //fetchStores(selfLocation) TODO: Uncomment when app ready (DELETE the two next lines)
                        storesList = ArrayList()
                        storesList.add(
                            Store(
                                "DUMMY",
                                "Store name",
                                31.98714019112028,
                                34.77956492529882,
                                "Store address",
                                "Closed",
                                140.0,
                                "039622225",
                                false
                            )
                        )
                    }
                }.addOnFailureListener { exception: Exception ->
                    Log.d("Error", exception.toString())
                }
        inflater.inflate(R.layout.fragment_store_search, container, false)
        binding = FragmentStoreSearchBinding.inflate(inflater, container, false)
        communicator = activity as ICommunicator
        return binding.root
    }

    private fun fetchStores(selfLocation: SelfLocation) {
        storesList = ArrayList()
        val userCredentialsJSON = JSONObject()
        val requestQueue: RequestQueue? =
            context?.let { VolleySingleton.getInstance(it)?.requestQueue }
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${selfLocation.latitudeDistance}, ${selfLocation.longitudeDistance}&radius=3000&types=florist&key=AIzaSyABHDVGoOtqs1P1-N_jOYFud-rQH8F0WpM"
        val stringReq = JsonObjectRequest(
            Request.Method.GET, url, userCredentialsJSON, { response ->
                var storeIsOpen: String
                val jsonObject = response.getJSONArray("results")
                for (i in 0 until jsonObject.length()) {
                    val storeName = jsonObject.getJSONObject(i).getString("name")
                    val storeAddress = jsonObject.getJSONObject(i).getString("vicinity")
                    val storeID = jsonObject.getJSONObject(i).getString("place_id")
                    try {
                        storeIsOpen =
                            jsonObject.getJSONObject(i).getJSONObject("opening_hours")
                                .getString("open_now")
                    } catch (exception: Exception) {
                        storeIsOpen = "false"
                    }
                    val storeLatitude =
                        jsonObject.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location")
                            .getString("lat").toDouble()
                    val storeLongitude =
                        jsonObject.getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location")
                            .getString("lng").toDouble()
                    val storeDistanceFromUser = calculateDistanceInKilometer(
                        selfLocation.latitudeDistance,
                        selfLocation.longitudeDistance,
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
                    storesList.add(store)
                }
                setUpRecyclerView()
            }, {
                Log.d("Error", "Could not fetch data from Places API")
            })
        requestQueue?.add(stringReq)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((context?.let {
                            ContextCompat.checkSelfPermission(
                                it, Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        } == PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.storesRecyclerView.adapter = StoreAdapter(storesList)
        binding.storesRecyclerView.layoutManager = LinearLayoutManager(context)
        //val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(taskAdapter, getContext()))
        //itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    companion object {
        private const val AVERAGE_RADIUS_OF_EARTH_KM = 6371.0
        fun calculateDistanceInKilometer(
            lat1: Double, lat2: Double, lon1: Double,
            lon2: Double
        ): Double {
            val latDistance = Math.toRadians(lat2 - lat1)
            val lonDistance = Math.toRadians(lon2 - lon1)
            val a = (sin(latDistance / 2) * sin(latDistance / 2)
                    + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
                    * sin(lonDistance / 2) * sin(lonDistance / 2)))
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            var distance = AVERAGE_RADIUS_OF_EARTH_KM * c * 1000 // convert to meters
            distance = distance.pow(2.0)
            return (sqrt(distance) / 1000 * 100.0).roundToInt() / 100.0
        }
    }
}