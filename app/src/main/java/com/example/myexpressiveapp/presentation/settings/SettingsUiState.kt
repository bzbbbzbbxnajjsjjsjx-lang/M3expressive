package com.example.myexpressiveapp.presentation.settings

import com.example.myexpressiveapp.domain.model.ThemeMode

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Success(val themeMode: ThemeMode) : SettingsUiState

    data object Error : SettingsUiState
}
