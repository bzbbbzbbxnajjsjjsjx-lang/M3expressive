package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.repository.NoteRepository

class SaveNoteUseCase(private val repository: NoteRepository) {

    // Başlık ve kategori zorunlu; içerik isteğe bağlıdır. Kaydedildiyse true döner.
    suspend operator fun invoke(id: String?, title: String, content: String, category: String): Boolean {
        val cleanTitle = title.trim()
        val cleanCategory = category.trim()
        if (cleanTitle.isEmpty() || cleanCategory.isEmpty()) return false
        repository.saveNote(id, cleanTitle, content.trim(), cleanCategory)
        return true
    }
}
