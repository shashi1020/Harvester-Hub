package com.example.harvesterhub.utils

import android.content.Context
import android.location.Geocoder
import android.location.Location
import java.util.*

fun reverseGeocode(context: Context, location: Location): Pair<String?, String?> {
    val geocoder = Geocoder(context, Locale.getDefault())
    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

    if (!addresses.isNullOrEmpty()) {
        val address = addresses[0]

        // Prefer locality or subLocality for district over subAdminArea
        val district = address.locality ?: address.subLocality ?: address.subAdminArea

        val state = address.adminArea // state usually correct

        return Pair(state, district)
    }

    return Pair(null, null)
}
