package com.example.myexpressiveapp.presentation.profile

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
class ProfileViewModel @Inject constructor(
    observeThemeMode: ObserveThemeModeUseCase,
    private val setThemeMode: SetThemeModeUseCase,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = observeThemeMode()
        .map<ThemeMode, ProfileUiState> { ProfileUiState.Content(it) }
        .catch { emit(ProfileUiState.Error(it.message.orEmpty())) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MS),
            initialValue = ProfileUiState.Loading,
        )

    fun onThemeSelected(mode: ThemeMode) {
        viewModelScope.launch { setThemeMode(mode) }
    }
}
