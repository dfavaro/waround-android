package com.danielefavaro.waround.maps.domain

import com.danielefavaro.waround.base.util.Result
import com.danielefavaro.waround.data.entities.ArticlesModel
import com.danielefavaro.waround.data.entities.DirectionModel
import com.danielefavaro.waround.data.entities.ImageModel
import com.danielefavaro.waround.data.entities.PageModel

interface MapsRepository {
    suspend fun getNearbyArticles(
        lat: Double,
        long: Double,
        distance: Double
    ): Result<ArticlesModel>

    suspend fun getPageDetail(pageId: Long): Result<PageModel>

    suspend fun getImageInfo(imageTitle: String): Result<ImageModel>

    suspend fun getDirection(
        originLatLng: String,
        destinationLatLng: String
    ): Result<DirectionModel>
}