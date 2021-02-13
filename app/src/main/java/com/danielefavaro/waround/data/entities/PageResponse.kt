package com.danielefavaro.waround.data.entities

data class PageModel(
    val batchcomplete: String,
    val query: PageQueryModel
)

data class PageQueryModel(
    val pages: Map<String, PageQueryResultModel>
)

data class PageQueryResultModel(
    val pageid: Long,
    val title: String,
    val contentmodel: String,
    val pagelanguage: String,
    val pagelanguagehtmlcode: String,
    val pagelanguagedir: String,
    val touched: String,
    val lastrevid: Long,
    val length: Int,
    val description: String?,
    val descriptionsource: String,
    val images: List<PageImageModel>
)

data class PageImageModel(
    val title: String
)