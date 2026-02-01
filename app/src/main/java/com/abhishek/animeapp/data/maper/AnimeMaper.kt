package com.abhishek.animeapp.data.mapper

import com.abhishek.animeapp.data.local.AnimeEntity
import com.abhishek.animeapp.data.model.AnimeDto

fun AnimeDto.toEntity(): AnimeEntity {
    return AnimeEntity(
        id = mal_id,
        title = title,
        imageUrl = images.jpg.image_url,
        episodes = episodes,
        rating = score
    )
}
