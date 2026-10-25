package com.example.myexpressiveapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpressiveapp.domain.model.ThemeMode
import com.example.myexpressiveapp.domain.usecase.ObserveThemeModeUseCase
import com.example.myexpressiveapp.domain.usecase.SetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val STATE_STOP_TIMEOUT_MS = 5_000L

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeThemeMode: ObserveThemeModeUseCase,
    private val setThemeMode: SetThemeModeUseCase,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = observeThemeMode()
        .map<ThemeMode, SettingsUiState> { SettingsUiState.Success(it) }
        .catch { emit(SettingsUiState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MS),
            initialValue = SettingsUiState.Loading,
        )

    fun onThemeSelected(mode: ThemeMode) {
        viewModelScope.launch { setThemeMode(mode) }
    }
}
