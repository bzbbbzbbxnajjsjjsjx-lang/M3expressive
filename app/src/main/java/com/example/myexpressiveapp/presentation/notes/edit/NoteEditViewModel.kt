package com.example.myexpressiveapp.presentation.notes.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpressiveapp.domain.model.Note
import com.example.myexpressiveapp.domain.usecase.GetNotesUseCase
import com.example.myexpressiveapp.domain.usecase.SaveNoteUseCase
import com.example.myexpressiveapp.presentation.navigation.NOTE_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Düzenleme (noteId var) ve yeni not (noteId yok) aynı ViewModel'i kullanır.
@HiltViewModel
class NoteEditViewModel @Inject constructor(
    private val getNotes: GetNotesUseCase,
    private val saveNote: SaveNoteUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val noteId: String? = savedStateHandle[NOTE_ID_ARG]

    private val _uiState = MutableStateFlow(
        NoteEditUiState(status = if (noteId == null) NoteEditUiState.Status.Success else NoteEditUiState.Status.Loading),
    )
    val uiState: StateFlow<NoteEditUiState> = _uiState.asStateFlow()

    init {
        if (noteId != null) loadExisting(noteId)
    }

    private fun loadExisting(id: String) {
        viewModelScope.launch {
            val note = runCatching { getNotes().first().firstOrNull { it.id == id } }.getOrNull()
            if (note == null) {
                _uiState.update { it.copy(status = NoteEditUiState.Status.Error) }
            } else {
                _uiState.update { it.withNote(note) }
            }
        }
    }

    private fun NoteEditUiState.withNote(note: Note): NoteEditUiState =
        copy(
            status = NoteEditUiState.Status.Success,
            title = note.title,
            content = note.content,
            category = note.category,
        )

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, isInvalid = false) }
    }

    fun onContentChange(value: String) {
        _uiState.update { it.copy(content = value) }
    }

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value, isInvalid = false) }
    }

    fun onSave() {
        val draft = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val saved = saveNote(noteId, draft.title, draft.content, draft.category)
            _uiState.update { it.copy(isSaving = false, isInvalid = !saved, isSaved = saved) }
        }
    }
}
