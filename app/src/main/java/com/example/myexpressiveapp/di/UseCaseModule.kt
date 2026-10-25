package com.example.myexpressiveapp.di

import com.example.myexpressiveapp.domain.repository.NoteRepository
import com.example.myexpressiveapp.domain.repository.SettingsRepository
import com.example.myexpressiveapp.domain.usecase.ArchiveNoteUseCase
import com.example.myexpressiveapp.domain.usecase.DeleteNoteUseCase
import com.example.myexpressiveapp.domain.usecase.GetNotesUseCase
import com.example.myexpressiveapp.domain.usecase.ObserveThemeModeUseCase
import com.example.myexpressiveapp.domain.usecase.SaveNoteUseCase
import com.example.myexpressiveapp.domain.usecase.SetThemeModeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Use case'ler saf Kotlin sınıflarıdır; Hilt bağlaması bu modülde yapılır.
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetNotesUseCase(repository: NoteRepository): GetNotesUseCase = GetNotesUseCase(repository)

    @Provides
    fun provideSaveNoteUseCase(repository: NoteRepository): SaveNoteUseCase = SaveNoteUseCase(repository)

    @Provides
    fun provideDeleteNoteUseCase(repository: NoteRepository): DeleteNoteUseCase = DeleteNoteUseCase(repository)

    @Provides
    fun provideArchiveNoteUseCase(repository: NoteRepository): ArchiveNoteUseCase = ArchiveNoteUseCase(repository)

    @Provides
    fun provideObserveThemeModeUseCase(repository: SettingsRepository): ObserveThemeModeUseCase =
        ObserveThemeModeUseCase(repository)

    @Provides
    fun provideSetThemeModeUseCase(repository: SettingsRepository): SetThemeModeUseCase =
        SetThemeModeUseCase(repository)
}
