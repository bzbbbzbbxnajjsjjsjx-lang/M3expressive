package com.example.myexpressiveapp.domain.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val isCompleted: Boolean,
)
