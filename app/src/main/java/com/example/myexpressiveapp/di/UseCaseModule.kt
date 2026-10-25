package com.example.myexpressiveapp.di

import com.example.myexpressiveapp.domain.repository.SettingsRepository
import com.example.myexpressiveapp.domain.repository.TaskRepository
import com.example.myexpressiveapp.domain.usecase.AddTaskUseCase
import com.example.myexpressiveapp.domain.usecase.DeleteTaskUseCase
import com.example.myexpressiveapp.domain.usecase.GetTasksUseCase
import com.example.myexpressiveapp.domain.usecase.ObserveThemeModeUseCase
import com.example.myexpressiveapp.domain.usecase.SetThemeModeUseCase
import com.example.myexpressiveapp.domain.usecase.ToggleTaskUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Use case'ler saf Kotlin sınıflarıdır; Hilt bağlaması bu modülde yapılır.
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetTasksUseCase(repository: TaskRepository): GetTasksUseCase = GetTasksUseCase(repository)

    @Provides
    fun provideAddTaskUseCase(repository: TaskRepository): AddTaskUseCase = AddTaskUseCase(repository)

    @Provides
    fun provideDeleteTaskUseCase(repository: TaskRepository): DeleteTaskUseCase = DeleteTaskUseCase(repository)

    @Provides
    fun provideToggleTaskUseCase(repository: TaskRepository): ToggleTaskUseCase = ToggleTaskUseCase(repository)

    @Provides
    fun provideObserveThemeModeUseCase(repository: SettingsRepository): ObserveThemeModeUseCase =
        ObserveThemeModeUseCase(repository)

    @Provides
    fun provideSetThemeModeUseCase(repository: SettingsRepository): SetThemeModeUseCase =
        SetThemeModeUseCase(repository)
}
