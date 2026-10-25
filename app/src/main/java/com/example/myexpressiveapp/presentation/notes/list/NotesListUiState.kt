package com.example.myexpressiveapp.presentation.notes.list

import com.example.myexpressiveapp.domain.model.Note

sealed interface NotesListUiState {
    data object Loading : NotesListUiState

    data class Success(
        val notes: List<Note>,
        val query: String,
        val isSearching: Boolean,
        val selectedCategory: String?,
        val categories: List<String>,
        val newestFirst: Boolean,
    ) : NotesListUiState

    data object Error : NotesListUiState
}
