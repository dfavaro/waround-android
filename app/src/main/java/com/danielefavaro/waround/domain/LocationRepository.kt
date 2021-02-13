package com.danielefavaro.waround.domain

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun onLocationAvailability(): Any
    suspend fun getOneShotLocation(): Location
    fun requestLocationUpdates(): Flow<Location>
}