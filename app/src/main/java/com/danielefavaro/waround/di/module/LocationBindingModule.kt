package com.danielefavaro.waround.di.module

import com.danielefavaro.waround.data.LocationRepositoryImpl
import com.danielefavaro.waround.domain.LocationRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class LocationBindingModule {

    @Singleton
    @Binds
    abstract fun bindsLocationRepository(repository: LocationRepositoryImpl): LocationRepository
}