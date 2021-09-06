package com.findmyflorist.fragments

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.findmyflorist.R
import com.findmyflorist.VolleySingleton
import com.findmyflorist.activities.MainActivity
import com.findmyflorist.databinding.FragmentStoreSearchBinding
import org.json.JSONObject
import androidx.recyclerview.widget.ItemTouchHelper

import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.findmyflorist.adapters.StoreAdapter
import com.findmyflorist.model.Store


class StoreSearch : Fragment() {
    private lateinit var binding: FragmentStoreSearchBinding
    private lateinit var storesList: ArrayList<Store>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.fragment_store_search, container, false)
        binding = FragmentStoreSearchBinding.inflate(inflater, container, false)
        //communicator = activity as ICommunicator TODO: check if needed
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        }
    private fun setUpRecyclerView() {
        binding.storesRecyclerView.adapter = StoreAdapter(storesList)
        binding.storesRecyclerView.layoutManager = LinearLayoutManager(context)
        //val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(taskAdapter, getContext()))
        //itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}

    private fun fetchStores(selfLocation: Location, context: Context) {
        val userCredentialsJSON = JSONObject()
        val requestQueue: RequestQueue? = VolleySingleton.getInstance(context)?.requestQueue
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${selfLocation.latitude}, ${selfLocation.longitude}&radius=500&types=florist&key=AIzaSyABHDVGoOtqs1P1-N_jOYFud-rQH8F0WpM"
        val stringReq = JsonObjectRequest(
            Request.Method.GET, url, userCredentialsJSON, { response ->
                Log.d("VolleySucceedSignIn", response.toString())
                val jsonObject = response.getJSONArray("results")
                //TODO add stores to storesList
                Log.d("VolleySucceedSignIn", jsonObject.getJSONObject(0).getString("name"))
            }, {
                Log.d("Error", "signIn: ")
            })
        requestQueue?.add(stringReq)
    }

    companion object {
    }
}