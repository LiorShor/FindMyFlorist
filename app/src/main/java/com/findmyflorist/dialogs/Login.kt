package com.findmyflorist.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.findmyflorist.databinding.DialogLoginBinding
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.findmyflorist.R
import com.findmyflorist.VolleySingleton
import com.findmyflorist.activities.MainActivity.Companion.EMAIL
import com.findmyflorist.activities.MainActivity.Companion.FULL_NAME
import com.findmyflorist.activities.MainActivity.Companion.PASSWORD
import com.findmyflorist.activities.MainActivity.Companion.user
import com.findmyflorist.fragments.StoreSearch
import org.json.JSONObject

class Login(context: Context) : ConstraintLayout(context) {
    private val mLoginDialog = Dialog(context)
    private lateinit var mBinding: DialogLoginBinding

    init {
        setDialogSettings()
        onClickLoginButton(context)
        onClicksSignUpEditText(context)
        onClickForgotPassword()
    }

    private fun setDialogSettings() {
        mBinding = DialogLoginBinding.inflate(LayoutInflater.from(context))
        mLoginDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mLoginDialog.setContentView(mBinding.root)
        mLoginDialog.show()
        mLoginDialog.setCanceledOnTouchOutside(true)
        setDialogWidthAndHeight()
    }

    private fun setDialogWidthAndHeight() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        mLoginDialog.window?.setLayout(6 * width / 7, 4 * height / 5)
    }

    private fun onClicksSignUpEditText(context: Context) {
        mBinding.editTextSignUp.setOnClickListener {
            mLoginDialog.dismiss()
            Register(context)
        }
    }

    private fun onClickForgotPassword() {
        mBinding.forgotPasswordTextView.setOnClickListener {
        }
    }

    private fun onClickLoginButton(context: Context) {
        mBinding.signIn.setOnClickListener {

            val emailAddress = mBinding.editTextEmailAddress.text.toString()
            val password = mBinding.editTextPassword.text.toString()
            if (stringChecker(
                    emailAddress,
                    password
                )
            ) {
                signIn(emailAddress, password, context)
            }
        }
    }

    private fun stringChecker(emailAddress: String, password: String): Boolean {
        var isValid = true
        if (emailAddress.isEmpty()) {
            mBinding.editTextEmailAddress.setHintTextColor(Color.RED)
            isValid = false
        }
        if (password.isEmpty()) {
            mBinding.editTextPassword.setHintTextColor(Color.RED)
            isValid = false
        }
        return isValid
    }

    private fun signIn(emailAddress: String, password: String, context: Context) {
        val userCredentialsJSON = JSONObject()
        userCredentialsJSON.put(EMAIL, emailAddress)
        userCredentialsJSON.put(PASSWORD, password)
        val requestQueue: RequestQueue? = VolleySingleton.getInstance(context)?.requestQueue
        val url = "http://192.168.1.23:45455/api/User/SignIn"
        val stringReq = JsonObjectRequest(
            Request.Method.POST, url, userCredentialsJSON, { response ->
                Log.d("VolleySucceedSignIn", response.toString())
                if(response.getBoolean("succeed")) {
                    user.fullName = "Hello " + response.getString("message")
                    mLoginDialog.dismiss()
                }
                else{
                    mBinding.editTextEmailAddress.setHintTextColor(Color.RED)
                    mBinding.editTextPassword.setHintTextColor(Color.RED)
                }
            }, {
                Log.d("Error", "signIn: check if server is up")
            })
        requestQueue?.add(stringReq)
    }
}