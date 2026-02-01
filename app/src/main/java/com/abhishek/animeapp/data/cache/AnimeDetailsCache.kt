package com.abhishek.animeapp.data.cache

class AnimeDetailsCache {

    private val cache = mutableMapOf<Int, Boolean>()

    fun contains(id: Int): Boolean = cache[id] == true

    fun put(id: Int) {
        cache[id] = true
    }

    fun preload(ids: List<Int>) {
        ids.forEach { id ->
            cache[id] = true
        }
    }
}
