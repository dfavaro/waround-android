package com.danielefavaro.waround.maps.ui.model

import android.os.Parcelable
import com.danielefavaro.waround.data.entities.DirectionModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DirectionUI(
    var startName: String = "",
    var endName: String = "",
    var startLat: Double = .0,
    var startLng: Double = .0,
    var endLat: Double = .0,
    var endLng: Double = .0,
    var overviewPolyline: String = "",
    var routeList: MutableList<String> = mutableListOf()
) : Parcelable

fun DirectionUI.fromApi(data: DirectionModel) = apply {
    data.routes.getOrNull(0)?.let { route ->
        route.legs.getOrNull(0)?.let { leg ->
            startLat = leg.start_location.lat
            startLng = leg.start_location.lng
            endLat = leg.end_location.lat
            endLng = leg.end_location.lng
            leg.steps.forEach {
                routeList.add(it.html_instructions)
            }
        }
        overviewPolyline = route.overview_polyline.points
    }
}
