package com.findmyflorist.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.textrecognition.view.fragments.ICommunicator
import com.findmyflorist.R
import com.findmyflorist.databinding.ActivityMainBinding
import com.findmyflorist.fragments.RequestLocationPermissionsFragment
import com.findmyflorist.fragments.StoreSearch

class MainActivity : AppCompatActivity(),ICommunicator {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
    }
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
        const val FULLNAME = "FullName"
        const val ADDRESS = "Address"

    }

    override fun changeFragmentWithData(userName: String) {
        TODO("Not yet implemented")
    }

    override fun changeFragmentToStoreSearch() {
        supportFragmentManager.beginTransaction().replace(R.id.container, StoreSearch()).commit()
    }

}
