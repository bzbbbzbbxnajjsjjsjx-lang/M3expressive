package com.example.myexpressiveapp.data.repository

import com.example.myexpressiveapp.data.local.NoteSeedDataSource
import com.example.myexpressiveapp.domain.model.Note
import com.example.myexpressiveapp.domain.repository.NoteRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Notlar bellekte tutulur; kalıcı depolama eklendiğinde bu sınıf değiştirilir.
@Singleton
class InMemoryNoteRepository @Inject constructor(
    seedDataSource: NoteSeedDataSource,
) : NoteRepository {

    private val notes = MutableStateFlow(seedDataSource.notes(System.currentTimeMillis()))

    override fun observeNotes(): Flow<List<Note>> = notes.asStateFlow()

    override suspend fun saveNote(id: String?, title: String, content: String, category: String) {
        val now = System.currentTimeMillis()
        notes.update { current ->
            if (id == null) {
                val note = Note(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    content = content,
                    timestamp = now,
                    category = category,
                    isArchived = false,
                )
                listOf(note) + current
            } else {
                current.map { note ->
                    if (note.id == id) {
                        note.copy(title = title, content = content, category = category, timestamp = now)
                    } else {
                        note
                    }
                }
            }
        }
    }

    override suspend fun deleteNote(id: String) {
        notes.update { current -> current.filterNot { it.id == id } }
    }

    override suspend fun setArchived(id: String, archived: Boolean) {
        notes.update { current ->
            current.map { note -> if (note.id == id) note.copy(isArchived = archived) else note }
        }
    }
}
