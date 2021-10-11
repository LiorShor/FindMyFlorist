package com.findmyflorist.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.findmyflorist.R
import com.findmyflorist.databinding.FragmentStoreSearchBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.findmyflorist.adapters.StoreAdapter
import com.findmyflorist.dialogs.Search
import com.findmyflorist.remote.StoresRepository
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

class StoreSearch : Fragment() {
    private lateinit var mCommunicator: ICommunicator
    private lateinit var mBinding: FragmentStoreSearchBinding
    private lateinit var storeAdapter: StoreAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inflater.inflate(R.layout.fragment_store_search, container, false)
        mBinding = FragmentStoreSearchBinding.inflate(inflater, container, false)
        mCommunicator = activity as ICommunicator
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storeAdapter = StoresRepository.getInstance()?.getStoreList?.let {
            context?.let { context ->
                StoreAdapter(
                    it,
                    context
                )
            }
        }!!
        mBinding.storesRecyclerView.adapter = storeAdapter
        mBinding.storesRecyclerView.layoutManager = LinearLayoutManager(context)
        mBinding.mapViewFloatingButton.setOnClickListener {
            mCommunicator.changeFragmentToMapFragment(
                LatLng(0.0, 0.0)
            )
        }
        mBinding.dropdownSearch.setItemClickListener { position, _ ->
            if (position == 1) {
                context?.let { Search(it) }
            } else {
                StoresRepository.getInstance()?.getUserLocation(requireContext())
            }
        }
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
            return (sqrt(distance) / 1000).roundToInt() / 100.0
        }
    }
}