package com.findmyflorist.model

import java.io.Serializable

class Store(
    val storeID: String,
    val storeName: String,
    val Latitude: Double,
    val Longitude: Double,
    val Address: String,
    val isOpen: String,
    val storeDistanceFromUser : Double,
    val phone: String = "",
    var isFavorite: Boolean = false,
    val website : String = ""
) : Serializable