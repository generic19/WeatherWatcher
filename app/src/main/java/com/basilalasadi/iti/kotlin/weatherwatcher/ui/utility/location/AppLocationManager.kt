package com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AppLocationManager(private val application: Application) {
    val arePermissionsGranted: Boolean get() {
        return sequenceOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ).any { application.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
    }
    
    val isLocationEnabled: Boolean get() {
        val locationManager = getLocationManager()
        
        return sequenceOf(
            LocationManager.NETWORK_PROVIDER,
            LocationManager.GPS_PROVIDER,
        ).any { locationManager.isProviderEnabled(it) }
    }
    
    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ])
    fun getLocation(): Flow<Location> = callbackFlow<Location> {
        val locationProvider = getLocationProvider()
        
        val request = LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 5000)
            .setGranularity(Granularity.GRANULARITY_COARSE)
            .setMaxUpdateAgeMillis(1 * 60 * 60 * 1000)
            .build()
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { trySend(it) }
            }
            
            override fun onLocationAvailability(availability: LocationAvailability) {
                if (!availability.isLocationAvailable) {
                    throw LocationException("Location got turned off.")
                }
            }
        }
        
        locationProvider.requestLocationUpdates(request, locationCallback, null)
        
        awaitClose {
            locationProvider.removeLocationUpdates(locationCallback)
        }
    }
    
    private fun getLocationProvider(): FusedLocationProviderClient {
        if (!isLocationEnabled) {
            throw LocationException("Location is not enabled.")
        }
        return LocationServices.getFusedLocationProviderClient(application)
    }
    
    private fun getLocationManager(): LocationManager {
        if (!arePermissionsGranted) {
            throw LocationException("Missing permissions.")
        }
        return application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}