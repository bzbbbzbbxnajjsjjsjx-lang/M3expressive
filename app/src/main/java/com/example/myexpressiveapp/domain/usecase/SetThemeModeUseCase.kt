package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.model.ThemeMode
import com.example.myexpressiveapp.domain.repository.SettingsRepository

class SetThemeModeUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}
