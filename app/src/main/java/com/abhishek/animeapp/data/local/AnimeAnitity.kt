package com.abhishek.animeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anime")
data class AnimeEntity(

    @PrimaryKey
    val id: Int,

    val title: String,
    val imageUrl: String?,
    val episodes: Int?,
    val rating: Double?
)
