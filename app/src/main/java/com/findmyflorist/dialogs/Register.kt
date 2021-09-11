package com.findmyflorist.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.findmyflorist.VolleySingleton
import com.findmyflorist.activities.MainActivity.Companion.ADDRESS
import com.findmyflorist.activities.MainActivity.Companion.EMAIL
import com.findmyflorist.activities.MainActivity.Companion.FULL_NAME
import com.findmyflorist.activities.MainActivity.Companion.PASSWORD
import com.findmyflorist.activities.MainActivity.Companion.USERID
import com.findmyflorist.databinding.DialogRegisterBinding
import org.json.JSONObject
import java.util.*

class Register(context: Context) : ConstraintLayout(context) {

    private lateinit var mBinding: DialogRegisterBinding
    private var mRegisterDialog = Dialog(context)

    init {
        setDialogSettings()
        setOnClickRegisterButton()
    }

    private fun setDialogSettings() {
        mBinding = DialogRegisterBinding.inflate(LayoutInflater.from(context))
        mRegisterDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mRegisterDialog.setContentView(mBinding.root)
        mRegisterDialog.show()
        mRegisterDialog.setCanceledOnTouchOutside(true)
        setDialogWidthAndHeight()
    }

    private fun setDialogWidthAndHeight() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        mRegisterDialog.window!!.setLayout(6 * width / 7, 4 * height / 5)
    }

    private fun setOnClickRegisterButton() {
        mBinding.registerBT.setOnClickListener {
            val name = mBinding.editTextPersonName.text.toString()
            val email = mBinding.editTextEmail.text.toString()
            val password = mBinding.editTextRegisterPassword.text.toString()
            val rePassword = mBinding.editTextRePassword.text.toString()
            val address = mBinding.editTextAddress.text.toString()
            if (validation(name, email, password, rePassword, address)) {
                writeNewUser(name, password, email, address)
            }
        }
    }

    private fun validation(
        name: String,
        email: String,
        password: String,
        rePassword: String,
        address: String
    ): Boolean {
        var validationSuccess = true
        if (name.isEmpty()) {
            mBinding.editTextLayoutPersonName.error = "Required*"
            validationSuccess = false
        } else {
            mBinding.editTextLayoutPersonName.error = null
        }
        if (email.isEmpty()) {
            mBinding.editTextLayoutEmail.error = "Required*"
            validationSuccess = false
        } else {
            mBinding.editTextLayoutEmail.error = null
        }
        if (rePassword.isEmpty()) {
            mBinding.editTextLayoutRePassword.error = "Required*"
            validationSuccess = false
        } else {
            mBinding.editTextLayoutRePassword.error = null
        }
        if (password.isEmpty()) {
            mBinding.editTextLayoutRegisterPassword.error = "Required*"
            validationSuccess = false

        } else {
            if (password != rePassword) {
                mBinding.editTextLayoutRegisterPassword.error = "Passwords do not match"
                mBinding.editTextLayoutRePassword.error = "Passwords do not match"
                validationSuccess = false
            } else {
                mBinding.editTextLayoutRegisterPassword.error = null
                mBinding.editTextLayoutRePassword.error = null
            }
        }

        if (address.isEmpty()) {
            mBinding.editTextLayoutAddress.error = "Required*"
            validationSuccess = false
        } else {
            mBinding.editTextLayoutAddress.error = null
        }
        return validationSuccess
    }

    private fun writeNewUser(
        fullName: String,
        password: String,
        emailAddress: String,
        address: String
    ) {
        val bundle = Bundle()
        val userCredentialsJSON = JSONObject()
        userCredentialsJSON.put(USERID, UUID.randomUUID())
        userCredentialsJSON.put(FULL_NAME, fullName)
        userCredentialsJSON.put(EMAIL, emailAddress)
        userCredentialsJSON.put(PASSWORD, password)
        userCredentialsJSON.put(ADDRESS, address)
        val requestQueue: RequestQueue? = VolleySingleton.getInstance(context)?.requestQueue
        val url = "http://192.168.1.23:45455/api/User/SignUp"
        val stringReq = JsonObjectRequest(
            Request.Method.POST, url, userCredentialsJSON,
            { response ->
                if (response.getString("succeed").equals("true")) {
                    bundle.putString(EMAIL, emailAddress)
                    bundle.putString(PASSWORD, password)
                    mRegisterDialog.dismiss()
                } else {
                    Toast.makeText(
                        context,
                        response.getString("message").toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            {
                Log.d("SignUpError", "Volley failed to register user")
            })
        requestQueue?.add(stringReq)
    }
}
