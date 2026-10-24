package com.example.myexpressiveapp.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpressiveapp.domain.model.HomeItem
import com.example.myexpressiveapp.presentation.theme.AppSpacing

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(uiState = uiState)
}

@Composable
private fun HomeContent(uiState: HomeUiState) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Ana Ekran") }) },
    ) { innerPadding ->
        when (uiState) {
            HomeUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is HomeUiState.Error -> Text(
                text = uiState.message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(AppSpacing.medium),
            )

            is HomeUiState.Content -> LazyColumn(contentPadding = innerPadding) {
                items(uiState.items, key = HomeItem::id) { item ->
                    ListItem(
                        headlineContent = {
                            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                        },
                        supportingContent = { Text(text = item.subtitle) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    MaterialTheme {
        HomeContent(uiState = HomeUiState.Loading)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentItemsPreview() {
    MaterialTheme {
        HomeContent(
            uiState = HomeUiState.Content(
                items = listOf(
                    HomeItem(id = "1", title = "Başlık", subtitle = "Açıklama"),
                    HomeItem(id = "2", title = "Başka bir öğe", subtitle = "Kısa not"),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentErrorPreview() {
    MaterialTheme {
        HomeContent(uiState = HomeUiState.Error(message = "Bir hata oluştu"))
    }
}
