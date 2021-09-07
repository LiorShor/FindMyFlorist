package com.findmyflorist.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.findmyflorist.R
import com.findmyflorist.VolleySingleton
import com.findmyflorist.databinding.FragmentStoreSearchBinding
import org.json.JSONObject
import androidx.recyclerview.widget.LinearLayoutManager
import com.findmyflorist.adapters.StoreAdapter
import com.findmyflorist.model.SelfLocation
import com.findmyflorist.model.Store
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Exception

class StoreSearch : Fragment() {
    private lateinit var binding: FragmentStoreSearchBinding

    // private lateinit var storesList: ArrayList<Store>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var selfLocation: SelfLocation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
        fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if (location != null) {
                            selfLocation = SelfLocation(location.latitude,location.longitude)
                            fetchStores(selfLocation)
                        }
                    }.addOnFailureListener { e : Exception ->
                        Log.d("error", "onCreateView: "+ e)
                    }
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_store_search, container, false)
        binding = FragmentStoreSearchBinding.inflate(inflater, container, false)
        //communicator = activity as ICommunicator TODO: check if needed
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setUpRecyclerView()

    }

    private fun fetchStores(selfLocation: SelfLocation) {
        val userCredentialsJSON = JSONObject()
        val requestQueue: RequestQueue? = context?.let { VolleySingleton.getInstance(it)?.requestQueue }
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${selfLocation.latitudeDistance}, ${selfLocation.longitudeDistance}&radius=3000&types=florist&key=AIzaSyABHDVGoOtqs1P1-N_jOYFud-rQH8F0WpM"
        val stringReq = JsonObjectRequest(
            Request.Method.GET, url, userCredentialsJSON, { response ->
                Log.d("VolleySucceedSignIn", response.toString())
                val jsonObject = response.getJSONArray("results")
                //TODO add stores to storesList
                val storeName = jsonObject.getJSONObject(0).getString("name")
                val storeAddress = jsonObject.getJSONObject(0).getString("vicinity")
                val storeID = jsonObject.getJSONObject(0).getString("place_id")
                val storeIsOpen = jsonObject.getJSONObject(0).getJSONObject("opening_hours").getString("open_now")
                //TODO take store location
                Log.d("VolleySucceedSignIn", jsonObject.getJSONObject(0).getString("name"))
            }, {
                Log.d("Error", "signIn: ")
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
        //     binding.storesRecyclerView.adapter = StoreAdapter(storesList)
        binding.storesRecyclerView.layoutManager = LinearLayoutManager(context)
        //val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(taskAdapter, getContext()))
        //itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    companion object {
    }
}