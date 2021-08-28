package com.findmyflorist

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
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
}
