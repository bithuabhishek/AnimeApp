package com.abhishek.animeapp

import android.app.Application
import androidx.room.Room
import com.abhishek.animeapp.data.cache.AnimeDetailsCache
import com.abhishek.animeapp.data.local.AnimeDatabase
import com.abhishek.animeapp.data.local.MIGRATION_1_2
import com.abhishek.animeapp.data.util.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyApplication : Application() {

    lateinit var database: AnimeDatabase
        private set

    lateinit var animeDetailsCache: AnimeDetailsCache
        private set

    lateinit var logger: AppLogger
        private set


    override fun onCreate() {
        super.onCreate()

        logger = AppLogger()

        database = Room.databaseBuilder(
            applicationContext,
            AnimeDatabase::class.java,
            "anime_db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        animeDetailsCache = AnimeDetailsCache()

        preloadAnimeDetailsCache()
    }

    private fun preloadAnimeDetailsCache() {
        CoroutineScope(Dispatchers.IO).launch {
            val ids = database.animeDetailsDao().getAllCachedIds()
            animeDetailsCache.preload(ids)
        }
    }
}
