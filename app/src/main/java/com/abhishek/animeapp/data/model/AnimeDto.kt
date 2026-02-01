package com.abhishek.animeapp.data.model

data class AnimeDto(
    val mal_id: Int,
    val title: String,
    val score: Double?,
    val episodes: Int?,
    val images: ImagesDto
)

data class ImagesDto(
    val jpg: JpgImageDto
)

data class JpgImageDto(
    val image_url: String
)

