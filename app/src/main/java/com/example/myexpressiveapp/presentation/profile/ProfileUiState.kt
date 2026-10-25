package com.example.myexpressiveapp.presentation.profile

import com.example.myexpressiveapp.domain.model.ThemeMode

sealed interface ProfileUiState {
    data object Loading : ProfileUiState

    data class Content(val themeMode: ThemeMode) : ProfileUiState

    data class Error(val message: String) : ProfileUiState
}
