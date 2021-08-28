package com.findmyflorist.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.findmyflorist.databinding.DialogRegisterBinding

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
            if (validation(name, email, password, rePassword)) {
                writeNewUser(password, email)
            }
        }
    }

    private fun writeNewUser(password: String, email: String) {

    }

    private fun validation(
        name: String,
        email: String,
        password: String,
        rePassword: String
    ): Boolean {
        var validationSuccess = true
        if (name.isEmpty()) {
            mBinding.editTextPersonName.setHintTextColor(Color.RED)
            validationSuccess = false
        }
        if (email.isEmpty()) {
            mBinding.editTextEmail.setHintTextColor(Color.RED)
            validationSuccess = false
        }
        if (rePassword == "") {
            mBinding.editTextRegisterPassword.setHintTextColor(Color.RED)
            validationSuccess = false
        }
        if (password == "") {
            mBinding.editTextRePassword.setHintTextColor(Color.RED)
            validationSuccess = false
        }
        if (password != rePassword) {
            mBinding.editTextRegisterPassword.setTextColor(Color.RED)
            mBinding.editTextRePassword.setTextColor(Color.RED)
            validationSuccess = false
        }
        return validationSuccess
    }
}