package com.example.myexpressiveapp.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpressiveapp.domain.usecase.AddTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val addTask: AddTaskUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTaskUiState())
    val uiState: StateFlow<AddTaskUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, isInvalid = false) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onCategoryChange(value: String) {
        _uiState.update { it.copy(category = value, isInvalid = false) }
    }

    fun onSave() {
        val draft = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val saved = addTask(draft.title, draft.description, draft.category)
            _uiState.update { it.copy(isSaving = false, isInvalid = !saved, isSaved = saved) }
        }
    }
}
