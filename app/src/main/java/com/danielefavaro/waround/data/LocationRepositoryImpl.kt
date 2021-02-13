package com.danielefavaro.waround.data

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.danielefavaro.waround.base.exception.LocationNotFound
import com.danielefavaro.waround.domain.LocationRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val LOCATION_INTERVAL: Long = 5000
private const val LOCATION_FASTEST_INTERVAL: Long = 2500

class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val settingsClient: SettingsClient
) : LocationRepository {

    private val locationRequest = LocationRequest.create().apply {
        interval = LOCATION_INTERVAL
        fastestInterval = LOCATION_FASTEST_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)

    override suspend fun onLocationAvailability(): Any = suspendCoroutine { cont ->
        settingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())
            .addOnCompleteListener {
                cont.resume(Any())
            }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getOneShotLocation(): Location = suspendCoroutine { cont ->
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                cont.resume(it)
            } ?: run {
                cont.resumeWithException(LocationNotFound())
            }
        }.addOnFailureListener {
            cont.resumeWithException(LocationNotFound())
        }
    }

    @ExperimentalCoroutinesApi
    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates() = callbackFlow<Location> {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    offer(location)
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
}