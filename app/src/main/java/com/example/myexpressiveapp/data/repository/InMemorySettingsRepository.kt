package com.example.myexpressiveapp.data.repository

import com.example.myexpressiveapp.domain.model.ThemeMode
import com.example.myexpressiveapp.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class InMemorySettingsRepository @Inject constructor() : SettingsRepository {

    private val themeMode = MutableStateFlow(ThemeMode.SYSTEM)

    override fun observeThemeMode(): Flow<ThemeMode> = themeMode.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        themeMode.value = mode
    }
}
