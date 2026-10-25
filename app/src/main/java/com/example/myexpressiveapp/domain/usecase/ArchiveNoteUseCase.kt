package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.repository.NoteRepository

class ArchiveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: String, archived: Boolean) = repository.setArchived(id, archived)
}
