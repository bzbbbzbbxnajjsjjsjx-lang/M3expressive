package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.model.Task
import com.example.myexpressiveapp.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.observeTasks()
}
