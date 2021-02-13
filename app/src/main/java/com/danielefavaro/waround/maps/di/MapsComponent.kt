package com.danielefavaro.waround.maps.di

import com.danielefavaro.waround.maps.di.module.MapsBindingModule
import com.danielefavaro.waround.maps.ui.ArticleDetailFragment
import com.danielefavaro.waround.maps.ui.MapsFragment
import dagger.Subcomponent

@MapsScope
@Subcomponent(
    modules = [
        MapsBindingModule::class
    ]
)
interface MapsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): MapsComponent
    }

    fun inject(mapsFragment: MapsFragment)
    fun inject(articleFragment: ArticleDetailFragment)
}