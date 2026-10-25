package com.example.myexpressiveapp.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.Task
import com.example.myexpressiveapp.presentation.theme.AppSpacing
import com.example.myexpressiveapp.presentation.theme.MyExpressiveTheme

@Composable
fun HomeDetailScreen(
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.deleted.collect { onBack() }
    }
    HomeDetailContent(
        uiState = uiState,
        onBack = onBack,
        onToggleCompleted = viewModel::onToggleCompleted,
        onDelete = viewModel::onDelete,
    )
}

@Composable
private fun HomeDetailContent(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.detail_title)) },
                navigationIcon = {
                    FilledTonalButton(onClick = onBack) { Text(text = stringResource(R.string.detail_back)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(AppSpacing.medium)) {
            when (uiState) {
                DetailUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                DetailUiState.NotFound -> Text(
                    text = stringResource(R.string.detail_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )

                is DetailUiState.Error -> Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )

                is DetailUiState.Content -> TaskDetail(
                    task = uiState.task,
                    onToggleCompleted = onToggleCompleted,
                    onDelete = onDelete,
                )
            }
        }
    }
}

@Composable
private fun TaskDetail(
    task: Task,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(
                modifier = Modifier.padding(AppSpacing.large),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.small),
            ) {
                Text(
                    text = task.category,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(text = task.title, style = MaterialTheme.typography.headlineLarge)
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
            Button(onClick = onToggleCompleted, modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        if (task.isCompleted) R.string.detail_reopen else R.string.detail_complete,
                    ),
                )
            }
            OutlinedButton(onClick = onDelete) {
                Text(text = stringResource(R.string.detail_delete))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeDetailContentPreview() {
    MyExpressiveTheme {
        HomeDetailContent(
            uiState = DetailUiState.Content(
                Task("1", "Sabah toplantısı", "Ekip senkronizasyonu, 15 dakika", "Çalışma", isCompleted = false),
            ),
            onBack = {},
            onToggleCompleted = {},
            onDelete = {},
        )
    }
}
