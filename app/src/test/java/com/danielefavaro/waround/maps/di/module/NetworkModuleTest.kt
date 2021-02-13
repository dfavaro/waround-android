package com.danielefavaro.waround.maps.di.module

import com.danielefavaro.waround.base.util.UtilsTest
import com.danielefavaro.waround.data.service.GoogleService
import com.danielefavaro.waround.data.service.WikipediaService
import dagger.Module
import dagger.Provides
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val REQUEST_GET_IMAGE_INFO = "action=query&prop=imageinfo&iiprop=url"
private const val REQUEST_GET_NEARBY_ARTICLES = "action=query&list=geosearch"
private const val REQUEST_GET_PAGE_DETAIL = "action=query&prop=info"

private const val RESPONSE_GET_IMAGE_INFO = "getImageInfo"
private const val RESPONSE_GET_NEARBY_ARTICLES = "getNearbyArticles"
private const val RESPONSE_GET_PAGE_DETAIL = "getPageDetail"

@Module
class NetworkModuleTest {

    @Singleton
    @Provides
    fun providesGoogleService(retrofit: Retrofit): GoogleService =
        retrofit.create(GoogleService::class.java)

    @Singleton
    @Provides
    fun providesWikipediaService(retrofit: Retrofit): WikipediaService =
        retrofit.create(WikipediaService::class.java)

    @Singleton
    @Provides
    fun providesMockWebServer() = MockWebServer()

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient, mockWebServer: MockWebServer) =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(mockWebServer.url(("/")))
            .build()

    @Singleton
    @Provides
    fun providesHttpClient() = OkHttpClient().newBuilder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val uri = chain.request().url.toUri().toString()
            val responseString = when {
                // custom logic
                uri.contains(REQUEST_GET_IMAGE_INFO) -> UtilsTest.getJsonFromFile("$RESPONSE_GET_IMAGE_INFO.json")
                uri.contains(REQUEST_GET_NEARBY_ARTICLES) -> UtilsTest.getJsonFromFile("$RESPONSE_GET_NEARBY_ARTICLES.json")
                uri.contains(REQUEST_GET_PAGE_DETAIL) -> UtilsTest.getJsonFromFile("$RESPONSE_GET_PAGE_DETAIL.json")
                else -> ""
            }

            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message(responseString)
                .addHeader("content-type", "application/json")
                .body(responseString.toResponseBody("application/json; charset=utf-8".toMediaType()))
                .build()
        }
        .build()

}