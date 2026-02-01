package com.abhishek.animeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnimeDetailsDao {

    @Query("SELECT * FROM anime_details WHERE id = :id")
    suspend fun getAnimeDetails(id: Int): AnimeDetailsEntity?

    @Query("SELECT id FROM anime_details")
    suspend fun getAllCachedIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeDetails(details: AnimeDetailsEntity)
}
