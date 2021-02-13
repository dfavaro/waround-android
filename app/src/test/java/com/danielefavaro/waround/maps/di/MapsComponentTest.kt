package com.danielefavaro.waround.maps.di

import com.danielefavaro.waround.base.BaseUnitTest
import com.danielefavaro.waround.maps.di.module.MapsBindingModuleTest
import com.danielefavaro.waround.maps.di.module.NetworkModuleTest
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
@Component(
    modules = [
        NetworkModuleTest::class,
        MapsBindingModuleTest::class,
    ]
)
interface MapsComponentTest {
    @Component.Factory
    interface Factory {
        fun create(): MapsComponentTest
    }

    fun inject(base: BaseUnitTest)
}