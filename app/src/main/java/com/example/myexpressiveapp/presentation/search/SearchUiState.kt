package com.example.myexpressiveapp.presentation.search

import com.example.myexpressiveapp.domain.model.Task

sealed interface SearchUiState {
    data object Loading : SearchUiState

    data class Content(
        val query: String,
        val pending: List<Task>,
        val completed: List<Task>,
    ) : SearchUiState

    data class Error(val message: String) : SearchUiState
}
