package com.example.myexpressiveapp.presentation.home

import com.example.myexpressiveapp.domain.model.HomeItem

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Content(
        val items: List<HomeItem>,
        val query: String,
        val isSearching: Boolean,
        val selectedCategory: String?,
        val categories: List<String>,
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
