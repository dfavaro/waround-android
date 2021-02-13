package com.danielefavaro.waround.maps.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.danielefavaro.waround.BuildConfig
import com.danielefavaro.waround.base.StatefulViewModel
import com.danielefavaro.waround.base.util.Result
import com.danielefavaro.waround.data.entities.PageQueryResultModel
import com.danielefavaro.waround.maps.domain.MapsRepository
import com.danielefavaro.waround.maps.ui.model.ArticleDetailFragmentEvents
import com.danielefavaro.waround.maps.ui.model.ArticleDetailFragmentViewState
import com.danielefavaro.waround.maps.ui.model.ArticleUI
import com.danielefavaro.waround.maps.ui.model.fromApi
import kotlinx.coroutines.launch
import javax.inject.Inject

class ArticleDetailViewModel @Inject constructor(
    private val mapsRepository: MapsRepository
) : StatefulViewModel<ArticleDetailFragmentViewState>(ArticleDetailFragmentViewState()) {

    fun getPageDetail(pageId: Long) {
        viewModelScope.launch {
            when (val result = mapsRepository.getPageDetail(pageId)) {
                is Result.Success -> {
                    result.data.query.pages.values.firstOrNull()?.let { pageQueryResultModel ->
                        val articleUI = ArticleUI().fromApi(pageQueryResultModel)
                        articleUI.imagesUrl.addAll(getImageInfo(pageQueryResultModel))

                        setState {
                            it.copy(
                                isLoading = false,
                                articleUI = articleUI
                            )
                        }
                        sendEvent(ArticleDetailFragmentEvents.OnArticleDetailLoaded)
                    } ?: run {
                        sendEvent(ArticleDetailFragmentEvents.OnGenericError)
                    }
                }
                is Result.Error -> sendEvent(ArticleDetailFragmentEvents.OnGenericError)
            }
        }
    }

    private suspend fun getImageInfo(pageQueryResultModel: PageQueryResultModel) =
        mutableListOf<String>().apply {
            pageQueryResultModel.images.forEach { pageImageModel ->
                when (val result = mapsRepository.getImageInfo(pageImageModel.title)) {
                    is Result.Success -> result.data.query.pages.values.firstOrNull()?.let {
                        it.imageinfo.getOrNull(0)?.let {
                            add(it.url)
                        }
                    }
                    is Result.Error -> {
                        // Do nothing
                    }
                }
            }
        }

    fun onUrlClick() {
        val url = StringBuilder().apply {
            append(BuildConfig.WIKIPEDIA_PAGE_URL)
            append(viewState.articleUI.title)
        }.toString()
        sendEvent(ArticleDetailFragmentEvents.OnUrlClick(url))
    }

    fun onRouteStart() {
        sendEvent(ArticleDetailFragmentEvents.OnRouteStart)
    }

}