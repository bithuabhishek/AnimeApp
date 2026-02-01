package com.abhishek.animeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime_details")
data class AnimeDetailsEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val synopsis: String?,
    val episodes: Int?,
    val score: Double?,
    val genres: String,        // comma separated
    val trailerYoutubeId: String?
)
