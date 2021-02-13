package com.danielefavaro.waround.di.module

import com.danielefavaro.waround.data.service.GoogleService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class GoogleNetworkModule {

    @Singleton
    @Provides
    fun providesGoogleService(@Named("providesGoogleMapsRetrofit") retrofit: Retrofit): GoogleService =
        retrofit.create(GoogleService::class.java)
}