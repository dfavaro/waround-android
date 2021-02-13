package com.danielefavaro.waround.maps.ui.model

import com.danielefavaro.waround.base.OneTimeEvent

sealed class ArticleDetailFragmentEvents : OneTimeEvent {
    object OnGenericError : ArticleDetailFragmentEvents()
    data class OnUrlClick(val url: String) : ArticleDetailFragmentEvents()
    object OnRouteStart : ArticleDetailFragmentEvents()
    object OnArticleDetailLoaded : ArticleDetailFragmentEvents()
}