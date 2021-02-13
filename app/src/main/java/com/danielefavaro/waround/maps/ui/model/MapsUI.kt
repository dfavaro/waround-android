package com.danielefavaro.waround.maps.ui.model

import com.danielefavaro.waround.data.entities.ArticleQueryResultModel

data class MapsUI(
    var pageid: Long = 0,
    var title: String = "",
    var lat: Double = 0.0,
    var lon: Double = 0.0,
    var dist: Double = 0.0
)

fun MapsUI.fromApi(article: ArticleQueryResultModel) = apply {
    pageid = article.pageid
    title = article.title
    lat = article.lat
    lon = article.lon
    dist = article.dist
}