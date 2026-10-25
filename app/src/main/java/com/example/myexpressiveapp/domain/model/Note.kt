package com.example.myexpressiveapp.domain.model

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: Long,
    val category: String,
    val isArchived: Boolean,
)
