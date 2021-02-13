package com.danielefavaro.waround.data.entities

data class DirectionModel(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
)

data class Leg(
    val distance: DistanceOrDuration,
    val duration: DistanceOrDuration,
    val end_address: String,
    val end_location: Location,
    val start_address: String,
    val start_location: Location,
    val steps: List<Step>
)

data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>,
    val overview_polyline: Polyline,
    val summary: String,
    val warnings: List<Any>,
    val waypoint_order: List<Int>
)

data class Step(
    val distance: DistanceOrDuration,
    val duration: DistanceOrDuration,
    val end_location: Location,
    val html_instructions: String,
    val polyline: Polyline,
    val start_location: Location,
    val travel_mode: String
)

data class Bounds(
    val northeast: Location,
    val southwest: Location
)

data class DistanceOrDuration(
    val text: String,
    val value: Int
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Polyline(
    val points: String
)