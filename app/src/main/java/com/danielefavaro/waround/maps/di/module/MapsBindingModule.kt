package com.danielefavaro.waround.maps.di.module

import androidx.lifecycle.ViewModel
import com.danielefavaro.waround.di.scope.ViewModelKey
import com.danielefavaro.waround.maps.data.MapsRepositoryImpl
import com.danielefavaro.waround.maps.data.source.MapsRemoteDataSource
import com.danielefavaro.waround.maps.data.source.MapsRemoteDataSourceImpl
import com.danielefavaro.waround.maps.domain.MapsRepository
import com.danielefavaro.waround.maps.ui.viewmodel.ArticleDetailViewModel
import com.danielefavaro.waround.maps.ui.viewmodel.MapsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MapsBindingModule {

    @Binds
    @IntoMap
    @ViewModelKey(MapsViewModel::class)
    abstract fun bindsMapsViewModel(mapsViewModel: MapsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ArticleDetailViewModel::class)
    abstract fun bindsArticleViewModel(articleDetailViewModel: ArticleDetailViewModel): ViewModel

    @Binds
    abstract fun bindsMapsRepository(repository: MapsRepositoryImpl): MapsRepository

    @Binds
    abstract fun bindsMapsRemoteDataSource(source: MapsRemoteDataSourceImpl): MapsRemoteDataSource
}