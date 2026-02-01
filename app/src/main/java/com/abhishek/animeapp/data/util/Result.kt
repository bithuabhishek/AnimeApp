package com.abhishek.animeapp.data.util

sealed class Result<out T> {

    data class Success<T>(val data: T) : Result<T>()

    data class Error(
        val exception: Throwable,
        val userMessage: String
    ) : Result<Nothing>()
}
