package com.danielefavaro.waround.maps.ui.model

import com.danielefavaro.waround.base.ViewState

data class ArticleDetailFragmentViewState(
    var isLoading: Boolean = true,
    var articleUI: ArticleUI = ArticleUI()
) : ViewState