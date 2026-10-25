package com.example.myexpressiveapp.data.repository

import com.example.myexpressiveapp.data.local.TaskSeedDataSource
import com.example.myexpressiveapp.domain.model.Task
import com.example.myexpressiveapp.domain.repository.TaskRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Görevler bellekte tutulur; kalıcı depolama eklendiğinde bu sınıf değiştirilir.
@Singleton
class InMemoryTaskRepository @Inject constructor(
    seedDataSource: TaskSeedDataSource,
) : TaskRepository {

    private val tasks = MutableStateFlow(seedDataSource.tasks())

    override fun observeTasks(): Flow<List<Task>> = tasks.asStateFlow()

    override suspend fun addTask(title: String, description: String, category: String) {
        val task = Task(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            category = category,
            isCompleted = false,
        )
        tasks.update { current -> listOf(task) + current }
    }

    override suspend fun deleteTask(id: String) {
        tasks.update { current -> current.filterNot { it.id == id } }
    }

    override suspend fun toggleTask(id: String) {
        tasks.update { current ->
            current.map { task -> if (task.id == id) task.copy(isCompleted = !task.isCompleted) else task }
        }
    }
}
