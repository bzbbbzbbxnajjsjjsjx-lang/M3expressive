package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.model.Note
import com.example.myexpressiveapp.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repository.observeNotes()
}
