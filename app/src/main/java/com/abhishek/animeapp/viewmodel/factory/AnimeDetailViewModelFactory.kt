package com.abhishek.animeapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhishek.animeapp.data.repository.AnimeDetailsRepository
import com.abhishek.animeapp.data.util.AppLogger
import com.abhishek.animeapp.viewmodel.AnimeDetailViewModel

class AnimeDetailViewModelFactory(
    private val repository: AnimeDetailsRepository,
    private val logger: AppLogger,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimeDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnimeDetailViewModel(repository, logger) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
