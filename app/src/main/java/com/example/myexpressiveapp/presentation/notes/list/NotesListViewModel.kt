package com.example.myexpressiveapp.presentation.notes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpressiveapp.domain.model.Note
import com.example.myexpressiveapp.domain.usecase.GetNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val STATE_STOP_TIMEOUT_MS = 5_000L

@HiltViewModel
class NotesListViewModel @Inject constructor(
    getNotes: GetNotesUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val isSearching = MutableStateFlow(false)
    private val selectedCategory = MutableStateFlow<String?>(null)
    private val newestFirst = MutableStateFlow(true)

    private val content: Flow<NotesListUiState> = combine(
        getNotes(),
        query,
        isSearching,
        selectedCategory,
        newestFirst,
    ) { notes, currentQuery, searching, category, newest ->
        val active = notes.filterNot(Note::isArchived)
        val visible = active.filter { it.matches(currentQuery, category) }
        NotesListUiState.Success(
            notes = if (newest) visible.sortedByDescending(Note::timestamp) else visible.sortedBy(Note::timestamp),
            query = currentQuery,
            isSearching = searching,
            selectedCategory = category,
            categories = active.map(Note::category).distinct().sorted(),
            newestFirst = newest,
        )
    }

    val uiState: StateFlow<NotesListUiState> = content
        .catch { emit(NotesListUiState.Error) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MS),
            initialValue = NotesListUiState.Loading,
        )

    fun onSearchOpen() {
        isSearching.value = true
    }

    fun onSearchClose() {
        isSearching.value = false
        query.value = ""
        selectedCategory.value = null
    }

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onCategorySelected(category: String?) {
        selectedCategory.value = category
    }

    fun onToggleSort() {
        newestFirst.update { !it }
    }

    private fun Note.matches(currentQuery: String, category: String?): Boolean =
        (category == null || this.category == category) &&
            (currentQuery.isBlank() ||
                title.contains(currentQuery, ignoreCase = true) ||
                content.contains(currentQuery, ignoreCase = true))
}
