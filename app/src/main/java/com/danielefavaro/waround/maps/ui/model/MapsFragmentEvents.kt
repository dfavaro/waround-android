package com.danielefavaro.waround.maps.ui.model

import com.danielefavaro.waround.base.OneTimeEvent
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

sealed class MapsFragmentEvents : OneTimeEvent {
    object OnLocationPermissionRequest : MapsFragmentEvents()
    object OnLocationPermissionGranted : MapsFragmentEvents()
    object OnLocationPermissionDenied : MapsFragmentEvents()
    object OnGenericError : MapsFragmentEvents()
    data class OnRouteStart(val directionUI: DirectionUI) : MapsFragmentEvents()
    object OnRouteEnd : MapsFragmentEvents()
    data class OnMarkerClick(val marker: Marker) : MapsFragmentEvents()
    data class OnNearbyArticles(val nearbyArticles: List<MapsUI>) : MapsFragmentEvents()
    data class OnUserLocation(
        val userLocation: LatLng,
        val bearing: Float? = null,
        val moveCamera: Boolean = true
    ) : MapsFragmentEvents()
}