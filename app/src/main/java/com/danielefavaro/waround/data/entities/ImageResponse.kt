package com.danielefavaro.waround.data.entities

data class ImageModel(
    val batchcomplete: String,
    val query: ImageQueryModel
)

data class ImageQueryModel(
    val pages: Map<String, ImageQueryResultModel>
)

data class ImageQueryResultModel(
    val imageinfo: List<ImageInfo>
)

data class ImageInfo(
    val url: String,
    val descriptionurl: String,
    val descriptionshorturl: String
)