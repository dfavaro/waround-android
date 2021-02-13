package com.danielefavaro.waround.maps.ui.viewmodel

import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.danielefavaro.waround.BuildConfig
import com.danielefavaro.waround.base.StatefulViewModel
import com.danielefavaro.waround.base.exception.LocationNotFound
import com.danielefavaro.waround.base.ktx.toQuery
import com.danielefavaro.waround.base.util.Constants
import com.danielefavaro.waround.base.util.Result
import com.danielefavaro.waround.domain.LocationRepository
import com.danielefavaro.waround.maps.domain.MapsRepository
import com.danielefavaro.waround.maps.ui.MapsFragment
import com.danielefavaro.waround.maps.ui.model.*
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.min

private const val MAX_RADIUS = 10000.0

class MapsViewModel @Inject constructor(
    private val mapsRepository: MapsRepository,
    private val locationRepository: LocationRepository
) : StatefulViewModel<MapsFragmentViewState>(MapsFragmentViewState()) {

    fun onStartLocationFlow() {
        viewModelScope.launch {
            // TODO improve
            val locationAvailability = locationRepository.onLocationAvailability()
            onLocationPermissionRequest()
        }
    }

    fun onLocationPermissionRequest() {
        sendEvent(MapsFragmentEvents.OnLocationPermissionRequest)
    }

    fun onLocationPermissionDenied() {
        setState {
            it.copy(
                isLoading = false
            )
        }
        sendEvent(MapsFragmentEvents.OnLocationPermissionDenied)
    }

    fun onLocationPermissionGranted() {
        setState {
            it.copy(
                isLoading = false
            )
        }
        sendEvent(MapsFragmentEvents.OnLocationPermissionGranted)
    }

    fun onNearbyArticlesRequest(
        location: LatLng,
        mapProjection: Projection? = null,
        cameraPosition: CameraPosition? = null
    ) {
        if (viewState.isRouting) return

        viewModelScope.launch {
            var distance = MAX_RADIUS
            if (mapProjection != null && cameraPosition != null) {
                distance = async { zoomToDistance(mapProjection, cameraPosition) }.await()
            }

            when (val result =
                mapsRepository.getNearbyArticles(location.latitude, location.longitude, distance)) {
                is Result.Success -> {
                    val mapsUiList = mutableListOf<MapsUI>().apply {
                        result.data.query.geosearch.forEach {
                            val mapsUI = MapsUI().fromApi(it)
                            add(mapsUI)
                        }
                    }

                    val existingAndNew = async {
                        distinctArticles(
                            viewState.nearbyArticles,
                            mapsUiList
                        )
                    }.await()

                    sendEvent(MapsFragmentEvents.OnNearbyArticles(existingAndNew.second))
                    setState {
                        it.copy(
                            isLoading = false,
                            nearbyArticles = existingAndNew.first
                        )
                    }
                }
                is Result.Error -> {
                    sendEvent(MapsFragmentEvents.OnGenericError)
                }
            }
        }
    }

    private suspend fun distinctArticles(
        existingArticles: List<MapsUI>,
        newArticles: List<MapsUI>
    ): Pair<List<MapsUI>, List<MapsUI>> =
        withContext(Dispatchers.Default) {
            val updatedExisting = existingArticles.toMutableList()
            val onlyNew = mutableListOf<MapsUI>()

            newArticles.forEach { newArticle ->
                existingArticles.find { it.pageid == newArticle.pageid } ?: run {
                    updatedExisting.add(newArticle)
                    onlyNew.add(newArticle)
                }
            }
            return@withContext Pair(updatedExisting, onlyNew.toList())
        }

    private suspend fun zoomToDistance(
        mapProjection: Projection,
        cameraPosition: CameraPosition
    ): Double = withContext(Dispatchers.Default) {
        return@withContext min(
            SphericalUtil.computeDistanceBetween(
                mapProjection.visibleRegion.farLeft,
                cameraPosition.target
            ), MAX_RADIUS
        )
    }

    fun requestLocationUpdates() {
        setState {
            it.copy(
                locationCameraUpdates = true
            )
        }
        viewModelScope.launch {
            locationRepository.requestLocationUpdates().collect { location ->
                val userLocation = LatLng(location.latitude, location.longitude)
                onLocation(userLocation)

                if (BuildConfig.DEBUG) {
                    Log.d(MapsFragment.TAG, "Location update requested: $userLocation")
                }
            }
        }
    }

    fun getOneShotLocation() {
        viewModelScope.launch {
            val userLocation = try {
                val location = locationRepository.getOneShotLocation()
                LatLng(location.latitude, location.longitude)
            } catch (e: LocationNotFound) {
                LatLng(41.9028, 12.4964) // Rome
            }
            onLocation(userLocation)
        }
    }

    private fun onLocation(latLng: LatLng) {
        // location contains bearing if needed
        sendEvent(
            MapsFragmentEvents.OnUserLocation(
                latLng,
                moveCamera = viewState.locationCameraUpdates
            )
        )
    }

    fun onCameraGestureMove() {
        stopLocationCameraUpdates()
    }

    fun stopLocationCameraUpdates() {
        setState {
            it.copy(
                isLoading = false,
                locationCameraUpdates = false
            )
        }
    }

    fun onMarkerClick(marker: Marker) {
        setState {
            it.copy(
                selectedMarker = marker
            )
        }
        sendEvent(MapsFragmentEvents.OnMarkerClick(marker))
    }

    fun onRouteEnd() {
        sendEvent(MapsFragmentEvents.OnRouteEnd)
        setState {
            it.copy(
                locationCameraUpdates = true,
                isRouting = false
            )
        }
    }

    fun onRouteStart() {
        viewState.selectedMarker?.let { destinationMarker ->
            viewModelScope.launch {
                val origin = try {
                    val location = locationRepository.getOneShotLocation()
                    LatLng(location.latitude, location.longitude)
                } catch (e: LocationNotFound) {
                    LatLng(41.9028, 12.4964) // Rome
                }
                getDirection(origin, destinationMarker.position)
            }
        }
    }

    private fun getDirection(origin: LatLng, destination: LatLng) {
        viewModelScope.launch {
            when (val result =
                mapsRepository.getDirection(origin.toQuery(), destination.toQuery())) {
                is Result.Success -> {
                    setState {
                        it.copy(
                            locationCameraUpdates = false,
                            isRouting = true,
                            nearbyArticles = emptyList()
                        )
                    }
                    val directionUI = DirectionUI().fromApi(result.data)
                    sendEvent(MapsFragmentEvents.OnRouteStart(directionUI))
                }
                is Result.Error -> sendEvent(MapsFragmentEvents.OnGenericError)
            }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onLocationPermissionGranted()
                else onLocationPermissionDenied()
            }
        }

    }
}