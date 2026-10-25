package com.example.myexpressiveapp.presentation.detail

import com.example.myexpressiveapp.domain.model.Task

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Content(val task: Task) : DetailUiState

    data object NotFound : DetailUiState

    data class Error(val message: String) : DetailUiState
}
