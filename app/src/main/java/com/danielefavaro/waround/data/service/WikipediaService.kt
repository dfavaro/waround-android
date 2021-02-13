package com.danielefavaro.waround.data.service

import com.danielefavaro.waround.data.entities.ArticlesModel
import com.danielefavaro.waround.data.entities.ImageModel
import com.danielefavaro.waround.data.entities.PageModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaService {

    @GET("api.php?action=query&list=geosearch&gslimit=100&format=json")
    suspend fun getNearbyArticles(
        @Query("gscoord") location: String,
        @Query("gsradius") distance: Int
    ): ArticlesModel

    // TODO add image filter (only useful images)
    @GET("api.php?action=query&prop=info%7Cdescription%7Cimages&format=json")
    suspend fun getPageDetail(
        @Query("pageids") pageId: Long
    ): PageModel

    @GET("api.php?action=query&prop=imageinfo&iiprop=url&format=json")
    suspend fun getImageInfo(
        @Query("titles") imageTitle: String
    ): ImageModel
}