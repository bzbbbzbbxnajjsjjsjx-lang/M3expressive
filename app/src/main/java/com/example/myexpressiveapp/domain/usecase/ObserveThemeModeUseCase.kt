package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.model.ThemeMode
import com.example.myexpressiveapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ObserveThemeModeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<ThemeMode> = repository.observeThemeMode()
}
