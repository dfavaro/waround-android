package com.danielefavaro.waround.di.module

import com.danielefavaro.waround.maps.di.MapsComponent
import dagger.Module

@Module(
    subcomponents = [
        MapsComponent::class
    ]
)
class AppModule