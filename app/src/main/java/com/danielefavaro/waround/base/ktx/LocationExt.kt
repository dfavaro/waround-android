package com.danielefavaro.waround.base.ktx

import com.google.android.gms.maps.model.LatLng

fun LatLng.toQuery() = StringBuilder().apply {
    append(latitude)
    append(",")
    append(longitude)
}.toString()