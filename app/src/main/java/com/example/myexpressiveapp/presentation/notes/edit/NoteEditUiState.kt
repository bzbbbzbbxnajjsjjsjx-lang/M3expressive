package com.example.myexpressiveapp.presentation.notes.edit

data class NoteEditUiState(
    val status: Status = Status.Success,
    val title: String = "",
    val content: String = "",
    val category: String = "",
    val isSaving: Boolean = false,
    val isInvalid: Boolean = false,
    val isSaved: Boolean = false,
) {
    enum class Status { Loading, Success, Error }
}
