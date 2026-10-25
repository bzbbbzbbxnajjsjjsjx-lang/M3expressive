package com.example.myexpressiveapp.presentation.add

data class AddTaskUiState(
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val isSaving: Boolean = false,
    val isInvalid: Boolean = false,
    val isSaved: Boolean = false,
)
