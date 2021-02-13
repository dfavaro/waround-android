package com.danielefavaro.waround.maps.data

import com.danielefavaro.waround.base.util.Result
import com.danielefavaro.waround.data.entities.ArticlesModel
import com.danielefavaro.waround.data.entities.ImageModel
import com.danielefavaro.waround.data.entities.PageModel
import com.danielefavaro.waround.maps.data.source.MapsRemoteDataSourceImpl
import com.danielefavaro.waround.maps.domain.MapsRepository
import javax.inject.Inject

class MapsRepositoryImpl @Inject constructor(
    private val mapsRemoteDataSource: MapsRemoteDataSourceImpl
) : MapsRepository {

    override suspend fun getNearbyArticles(
        lat: Double,
        long: Double,
        distance: Double
    ): Result<ArticlesModel> =
        mapsRemoteDataSource.getNearbyArticles(lat, long, distance)

    override suspend fun getPageDetail(pageId: Long): Result<PageModel> =
        mapsRemoteDataSource.getPageDetail(pageId)

    override suspend fun getImageInfo(imageTitle: String): Result<ImageModel> =
        mapsRemoteDataSource.getImageInfo(imageTitle)

    override suspend fun getDirection(
        originLatLng: String,
        destinationLatLng: String
    ) = mapsRemoteDataSource.getDirection(originLatLng, destinationLatLng)
}