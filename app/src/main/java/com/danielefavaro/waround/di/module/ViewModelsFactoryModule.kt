package com.danielefavaro.waround.di.module

import androidx.lifecycle.ViewModel
import com.danielefavaro.waround.base.util.ViewModelsFactory
import dagger.Module
import dagger.Provides
import javax.inject.Provider

@Module
class ViewModelsFactoryModule {

    @Provides
    fun providesViewModelsFactory(creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) =
        ViewModelsFactory(creators)
}