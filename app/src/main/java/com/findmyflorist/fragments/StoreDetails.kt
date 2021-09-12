package com.findmyflorist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Transformations.map
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.textrecognition.view.fragments.ICommunicator
import com.findmyflorist.R
import com.findmyflorist.VolleySingleton
import com.findmyflorist.databinding.FragmentStoreDetailsBinding
import com.findmyflorist.databinding.FragmentStoreSearchBinding
import com.findmyflorist.model.Store
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class StoreDetails : Fragment(), OnMapReadyCallback {
    private lateinit var mBinding: FragmentStoreDetailsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var storeID : String
    private lateinit var store : Store
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = arguments
        if(bundle != null){
            storeID = bundle.getString("STORE_ID").toString()
        }
//        val map = requireFragmentManager().findFragmentById(R.id.map) as SupportMapFragment?
        inflater.inflate(R.layout.fragment_store_details, container, false)
        mBinding = FragmentStoreDetailsBinding.inflate(inflater, container, false)
//        val mapFragment = mBinding.map as SupportMapFragment
//        val mapFragment = requireFragmentManager().findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(this)
        return mBinding.root
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

    private fun fetchStoreData() {
        val url =
            "https://maps.googleapis.com/maps/api/place/details/json?place_id=${store.storeID}&fields=name%2Crating%2Cformatted_phone_number&key=AIzaSyABHDVGoOtqs1P1-N_jOYFud-rQH8F0WpM"
        val requestQueue: RequestQueue? =
            context?.let { VolleySingleton.getInstance(it)?.requestQueue }
        val stringReq = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                val jsonObjectResponse = response.getJSONObject("result")
                val storeName = jsonObjectResponse.getString("name")
                val storePhone = jsonObjectResponse.getString("formatted_phone_number")
                val storeWebsite = jsonObjectResponse.getString("website")
                val storeAddress = jsonObjectResponse.getString("vicinity")
                val storeIsOpen : String = if(jsonObjectResponse.getBoolean("open_now")) "Open" else "Closed"
                val storeRating = jsonObjectResponse.getDouble("rating")
                val storeLatitude = jsonObjectResponse.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                val storeLongitude = jsonObjectResponse.getJSONObject("geometry").getJSONObject("location").getDouble("lng")
                store = Store(storeID,storeName,storeLatitude,storeLongitude,storeAddress,storeIsOpen,0.0,storePhone)
            }, {})
        requestQueue?.add(stringReq)
    }

    companion object
}