package com.danielefavaro.waround.data.service

import com.danielefavaro.waround.data.entities.DirectionModel
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface GoogleService {

    @GET("directions/json?")
    suspend fun getDirection(
        @Query("origin") originLatLng: String,
        @Query("destination") destinationLatLng: String,
        @Query("language") language: String = Locale.getDefault().language
    ): DirectionModel
}