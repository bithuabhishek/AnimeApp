package com.abhishek.animeapp.data.repository

import androidx.core.util.Pair
import com.abhishek.animeapp.data.api.ApiService
import com.abhishek.animeapp.data.local.AnimeDao
import com.abhishek.animeapp.data.local.AnimeEntity
import com.abhishek.animeapp.data.mapper.toEntity
import com.abhishek.animeapp.data.util.AppLogger
import com.abhishek.animeapp.data.util.Result


class AnimeRepository(
    private val apiService: ApiService,
    private val animeDao: AnimeDao,
    private val logger: AppLogger
) {

    private val PAGE_SIZE = 25

    suspend fun getAnimeFromApi(
        page: Int
    ): Result<Pair<List<AnimeEntity>, Boolean>> {

        return try {
            val response = apiService.getTopAnime(page, PAGE_SIZE)
            val entities = response.data.map { it.toEntity() }

            animeDao.insertAnime(entities)

            Result.Success(
                Pair(entities, response.pagination.has_next_page)
            )
        } catch (e: Exception) {
            logger.logError("AnimeRepoAPI", e)
            Result.Error(e, "Failed to load anime list")
        }
    }

    suspend fun getAnimeFromDb(page: Int): Result<List<AnimeEntity>> {
        return try {
            val offset = (page - 1) * PAGE_SIZE
            Result.Success(
                animeDao.getAnimePage(PAGE_SIZE, offset)
            )
        } catch (e: Exception) {
            logger.logError("AnimeRepoDB", e)
            Result.Error(e, "Failed to load cached anime")
        }
    }
}

