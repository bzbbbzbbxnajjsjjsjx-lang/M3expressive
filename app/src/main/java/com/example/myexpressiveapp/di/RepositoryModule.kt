package com.example.myexpressiveapp.di

import com.example.myexpressiveapp.data.repository.InMemorySettingsRepository
import com.example.myexpressiveapp.data.repository.InMemoryTaskRepository
import com.example.myexpressiveapp.domain.repository.SettingsRepository
import com.example.myexpressiveapp.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTaskRepository(impl: InMemoryTaskRepository): TaskRepository

    @Binds
    abstract fun bindSettingsRepository(impl: InMemorySettingsRepository): SettingsRepository
}
