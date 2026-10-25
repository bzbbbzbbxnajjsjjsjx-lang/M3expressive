package com.example.myexpressiveapp.domain.repository

import com.example.myexpressiveapp.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(): Flow<List<Note>>

    // id null ise yeni not oluşturulur; değilse mevcut notun içeriği güncellenir.
    suspend fun saveNote(id: String?, title: String, content: String, category: String)

    suspend fun deleteNote(id: String)

    suspend fun setArchived(id: String, archived: Boolean)
}
