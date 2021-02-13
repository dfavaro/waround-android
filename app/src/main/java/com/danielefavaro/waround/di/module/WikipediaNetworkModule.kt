package com.danielefavaro.waround.di.module

import com.danielefavaro.waround.data.service.WikipediaService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class WikipediaNetworkModule {

    @Singleton
    @Provides
    fun providesWikipediaService(@Named("providesWikipediaRetrofit") retrofit: Retrofit): WikipediaService =
        retrofit.create(WikipediaService::class.java)
}