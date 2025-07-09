package com.example.harvesterhub.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@Composable
fun RequestAndFetchLocation(
    onLocationResult: (latitude: Double, longitude: Double, state: String?, district: String?) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            if (!isLocationEnabled()) {
                Toast.makeText(context, "Please turn on GPS", Toast.LENGTH_LONG).show()
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                return@rememberLauncherForActivityResult
            }

            scope.launch {
                try {
                    // 🔐 Check permission inside coroutine too
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
                        fusedLocationProviderClient.lastLocation
                            .addOnSuccessListener { location ->
                                location?.let {
                                    val (state, district) = reverseGeocode(context, it)
                                    onLocationResult(it.latitude, it.longitude, state, district)
                                }
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                            }
                    } else {
                        // 🚫 Handle the case when permission is not granted
                        Toast.makeText(context, "Location permission not granted.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: SecurityException) {
                    // 🔐 Handle unexpected permission issues gracefully
                    e.printStackTrace()
                    Toast.makeText(context, "SecurityException: ${e.message}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

