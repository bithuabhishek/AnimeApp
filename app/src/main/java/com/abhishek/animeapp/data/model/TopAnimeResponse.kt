package com.abhishek.animeapp.data.model

data class TopAnimeResponse(
    val pagination: PaginationDto,
    val data: List<AnimeDto>
)

