package com.abhishek.animeapp.data.repository

import android.content.Context
import android.net.Uri
import com.abhishek.animeapp.data.api.ApiService
import com.abhishek.animeapp.data.cache.AnimeDetailsCache
import com.abhishek.animeapp.data.local.AnimeDetailsDao
import com.abhishek.animeapp.data.local.AnimeDetailsEntity
import com.abhishek.animeapp.data.util.AppLogger
import com.abhishek.animeapp.data.util.Result

class AnimeDetailsRepository(
    private val apiService: ApiService,
    private val animeDetailsDao: AnimeDetailsDao,
    private val cache: AnimeDetailsCache,
    private val logger: AppLogger
) {

    suspend fun getAnimeDetails(
        animeId: Int,
        isInternetAvailable: Boolean
    ): Result<AnimeDetailsEntity> {

        return try {
            animeDetailsDao.getAnimeDetails(animeId)?.let {
                cache.put(animeId)
                return Result.Success(it)
            }

            if (!isInternetAvailable) {
                return Result.Error(
                    Exception("No internet connection"),
                    "No internet connection"
                )
            }

            val response = apiService.getAnimeDetail(animeId)
            val body = response.body()
                ?: return Result.Error(
                    IllegalStateException("Empty response"),
                    "Something went wrong"
                )

            val dto = body.data
            val trailerId = extractYoutubeVideoId(dto.trailer?.embed_url)

            val entity = AnimeDetailsEntity(
                id = dto.mal_id,
                title = dto.title,
                synopsis = dto.synopsis,
                episodes = dto.episodes,
                score = dto.score,
                genres = dto.genres.joinToString { it.name },
                trailerYoutubeId = trailerId
            )

            animeDetailsDao.insertAnimeDetails(entity)
            cache.put(animeId)

            Result.Success(entity)

        } catch (e: Exception) {
            logger.logError("AnimeDetailsRepo", e)
            Result.Error(e, "Failed to load anime details")
        }
    }

    private fun extractYoutubeVideoId(embedUrl: String?): String? {
        return try {
            embedUrl?.let {
                Uri.parse(it).pathSegments.getOrNull(1)
            }
        } catch (e: Exception) {
            logger.logError("TrailerParser", e)
            null
        }
    }
}
