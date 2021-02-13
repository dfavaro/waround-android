package com.danielefavaro.waround.maps.data.source

import com.danielefavaro.waround.base.util.Result
import com.danielefavaro.waround.data.entities.ArticlesModel
import com.danielefavaro.waround.data.entities.ImageModel
import com.danielefavaro.waround.data.entities.PageModel
import com.danielefavaro.waround.data.service.GoogleService
import com.danielefavaro.waround.data.service.WikipediaService
import javax.inject.Inject

class MapsRemoteDataSourceImpl @Inject constructor(
    private val wikipediaService: WikipediaService,
    private val googleService: GoogleService
) : MapsRemoteDataSource {

    override suspend fun getNearbyArticles(
        lat: Double,
        long: Double,
        distance: Double
    ): Result<ArticlesModel> = try {
        Result.Success(
            wikipediaService.getNearbyArticles(
                String.format("%f|%f", lat, long),
                distance.toInt()
            )
        )
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getPageDetail(pageId: Long): Result<PageModel> = try {
        Result.Success(wikipediaService.getPageDetail(pageId))
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getImageInfo(imageTitle: String): Result<ImageModel> = try {
        Result.Success(wikipediaService.getImageInfo(imageTitle))
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getDirection(originLatLng: String, destinationLatLng: String) = try {
        Result.Success(googleService.getDirection(originLatLng, destinationLatLng))
    } catch (e: Exception) {
        Result.Error(e)
    }
}