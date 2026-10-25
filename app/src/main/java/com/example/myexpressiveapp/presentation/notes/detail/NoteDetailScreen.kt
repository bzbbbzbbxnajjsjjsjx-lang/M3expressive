@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myexpressiveapp.presentation.notes.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.Note
import com.example.myexpressiveapp.presentation.theme.AppSpacing
import com.example.myexpressiveapp.presentation.theme.MyExpressiveTheme
import java.text.DateFormat
import java.util.Date

@Composable
fun NoteDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.deleted.collect { onBack() }
    }
    NoteDetailContent(
        uiState = uiState,
        onBack = onBack,
        onEdit = onEdit,
        onToggleArchived = viewModel::onToggleArchived,
        onDelete = viewModel::onDelete,
    )
}

@Composable
private fun NoteDetailContent(
    uiState: NoteDetailUiState,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onToggleArchived: () -> Unit,
    onDelete: () -> Unit,
) {
    var showOptions by remember { mutableStateOf(false) }
    val archived = (uiState as? NoteDetailUiState.Success)?.note?.isArchived == true

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    FilledTonalButton(onClick = onBack, shape = MaterialTheme.shapes.medium) {
                        Text(text = stringResource(R.string.note_back))
                    }
                },
                title = {},
                actions = {
                    if (uiState is NoteDetailUiState.Success) {
                        TextButton(onClick = onToggleArchived) {
                            Text(
                                text = stringResource(
                                    if (archived) R.string.note_unarchive else R.string.note_archive,
                                ),
                            )
                        }
                        FilledTonalButton(
                            onClick = { showOptions = true },
                            shape = MaterialTheme.shapes.extraLarge,
                            modifier = Modifier.padding(end = AppSpacing.small),
                        ) {
                            Text(text = stringResource(R.string.note_more))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
        floatingActionButton = {
            if (uiState is NoteDetailUiState.Success) {
                FloatingActionButton(onClick = { onEdit(uiState.note.id) }) {
                    Text(text = stringResource(R.string.note_edit))
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (uiState) {
                NoteDetailUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                NoteDetailUiState.Error -> Text(
                    text = stringResource(R.string.note_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(AppSpacing.medium),
                )

                is NoteDetailUiState.Success -> NoteBody(note = uiState.note)
            }
        }
    }

    if (showOptions) {
        ModalBottomSheet(
            onDismissRequest = { showOptions = false },
            sheetState = rememberModalBottomSheetState(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(AppSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.small),
            ) {
                Text(
                    text = stringResource(R.string.note_sheet_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                FilledTonalButton(
                    onClick = {
                        showOptions = false
                        onDelete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.note_delete))
                }
            }
        }
    }
}

@Composable
private fun NoteBody(note: Note) {
    SelectionContainer {
        Column(
            modifier = Modifier.fillMaxSize().padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)) {
                Text(
                    text = note.category,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(note.timestamp)),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(text = note.content, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteDetailPreview() {
    MyExpressiveTheme {
        NoteDetailContent(
            uiState = NoteDetailUiState.Success(
                Note("1", "Alışveriş listesi", "Süt, yumurta, ekmek", 0L, "Kişisel", isArchived = false),
            ),
            onBack = {},
            onEdit = {},
            onToggleArchived = {},
            onDelete = {},
        )
    }
}
