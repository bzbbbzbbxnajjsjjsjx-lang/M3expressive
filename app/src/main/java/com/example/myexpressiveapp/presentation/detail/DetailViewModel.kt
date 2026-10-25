package com.example.myexpressiveapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpressiveapp.domain.model.Task
import com.example.myexpressiveapp.domain.usecase.DeleteTaskUseCase
import com.example.myexpressiveapp.domain.usecase.GetTasksUseCase
import com.example.myexpressiveapp.domain.usecase.ToggleTaskUseCase
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

const val DETAIL_ITEM_ID_ARG = "itemId"

private const val STATE_STOP_TIMEOUT_MS = 5_000L

@HiltViewModel
class DetailViewModel @Inject constructor(
    getTasks: GetTasksUseCase,
    private val toggleTask: ToggleTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val itemId: String = checkNotNull(savedStateHandle[DETAIL_ITEM_ID_ARG])

    private val deletedEvents = Channel<Unit>(Channel.BUFFERED)

    // Silme tamamlandığında bir kez yayınlanır; ekran geri döner.
    val deleted: Flow<Unit> = deletedEvents.receiveAsFlow()

    val uiState: StateFlow<DetailUiState> = getTasks()
        .map<List<Task>, DetailUiState> { tasks ->
            tasks.firstOrNull { it.id == itemId }?.let { DetailUiState.Content(it) } ?: DetailUiState.NotFound
        }
        .catch { emit(DetailUiState.Error(it.message.orEmpty())) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MS),
            initialValue = DetailUiState.Loading,
        )

    fun onToggleCompleted() {
        viewModelScope.launch { toggleTask(itemId) }
    }

    fun onDelete() {
        viewModelScope.launch {
            deleteTask(itemId)
            deletedEvents.send(Unit)
        }
    }
}
