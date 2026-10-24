package com.example.myexpressiveapp.presentation.home

import com.example.myexpressiveapp.domain.model.HomeItem

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Content(val items: List<HomeItem>) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
