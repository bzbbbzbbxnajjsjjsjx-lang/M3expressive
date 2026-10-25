package com.example.myexpressiveapp.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.ThemeMode
import com.example.myexpressiveapp.presentation.theme.AppSpacing
import com.example.myexpressiveapp.presentation.theme.MyExpressiveTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsContent(uiState = uiState, onThemeSelected = viewModel::onThemeSelected)
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onThemeSelected: (ThemeMode) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(R.string.settings_title)) }) },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(AppSpacing.medium)) {
            when (uiState) {
                SettingsUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                SettingsUiState.Error -> Text(
                    text = stringResource(R.string.error_generic),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )

                is SettingsUiState.Success -> Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
                    ProfileCard()
                    Text(
                        text = stringResource(R.string.profile_theme_section),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    ThemeOption(
                        label = stringResource(R.string.theme_system),
                        selected = uiState.themeMode == ThemeMode.SYSTEM,
                        onSelect = { onThemeSelected(ThemeMode.SYSTEM) },
                    )
                    ThemeOption(
                        label = stringResource(R.string.theme_light),
                        selected = uiState.themeMode == ThemeMode.LIGHT,
                        onSelect = { onThemeSelected(ThemeMode.LIGHT) },
                    )
                    ThemeOption(
                        label = stringResource(R.string.theme_dark),
                        selected = uiState.themeMode == ThemeMode.DARK,
                        onSelect = { onThemeSelected(ThemeMode.DARK) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.large)) {
            Text(text = stringResource(R.string.profile_name), style = MaterialTheme.typography.headlineSmall)
            Text(
                text = stringResource(R.string.profile_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ThemeOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton)
            .padding(vertical = AppSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = AppSpacing.small),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    MyExpressiveTheme {
        SettingsContent(uiState = SettingsUiState.Success(ThemeMode.SYSTEM), onThemeSelected = {})
    }
}
