package com.danielefavaro.waround.data.entities

data class ArticlesModel(
    val batchcomplete: String = "",
    val query: ArticleQueryModel = ArticleQueryModel()
)

data class ArticleQueryModel(
    val geosearch: List<ArticleQueryResultModel> = emptyList()
)

data class ArticleQueryResultModel(
    val pageid: Long,
    val ns: Int,
    val title: String,
    val lat: Double,
    val lon: Double,
    val dist: Double,
    val primary: String
)