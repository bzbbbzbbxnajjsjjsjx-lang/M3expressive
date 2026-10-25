package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.repository.TaskRepository

class AddTaskUseCase(private val repository: TaskRepository) {

    // Başlık ve kategori boşsa kaydetmez; kaydedildiyse true döner.
    suspend operator fun invoke(title: String, description: String, category: String): Boolean {
        val cleanTitle = title.trim()
        val cleanCategory = category.trim()
        if (cleanTitle.isEmpty() || cleanCategory.isEmpty()) return false
        repository.addTask(cleanTitle, description.trim(), cleanCategory)
        return true
    }
}
