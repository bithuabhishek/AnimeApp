package com.abhishek.animeapp.data.util

sealed class UiEvent {

    data class ShowToast(val message: String) : UiEvent()

    data class Loading(val isLoading: Boolean) : UiEvent()
}
