package com.example.myexpressiveapp.presentation.detail

import com.example.myexpressiveapp.domain.model.HomeItem

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Content(val item: HomeItem) : DetailUiState

    data object NotFound : DetailUiState
}
