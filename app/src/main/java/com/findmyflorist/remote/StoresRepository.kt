package com.findmyflorist.remote

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.findmyflorist.CustomJsonArrayRequest
import com.findmyflorist.activities.MainActivity.Companion.USERID
import com.findmyflorist.activities.MainActivity.Companion.user
import com.findmyflorist.fragments.IAdapterListener
import com.findmyflorist.fragments.ICommunicator
import com.findmyflorist.fragments.StoreSearch
import com.findmyflorist.model.SelfLocation
import com.findmyflorist.model.Store
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.util.stream.Collectors
import kotlin.collections.ArrayList

class StoresRepository {
    private lateinit var mCommunicator: ICommunicator
    internal lateinit var adapterListener: IAdapterListener
    private var mStoresList: ArrayList<Store>? = null
    private lateinit var userID: String
    private var mFavoriteStoresList: ArrayList<String>? = null
    private lateinit var mSelfLocation: SelfLocation
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var requestQueue: RequestQueue
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
        mStoresList = ArrayList()
        getUserLocation(context)
        requestQueue = VolleySingleton.getInstance(context)?.requestQueue!!
    }

    fun setAdapter(adapter: IAdapterListener) {
        adapterListener = adapter
    }

    fun getUserLocation(context: Context) {
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
                            fetchStores()
                        }
                    }
                }
                .addOnFailureListener { exception: Exception ->
                    Log.d("Error", exception.toString())
                }
    }

    private fun isExist(): Boolean {
        var isExist = false
        for (i in 0 until mFavoriteStoresList!!.size) {
            for (j in 0 until mStoresList!!.size) {
                if (mStoresList!![j].storeID == mFavoriteStoresList!![i]) {
                    mStoresList!![j].isFavorite = true
                    isExist = true
                }
            }
        }
        return isExist
    }

    fun getLatLonByAddress(queryText: String) {
        mStoresList?.clear()
        val url =
            "http://api.positionstack.com/v1/forward?access_key=0584242924622fa92941238c9c2d23b7&query=${queryText}"
        val stringReq = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
            try{
                mSelfLocation.latitude = response.getJSONArray("data").getJSONObject(0).getDouble("latitude")
                mSelfLocation.longitude = response.getJSONArray("data").getJSONObject(0).getDouble("longitude")
                fetchStores()
            }catch (exception: Exception) {
                Log.d("Error", "getLatLonByAddress: could not find latitude or longitude for $queryText")
            }
            }, {
                Log.d("Error", "Could not fetch Lat lon by address")
            })
        requestQueue.add(stringReq)
    }

    private fun fetchStores() {
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${mSelfLocation.latitude}, ${mSelfLocation.longitude}&radius=3000&types=florist&key=AIzaSyABHDVGoOtqs1P1-N_jOYFud-rQH8F0WpM"
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
                        mSelfLocation.latitude,
                        mSelfLocation.longitude,
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
                addStoresToDatabase(requestQueue)
                mCommunicator.changeFragmentToStoreSearch()
            }, {
                Log.d("Error", "Could not fetch data from Places API")
            })
        requestQueue.add(stringReq)
    }

    private fun addStoresToDatabase(requestQueue: RequestQueue) {
        val storeAsString = mStoresList?.stream()?.map { store -> store.storeID }?.collect(
            Collectors.toList()
        )
        val json = Gson().toJson(storeAsString)
        val jsonParam = JSONArray(json)
        val url = "http://192.168.1.20:45455/api/Store/AddNewStoreByID"
        val stringReq = JsonArrayRequest(
            Request.Method.POST, url, jsonParam, { response ->
                Log.d("AddStoresToDB", response.toString())
            }, {
                Log.d("Error", "signIn: check if server is up")
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
                var storeWebsite = ""
                var storeAddress = ""
                var storePhone = ""
                var storeIsOpen = "Closed"
                var storeRating = 0.0
                try {
                    storePhone = jsonObjectResponse.getString("formatted_phone_number")
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
                if (mFavoriteStoresList?.find { storeID -> storeID == store!!.storeID } != null)
                    store!!.isFavorite = true
                mCommunicator.changeFragmentToStoreDetails(store!!)
            },
            {})
        requestQueue?.add(stringReq)
    }

    fun fetchFavoriteStores(context: Context, userID: String) {
        this.userID = userID
        mFavoriteStoresList = ArrayList()
        val requestQueue: RequestQueue? = VolleySingleton.getInstance(context)?.requestQueue
        val params = JSONArray()
        val jsonParam = JSONObject()
        jsonParam.put(USERID, userID)
        params.put(jsonParam)
        val url = "http://192.168.1.20:45455/api/Store/GetFavoriteStores"
        val stringReq = CustomJsonArrayRequest(
            Request.Method.POST, url, jsonParam, { response ->
                Log.d("VolleySucceedSignIn", response.toString())
                for (i in 0 until response.length()) {
                    mFavoriteStoresList!!.add(response.getJSONObject(i).getString("storeID"))
                }
                isExist()
                adapterListener.refreshAdapter()
            }, {
                Log.d("Error", "signIn: check if server is up")
            })
        requestQueue?.add(stringReq)
    }

    fun addOrRemoveStoreFromFavorite(context: Context, storeID: String, command: String) {
        val requestQueue: RequestQueue? = VolleySingleton.getInstance(context)?.requestQueue
        val jsonObject = JSONObject()
        jsonObject.put("StoreID", storeID)
        jsonObject.put("UserID", userID)
        val url = "http://192.168.1.20:45455/api/Store/${command}"
        val stringReq = JsonObjectRequest(
            Request.Method.POST, url, jsonObject, { response ->
                Log.d("VolleySucceedSignIn", response.toString())
            }, {
                Log.d("Error", "signIn: check if server is up")
            })
        requestQueue?.add(stringReq)
    }
}
