package com.findmyflorist.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.findmyflorist.databinding.DialogSearchBinding
import com.findmyflorist.remote.StoresRepository

class Search(context: Context) : ConstraintLayout(context) {
    private val mSearchDialog = Dialog(context)
    private lateinit var mBinding: DialogSearchBinding

    init {
        setDialogSettings()
        onClickSearchButton()
    }

    private fun onClickSearchButton() {
        mBinding.searchButton.setOnClickListener {
            val text = mBinding.searchEditText.text
            if (text.toString().isNotEmpty()) {
                StoresRepository.getInstance()?.getLatLonByAddress(text.toString())
                mSearchDialog.dismiss()
            }
        }
    }

    private fun setDialogSettings() {
        mBinding = DialogSearchBinding.inflate(LayoutInflater.from(context))
        mSearchDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        mSearchDialog.setContentView(mBinding.root)
        mSearchDialog.show()
        mSearchDialog.setCanceledOnTouchOutside(true)
        setDialogWidthAndHeight()
    }

    private fun setDialogWidthAndHeight() {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        mSearchDialog.window?.setLayout(6 * width / 7, 4 * height / 5)
    }
}