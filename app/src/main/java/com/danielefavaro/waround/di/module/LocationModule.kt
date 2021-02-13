package com.danielefavaro.waround.di.module

import android.app.Application
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocationModule {

    @Singleton
    @Provides
    fun providesFusedLocationProvider(application: Application) =
        LocationServices.getFusedLocationProviderClient(application)

    @Singleton
    @Provides
    fun providesSettingsClient(application: Application) =
        LocationServices.getSettingsClient(application)
}