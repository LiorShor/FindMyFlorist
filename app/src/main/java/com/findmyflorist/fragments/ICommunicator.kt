package com.example.textrecognition.view.fragments

import java.io.Serializable

interface ICommunicator {
    fun changeFragmentWithData(
        userName: String
    )

    fun changeFragmentToStoreSearch()
}