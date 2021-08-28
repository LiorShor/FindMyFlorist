package com.findmyflorist.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import com.findmyflorist.databinding.DialogLoginBinding
import androidx.constraintlayout.widget.ConstraintLayout

class Login(context: Context) : ConstraintLayout(context) {
    private val mLoginDialog = Dialog(context)
    private lateinit var mBinding: DialogLoginBinding

    init {
        setDialogSettings()
        onClickLoginButton(context)
        onClicksSignUpEditText(context)
        onClickForgotPassword(context)
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

    private fun onClickForgotPassword(context: Context) {
        mBinding.forgotPasswordTextView.setOnClickListener {
        }
    }

    private fun onClickLoginButton(context: Context) {
        mBinding.signIn.setOnClickListener {

            val emailAddress = mBinding.editTextTextEmailAddress.text.toString()
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
            mBinding.editTextTextEmailAddress.setHintTextColor(Color.RED)
            isValid = false
        }
        if (password.isEmpty()) {
            mBinding.editTextPassword.setHintTextColor(Color.RED)
            isValid = false
        }
        return isValid
    }

    private fun signIn(emailAddress: String, password: String, context: Context) {
        val bundle = Bundle()
    }
}