package com.abhishek.animeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.animeapp.data.local.AnimeDetailsEntity
import com.abhishek.animeapp.data.repository.AnimeDetailsRepository
import com.abhishek.animeapp.data.util.AppLogger
import com.abhishek.animeapp.data.util.Result
import com.abhishek.animeapp.data.util.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AnimeDetailViewModel(
    private val repository: AnimeDetailsRepository,
    private val logger: AppLogger
) : ViewModel() {

    // ---------------- STATE ----------------

    private val _animeDetails =
        MutableStateFlow<AnimeDetailsEntity?>(null)
    val animeDetails: StateFlow<AnimeDetailsEntity?> = _animeDetails

    // ---------------- UI EVENTS (CHANNEL) ----------------

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    // ---------------- LOAD ----------------

    fun loadAnimeDetails(
        animeId: Int,
        isInternetAvailable: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            _uiEvent.send(UiEvent.Loading(true))

            when (
                val result =
                    repository.getAnimeDetails(
                        animeId,
                        isInternetAvailable
                    )
            ) {
                is Result.Success -> {
                    _animeDetails.value = result.data
                }

                is Result.Error -> {
                    logger.logError(
                        tag = "AnimeDetailViewModel",
                        throwable = result.exception
                    )

                    _uiEvent.send(
                        UiEvent.ShowToast(result.userMessage)
                    )
                }
            }

            _uiEvent.send(UiEvent.Loading(false))
        }
    }
}
