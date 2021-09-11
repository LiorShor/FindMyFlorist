package com.findmyflorist.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import com.example.textrecognition.view.fragments.ICommunicator
import com.findmyflorist.R
import com.findmyflorist.databinding.ActivityMainBinding
import com.findmyflorist.dialogs.Login
import com.findmyflorist.fragments.*
import com.findmyflorist.model.User

class MainActivity : AppCompatActivity(), ICommunicator {
    private lateinit var mToggle: ActionBarDrawerToggle
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
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
            changeFragmentToStoreSearch()
        }
        actionBarDrawerConfiguration()
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
            headerTextView.text = getString(R.string.hello_guest)
            signInTextView.visibility = View.VISIBLE
            logoutTextView.visibility = View.INVISIBLE
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
            ContextCompat.getColor(this, R.color.white) //Set the drawer icon to white
        mBinding.drawerLayout.addDrawerListener(mToggle)
        mToggle.syncState()
        mBinding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeItem -> changeFragmentToStoreSearch()
                R.id.favoriteItem -> changeFragmentToFavorites()
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

    override fun changeFragmentWithData(userName: String) {
        TODO("Not yet implemented")
    }

    override fun changeFragmentToStoreSearch() {
        supportFragmentManager.beginTransaction().replace(R.id.container, StoreSearch()).commit()
    }

    override fun changeFragmentToFavorites() {
        supportFragmentManager.beginTransaction().replace(R.id.container, Favorites()).commit()
    }

    override fun changeFragmentToAboutUs() {
        supportFragmentManager.beginTransaction().replace(R.id.container, About()).commit()
    }

    override fun changeFragmentToStoreDetails() {
        supportFragmentManager.beginTransaction().replace(R.id.container, StoreDetails()).commit()
    }
}
