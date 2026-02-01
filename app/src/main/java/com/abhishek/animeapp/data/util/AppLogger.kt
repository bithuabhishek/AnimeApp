package com.abhishek.animeapp.data.util

import android.util.Log

class AppLogger {

    fun logInfo(tag: String, message: String) {
        Log.i(tag, message)
    }

    fun logError(tag: String, throwable: Throwable) {
        Log.e(tag, throwable.message, throwable)
    }
}
