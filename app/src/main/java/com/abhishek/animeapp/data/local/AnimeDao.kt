package com.abhishek.animeapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnimeDao {

    @Query("""
        SELECT * FROM anime
        ORDER BY id ASC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getAnimePage(
        limit: Int,
        offset: Int
    ): List<AnimeEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAnime(list: List<AnimeEntity>)


    @Query("DELETE FROM anime")
    suspend fun clearAnime()
}
