package com.findmyflorist.model

import java.util.*
import kotlin.math.*

class Store(
    val storeName: String,
    val city: String,
    val phone: String,
    val isOpen: String,
    iLatitudeDistance: Double, iLongitudeDistance: Double,
    val isFavorite: Boolean = false
) {

    val storeID: String = UUID.randomUUID().toString()
    val location = SelfLocation(iLatitudeDistance, iLongitudeDistance)

    val AVERAGE_RADIUS_OF_EARTH_KM = 6371.0
    fun calculateDistanceInKilometer(
        userLat: Double, userLng: Double,
        venueLat: Double, venueLng: Double
    ): Int {
        val latDistance = Math.toRadians(userLat - venueLat)
        val lngDistance = Math.toRadians(userLng - venueLng)
        val a = (sin(latDistance / 2) * sin(latDistance / 2)
                + (cos(Math.toRadians(userLat)) * cos(Math.toRadians(venueLat))
                * sin(lngDistance / 2) * sin(lngDistance / 2)))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (AVERAGE_RADIUS_OF_EARTH_KM * c).roundToInt()
    }
}