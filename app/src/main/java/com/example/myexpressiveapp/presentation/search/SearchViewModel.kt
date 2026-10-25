package com.example.myexpressiveapp.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpressiveapp.domain.model.Task
import com.example.myexpressiveapp.domain.usecase.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private const val STATE_STOP_TIMEOUT_MS = 5_000L

@HiltViewModel
class SearchViewModel @Inject constructor(
    getTasks: GetTasksUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val content: Flow<SearchUiState> = combine(getTasks(), query) { tasks, currentQuery ->
        // Boş sorguda sonuç gösterilmez; kullanıcı önce bir şey yazmalı.
        val results = if (currentQuery.isBlank()) {
            emptyList()
        } else {
            tasks.filter { it.matches(currentQuery) }
        }
        SearchUiState.Content(
            query = currentQuery,
            pending = results.filterNot(Task::isCompleted),
            completed = results.filter(Task::isCompleted),
        )
    }

    val uiState: StateFlow<SearchUiState> = content
        .catch { emit(SearchUiState.Error(it.message.orEmpty())) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MS),
            initialValue = SearchUiState.Loading,
        )

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    private fun Task.matches(currentQuery: String): Boolean =
        title.contains(currentQuery, ignoreCase = true) ||
            description.contains(currentQuery, ignoreCase = true) ||
            category.contains(currentQuery, ignoreCase = true)
}
