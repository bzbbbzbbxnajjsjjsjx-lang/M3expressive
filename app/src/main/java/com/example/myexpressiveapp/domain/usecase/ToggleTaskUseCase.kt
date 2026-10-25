package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.repository.TaskRepository

class ToggleTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(id: String) = repository.toggleTask(id)
}
