package com.findmyflorist.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.findmyflorist.databinding.ActivityMainBinding
import com.findmyflorist.dialogs.Login
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun executeLoginWithEmail() {
        Login(this)
/*        prefs = this.getPreferences(Context.MODE_PRIVATE)
        val editor = prefs?.edit()
        editor?.putBoolean("Fingerprint", false)
        editor?.apply()*/
    }

    fun executeLoginWithFingerprint() {
     //   prefs = this.getPreferences(Context.MODE_PRIVATE)
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
                this,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {

/*                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
*//*                        binding.loginBT.visibility = View.INVISIBLE
                        binding.fingerPrintBT.visibility = View.INVISIBLE
                        val editor = prefs?.edit()
                        editor?.putBoolean("Fingerprint", true)
                        editor?.apply()*//*
                        supportFragmentManager.beginTransaction()
                                .replace(
                                        R.id.container,
                                        ImageAnalysisFragment()

                                )
                                .commitNow()
                    }*/
                })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using biometric fingerprint")
                .setNegativeButtonText("Close")
                .build()
        biometricPrompt.authenticate(promptInfo)
    }

    fun executeLoginWithEmail(view: android.view.View) {
        Login(this)
    }

    /* fun getUsers() {
         // Instantiate the RequestQueue.
         val queue = Volley.newRequestQueue(this)
         val url: String = "https://api.github.com/search/users?q=eyehunt"

         // Request a string response from the provided URL.
         val stringReq = StringRequest(
             Request.Method.GET, url,
             { response ->

                 var strResp = response.toString()
                 val jsonObj: JSONObject = JSONObject(strResp)
                 val jsonArray: JSONArray = jsonObj.getJSONArray("items")
                 var str_user: String = ""
                 for (i in 0 until jsonArray.length()) {
                     var jsonInner: JSONObject = jsonArray.getJSONObject(i)
                     str_user = str_user + "\n" + jsonInner.get("login")
                 }
                 textView!!.text = "response : $str_user "
             },
             { textView!!.text = "That didn't work!" })
         queue.add(stringReq)
     }*/

    companion object {
        const val EMAIL = "Email"
        const val PASSWORD = "Password"
        const val USERID = "UserID"
        const val FULLNAME = "FullName"
        const val ADDRESS = "Address"

    }

}
