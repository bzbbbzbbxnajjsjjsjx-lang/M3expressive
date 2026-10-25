package com.example.myexpressiveapp.domain.repository

import com.example.myexpressiveapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    suspend fun addTask(title: String, description: String, category: String)
    suspend fun deleteTask(id: String)
    suspend fun toggleTask(id: String)
}
