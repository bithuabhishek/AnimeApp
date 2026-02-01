package com.abhishek.animeapp.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS anime_details (
                id INTEGER NOT NULL,
                title TEXT NOT NULL,
                synopsis TEXT,
                episodes INTEGER,
                score REAL,
                genres TEXT NOT NULL,
                trailerYoutubeId TEXT,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
    }
}
