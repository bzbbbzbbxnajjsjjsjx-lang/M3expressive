package com.example.myexpressiveapp.presentation.home

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
import kotlinx.coroutines.flow.update

private const val STATE_STOP_TIMEOUT_MS = 5_000L

@HiltViewModel
class HomeViewModel @Inject constructor(
    getTasks: GetTasksUseCase,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val isSearching = MutableStateFlow(false)
    private val selectedCategory = MutableStateFlow<String?>(null)

    private val content: Flow<HomeUiState> = combine(
        getTasks(),
        query,
        isSearching,
        selectedCategory,
    ) { tasks, currentQuery, searching, category ->
        val visible = tasks.filter { it.matches(currentQuery, category) }
        HomeUiState.Content(
            pending = visible.filterNot(Task::isCompleted),
            completed = visible.filter(Task::isCompleted),
            query = currentQuery,
            isSearching = searching,
            selectedCategory = category,
            categories = tasks.map(Task::category).distinct(),
        )
    }

    val uiState: StateFlow<HomeUiState> = content
        .catch { emit(HomeUiState.Error(it.message.orEmpty())) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MS),
            initialValue = HomeUiState.Loading,
        )

    fun onSearchClick() {
        isSearching.update { !it }
        if (!isSearching.value) query.value = ""
    }

    fun onQueryChange(newQuery: String) {
        query.value = newQuery
    }

    fun onFilterClick() {
        val current = uiState.value as? HomeUiState.Content ?: return
        val options: List<String?> = listOf(null) + current.categories
        val next = options[(options.indexOf(current.selectedCategory) + 1) % options.size]
        selectedCategory.value = next
    }

    private fun Task.matches(currentQuery: String, category: String?): Boolean =
        (category == null || this.category == category) &&
            (currentQuery.isBlank() || title.contains(currentQuery, ignoreCase = true))
}
