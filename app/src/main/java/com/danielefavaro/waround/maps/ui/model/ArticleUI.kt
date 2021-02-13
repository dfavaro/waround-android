package com.danielefavaro.waround.maps.ui.model

import com.danielefavaro.waround.data.entities.PageQueryResultModel

data class ArticleUI(
    var pageid: Long = 0,
    var title: String = "",
    var description: String = "",
    var imagesUrl: MutableList<String> = mutableListOf()
)

fun ArticleUI.fromApi(pageQueryResultModel: PageQueryResultModel) = apply {
    pageid = pageQueryResultModel.pageid
    title = pageQueryResultModel.title
    description = pageQueryResultModel.description.orEmpty()
}