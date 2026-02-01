package com.abhishek.animeapp.viewmodel

import androidx.core.util.Pair
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.animeapp.data.local.AnimeEntity
import com.abhishek.animeapp.data.repository.AnimeRepository
import com.abhishek.animeapp.data.util.AppLogger
import com.abhishek.animeapp.data.util.Result
import com.abhishek.animeapp.data.util.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Collections.emptyList

class AnimeViewModel(
    private val repository: AnimeRepository,
    private val logger: AppLogger
) : ViewModel() {

    // ---------------- STATE ----------------

    private val _animeList =
        MutableStateFlow<List<AnimeEntity>>(emptyList())
    val animeList: StateFlow<List<AnimeEntity>> = _animeList

    // ---------------- UI EVENTS (CHANNEL) ----------------

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    // ---------------- PAGINATION ----------------

    private var currentPage = 1
    private var hasNextPage = true
    private var isLoading = false

    private val pageBuffer = mutableListOf<AnimeEntity>()

    // ---------------- LOAD ----------------

    fun loadNextPage(isInternetAvailable: Boolean) {
        if (isLoading || !hasNextPage) return

        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            _uiEvent.send(UiEvent.Loading(true))

            val result = try {
                if (isInternetAvailable) {
                    repository.getAnimeFromApi(currentPage)
                } else {
                    _uiEvent.send(
                        UiEvent.ShowToast("No internet connection")
                    )
                    repository.getAnimeFromDb(currentPage)
                }
            } catch (e: Exception) {
                Result.Error(
                    exception = e,
                    userMessage = "Failed to load anime list"
                )
            }

            when (result) {

                is Result.Success -> {
                    when (val data = result.data) {

                        is Pair<*, *> -> {
                            val list = data.first as List<AnimeEntity>
                            val hasMore = data.second as Boolean
                            pageBuffer.addAll(list)
                            hasNextPage = hasMore
                        }

                        is List<*> -> {
                            val list = data as List<AnimeEntity>
                            if (list.isEmpty()) hasNextPage = false
                            pageBuffer.addAll(list)
                        }
                    }

                    _animeList.value = pageBuffer.toList()
                    currentPage++
                }

                is Result.Error -> {
                    logger.logError("AnimeViewModel", result.exception)
                    _uiEvent.send(
                        UiEvent.ShowToast(result.userMessage)
                    )
                }
            }

            isLoading = false
            _uiEvent.send(UiEvent.Loading(false))
        }
    }
}
