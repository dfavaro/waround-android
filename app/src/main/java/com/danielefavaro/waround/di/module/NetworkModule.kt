package com.danielefavaro.waround.di.module

import android.app.Application
import com.danielefavaro.waround.BuildConfig
import com.danielefavaro.waround.R
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    @Named("providesWikipediaRetrofit")
    fun providesWikipediaRetrofit(@Named("providesHttpClient") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().apply {
            baseUrl(BuildConfig.WIKIPEDIA_BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
            client(okHttpClient)
        }.build()

    @Singleton
    @Provides
    @Named("providesGoogleMapsRetrofit")
    fun providesGoogleMapsRetrofit(@Named("providesGoogleHttpClient") okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().apply {
            baseUrl(BuildConfig.GOOGLE_BASE_URL)
            addConverterFactory(GsonConverterFactory.create())
            client(okHttpClient)
        }.build()

    @Provides
    @Singleton
    @Named("providesHttpClient")
    fun providesHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(httpLoggingInterceptor)
            }
        }.build()

    @Provides
    @Singleton
    @Named("providesGoogleHttpClient")
    fun providesGoogleHttpClient(
        application: Application,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ) =
        OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(httpLoggingInterceptor)
            }
            addInterceptor { chain ->
                val newRequest = chain.request().newBuilder().apply {
                    val newUrl = chain.request().url.newBuilder().apply {
                        addQueryParameter("key", application.getString(R.string.google_maps_key))
                    }.build()
                    url(newUrl)
                }.build()

                chain.proceed(newRequest)
            }
        }.build()

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}