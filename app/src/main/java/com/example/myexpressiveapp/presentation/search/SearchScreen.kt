package com.example.myexpressiveapp.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.Task
import com.example.myexpressiveapp.presentation.components.TaskList
import com.example.myexpressiveapp.presentation.theme.AppSpacing
import com.example.myexpressiveapp.presentation.theme.MyExpressiveTheme

@Composable
fun SearchScreen(
    onItemClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SearchContent(
        uiState = uiState,
        onItemClick = onItemClick,
        onQueryChange = viewModel::onQueryChange,
    )
}

@Composable
private fun SearchContent(
    uiState: SearchUiState,
    onItemClick: (String) -> Unit,
    onQueryChange: (String) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(R.string.tab_search)) }) },
    ) { innerPadding ->
        when (uiState) {
            SearchUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is SearchUiState.Error -> Text(
                text = uiState.message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(innerPadding).padding(AppSpacing.medium),
            )

            is SearchUiState.Content -> Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    label = { Text(text = stringResource(R.string.search_hint)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small),
                )
                SearchResults(uiState = uiState, onItemClick = onItemClick)
            }
        }
    }
}

@Composable
private fun SearchResults(uiState: SearchUiState.Content, onItemClick: (String) -> Unit) {
    val message = when {
        uiState.query.isBlank() -> stringResource(R.string.search_empty_hint)
        uiState.pending.isEmpty() && uiState.completed.isEmpty() -> stringResource(R.string.search_no_results)
        else -> null
    }
    if (message != null) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(AppSpacing.medium),
        )
    } else {
        TaskList(
            pending = uiState.pending,
            completed = uiState.completed,
            onTaskClick = onItemClick,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = AppSpacing.medium, vertical = AppSpacing.small),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchContentPreview() {
    MyExpressiveTheme {
        SearchContent(
            uiState = SearchUiState.Content(
                query = "toplantı",
                pending = listOf(Task("1", "Sabah toplantısı", "15 dakika", "Çalışma", isCompleted = false)),
                completed = emptyList(),
            ),
            onItemClick = {},
            onQueryChange = {},
        )
    }
}
