package com.example.myexpressiveapp.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpressiveapp.domain.model.ThemeMode
import com.example.myexpressiveapp.presentation.navigation.AppNavHost
import com.example.myexpressiveapp.presentation.theme.MyExpressiveTheme

@Composable
fun AppRoot(viewModel: AppViewModel = hiltViewModel()) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MyExpressiveTheme(darkTheme = darkTheme) {
        AppNavHost()
    }
}
