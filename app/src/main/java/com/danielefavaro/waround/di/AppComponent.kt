package com.danielefavaro.waround.di

import android.app.Application
import com.danielefavaro.waround.MainActivity
import com.danielefavaro.waround.di.module.*
import com.danielefavaro.waround.maps.di.MapsComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        WikipediaNetworkModule::class,
        GoogleNetworkModule::class,
        ViewModelsFactoryModule::class,
        LocationModule::class,
        LocationBindingModule::class,
        AppModule::class
    ]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    fun inject(application: Application)
    fun inject(activity: MainActivity)
    fun mapsFractory(): MapsComponent.Factory?
}