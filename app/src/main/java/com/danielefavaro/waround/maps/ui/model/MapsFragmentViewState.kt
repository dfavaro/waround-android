package com.danielefavaro.waround.maps.ui.model

import com.danielefavaro.waround.base.ViewState
import com.google.android.gms.maps.model.Marker

data class MapsFragmentViewState(
    val isLoading: Boolean = true,
    val nearbyArticles: List<MapsUI> = listOf(),
    val locationCameraUpdates: Boolean = true,
    val isRouting: Boolean = false,
    val selectedMarker: Marker? = null
) : ViewState