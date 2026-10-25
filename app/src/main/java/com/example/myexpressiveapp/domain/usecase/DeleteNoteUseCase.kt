package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.repository.NoteRepository

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: String) = repository.deleteNote(id)
}
