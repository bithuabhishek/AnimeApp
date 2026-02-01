package com.abhishek.animeapp.data.model

data class AnimeDetailDto(
    val mal_id: Int,
    val title: String,
    val synopsis: String?,
    val episodes: Int?,
    val score: Double?,
    val genres: List<GenreDto>,
    val trailer: TrailerDto?
)

data class GenreDto(
    val name: String
)

data class TrailerDto(
    val embed_url: String?
)
