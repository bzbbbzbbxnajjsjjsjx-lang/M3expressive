package com.example.myexpressiveapp.presentation.notes.detail

import com.example.myexpressiveapp.domain.model.Note

sealed interface NoteDetailUiState {
    data object Loading : NoteDetailUiState

    data class Success(val note: Note) : NoteDetailUiState

    data object Error : NoteDetailUiState
}
