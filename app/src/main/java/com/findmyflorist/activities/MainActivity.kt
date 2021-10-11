package com.findmyflorist.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.findmyflorist.fragments.ICommunicator
import com.findmyflorist.R
import com.findmyflorist.databinding.ActivityMainBinding
import com.findmyflorist.dialogs.Login
import com.findmyflorist.fragments.*
import com.findmyflorist.model.Store
import com.findmyflorist.model.User
import com.findmyflorist.remote.StoresRepository
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), ICommunicator {
    private lateinit var mToggle: ActionBarDrawerToggle
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        actionBarDrawerConfiguration()
        Handler(Looper.getMainLooper()).postDelayed({
            StoresRepository.getInstance()?.init(this, applicationContext)

        }, 3000)
    }

    private fun actionBarDrawerConfiguration() {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val signInTextView =
            mBinding.navView.getHeaderView(0).findViewById<TextView>(R.id.sign_in_register_textview)
        val logoutTextView = mBinding.navView.getHeaderView(0).findViewById<TextView>(R.id.logout)
        val headerTextView =
            mBinding.navView.getHeaderView(0).findViewById<TextView>(R.id.hello_guest_title)
        signInTextView.setOnClickListener {

            Login(this)
            mBinding.drawerLayout.closeDrawers()

        }
        logoutTextView.setOnClickListener {
            user.fullName = "Hello guest"
            headerTextView.text = getString(R.string.hello_guest)
            signInTextView.visibility = View.VISIBLE
            logoutTextView.visibility = View.INVISIBLE
            StoresRepository.getInstance()?.getStoreList?.stream()
                ?.forEach { store -> store.isFavorite = false }
            StoresRepository.getInstance()?.adapterListener?.refreshAdapter()
        }
        mToggle = object : ActionBarDrawerToggle(
            this,
            mBinding.drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                headerTextView.text = user.fullName
                if (user.fullName != "Hello guest") {
                    signInTextView.visibility = View.INVISIBLE
                    logoutTextView.visibility = View.VISIBLE
                }
            }
        }

        supportActionBar!!.setDisplayShowTitleEnabled(false)
        mToggle.drawerArrowDrawable.color =
            ContextCompat.getColor(this, R.color.deep_green) //Set the drawer icon to white
        mBinding.drawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        mBinding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeItem -> changeFragmentToStoreSearch()
                R.id.favoriteItem -> {
                    if (user.fullName != "Hello guest") {
                        changeFragmentToFavorites()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Please log in to see your favorite stores",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                R.id.aboutItem -> changeFragmentToAboutUs()
            }
            mBinding.drawerLayout.closeDrawers()
            true
        }
    }

    //Request permissions from the user to use GPS location
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (applicationContext.let {
                        ContextCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } == PackageManager.PERMISSION_GRANTED
                ) {
                    changeFragmentToStoreSearch()
                }
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EMAIL = "Email"
        const val PASSWORD = "Password"
        const val USERID = "UserID"
        const val FULL_NAME = "FullName"
        const val ADDRESS = "Address"
        val user: User = User("Hello guest")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    override fun stopLottie() {
        mBinding.loadingAnimation.visibility = View.INVISIBLE
    }

    override fun changeFragmentToMapFragment(latLng: LatLng) {
        val bundle = Bundle()
        val fragment = MapFragment()
        bundle.putDouble("lat", latLng.latitude)
        bundle.putDouble("lon", latLng.longitude)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            .addToBackStack(null).commit()
    }

    override fun changeFragmentToStoreSearch() {
        if (applicationContext.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } !=
            PackageManager.PERMISSION_GRANTED) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RequestLocationPermissionsFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.container, StoreSearch())
                .commit()
        }
    }

    override fun changeFragmentToFavorites() {
        supportFragmentManager.beginTransaction().replace(R.id.container, Favorites()).commit()
    }

    override fun changeFragmentToAboutUs() {
        supportFragmentManager.beginTransaction().replace(R.id.container, About()).commit()
    }

    override fun changeFragmentToStoreDetails(store: Store) {
        val bundle = Bundle()
        val fragment = StoreDetails()
        bundle.putSerializable("Store", store)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            .addToBackStack(null).commit()
    }

    override fun openWaze(latitude: Double, longitude: Double) {
        packageManager?.let {
            val url = "waze://?ll=$latitude,$longitude&navigate=yes"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.resolveActivity(it)?.let {
                startActivity(intent)
            } ?: run {
                Toast.makeText(
                    applicationContext,
                    "It appears waze is not installed on your phone",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun openWebsite(url: String) {
        if(url != "") {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        else
            Toast.makeText(this,"This store doesn't have a website",Toast.LENGTH_LONG).show()
    }
}
