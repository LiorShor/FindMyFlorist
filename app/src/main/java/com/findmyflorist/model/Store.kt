package com.findmyflorist.model

class Store(
    val storeID: String,
    val storeName: String,
    val storeLatitude: Double,
    val storeLongitude: Double,
    val Address: String,
    val isOpen: String,
    val storeDistanceFromUser : Double,
    val phone: String = "",
    val isFavorite: Boolean = false
)