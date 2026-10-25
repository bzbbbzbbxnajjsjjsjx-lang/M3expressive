package com.example.myexpressiveapp.presentation.notes.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpressiveapp.domain.model.Note
import com.example.myexpressiveapp.domain.usecase.ArchiveNoteUseCase
import com.example.myexpressiveapp.domain.usecase.DeleteNoteUseCase
import com.example.myexpressiveapp.domain.usecase.GetNotesUseCase
import com.example.myexpressiveapp.presentation.navigation.NOTE_ID_ARG
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val STATE_STOP_TIMEOUT_MS = 5_000L

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    getNotes: GetNotesUseCase,
    private val archiveNote: ArchiveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val noteId: String = checkNotNull(savedStateHandle[NOTE_ID_ARG])

    private val deletedEvents = Channel<Unit>(Channel.BUFFERED)

    // Silme tamamlandığında bir kez yayınlanır; ekran geri döner.
    val deleted: Flow<Unit> = deletedEvents.receiveAsFlow()

    val uiState: StateFlow<NoteDetailUiState> = getNotes()
        .map<List<Note>, NoteDetailUiState> { notes ->
            notes.firstOrNull { it.id == noteId }?.let { NoteDetailUiState.Success(it) }
                ?: NoteDetailUiState.Error
        }
        .catch { emit(NoteDetailUiState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MS),
            initialValue = NoteDetailUiState.Loading,
        )

    fun onToggleArchived() {
        val current = uiState.value as? NoteDetailUiState.Success ?: return
        viewModelScope.launch { archiveNote(noteId, !current.note.isArchived) }
    }

    fun onDelete() {
        viewModelScope.launch {
            deleteNote(noteId)
            deletedEvents.send(Unit)
        }
    }
}
