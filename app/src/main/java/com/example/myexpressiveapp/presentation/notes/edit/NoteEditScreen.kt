package com.example.myexpressiveapp.presentation.notes.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.myexpressiveapp.presentation.theme.AppSpacing
import com.example.myexpressiveapp.presentation.theme.MyExpressiveTheme

@Composable
fun NoteEditScreen(
    onClose: () -> Unit,
    onSaved: () -> Unit,
    viewModel: NoteEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onSaved()
    }
    NoteEditContent(
        uiState = uiState,
        onClose = onClose,
        onTitleChange = viewModel::onTitleChange,
        onContentChange = viewModel::onContentChange,
        onCategoryChange = viewModel::onCategoryChange,
        onSave = viewModel::onSave,
    )
}

@Composable
private fun NoteEditContent(
    uiState: NoteEditUiState,
    onClose: () -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    FilledTonalButton(onClick = onClose, shape = MaterialTheme.shapes.medium) {
                        Text(text = stringResource(R.string.editor_close))
                    }
                },
                title = {},
                actions = {
                    Button(
                        onClick = onSave,
                        enabled = uiState.status == NoteEditUiState.Status.Success && !uiState.isSaving,
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(text = stringResource(R.string.editor_save))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { innerPadding ->
        when (uiState.status) {
            NoteEditUiState.Status.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            NoteEditUiState.Status.Error -> Text(
                text = stringResource(R.string.note_not_found),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(innerPadding).padding(AppSpacing.medium),
            )

            NoteEditUiState.Status.Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(AppSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
            ) {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChange,
                    label = { Text(text = stringResource(R.string.editor_title_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = uiState.category,
                    onValueChange = onCategoryChange,
                    label = { Text(text = stringResource(R.string.editor_category_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = uiState.content,
                    onValueChange = onContentChange,
                    label = { Text(text = stringResource(R.string.editor_content_label)) },
                    minLines = EDITOR_CONTENT_MIN_LINES,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (uiState.isInvalid) {
                    Text(
                        text = stringResource(R.string.editor_invalid),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

private const val EDITOR_CONTENT_MIN_LINES = 8

@Preview(showBackground = true)
@Composable
private fun NoteEditPreview() {
    MyExpressiveTheme {
        NoteEditContent(
            uiState = NoteEditUiState(title = "Yeni not", category = "Kişisel"),
            onClose = {},
            onTitleChange = {},
            onContentChange = {},
            onCategoryChange = {},
            onSave = {},
        )
    }
}
