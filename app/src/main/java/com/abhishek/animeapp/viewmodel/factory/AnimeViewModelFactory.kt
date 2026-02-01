package com.abhishek.animeapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhishek.animeapp.data.repository.AnimeRepository
import com.abhishek.animeapp.data.util.AppLogger
import com.abhishek.animeapp.viewmodel.AnimeViewModel

class AnimeViewModelFactory(
    private val repository: AnimeRepository,
    private val logger: AppLogger
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnimeViewModel(repository, logger) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
