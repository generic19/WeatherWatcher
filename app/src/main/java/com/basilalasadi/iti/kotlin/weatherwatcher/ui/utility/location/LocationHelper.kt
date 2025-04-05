package com.basilalasadi.iti.kotlin.weatherwatcher.ui.utility.location

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationHelper(private val application: Application) {
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
    fun getLocationFlow(): Flow<Location> = callbackFlow<Location> {
        val locationProvider = getLocationProvider()
        
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setMaxUpdateAgeMillis(60 * 1000)
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
    
    @RequiresPermission(allOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ])
    suspend fun getCurrentLocation(): Location {
        val locationProvider = getLocationProvider()
        
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .setMaxUpdateAgeMillis(60 * 1000)
            .build()
        
        return suspendCancellableCoroutine { cont ->
            locationProvider.getCurrentLocation(request, null)
                .addOnSuccessListener {
                    cont.resume(it)
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
                .addOnCanceledListener {
                    cont.cancel()
                }
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

fun Location.asCoordinates() = City.Coordinates(latitude, longitude)

