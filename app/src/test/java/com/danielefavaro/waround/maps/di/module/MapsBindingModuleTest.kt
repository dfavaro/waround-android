package com.danielefavaro.waround.maps.di.module

import com.danielefavaro.waround.maps.data.MapsRepositoryImpl
import com.danielefavaro.waround.maps.data.source.MapsRemoteDataSource
import com.danielefavaro.waround.maps.data.source.MapsRemoteDataSourceImpl
import com.danielefavaro.waround.maps.domain.MapsRepository
import dagger.Binds
import dagger.Module

@Module
abstract class MapsBindingModuleTest {

    @Binds
    abstract fun bindsMapsRepository(repository: MapsRepositoryImpl): MapsRepository

    @Binds
    abstract fun bindsMapsDataSource(source: MapsRemoteDataSourceImpl): MapsRemoteDataSource
}