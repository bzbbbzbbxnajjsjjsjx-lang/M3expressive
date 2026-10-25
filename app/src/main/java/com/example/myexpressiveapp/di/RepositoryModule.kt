package com.example.myexpressiveapp.di

import com.example.myexpressiveapp.data.repository.InMemoryNoteRepository
import com.example.myexpressiveapp.data.repository.InMemorySettingsRepository
import com.example.myexpressiveapp.domain.repository.NoteRepository
import com.example.myexpressiveapp.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindNoteRepository(impl: InMemoryNoteRepository): NoteRepository

    @Binds
    abstract fun bindSettingsRepository(impl: InMemorySettingsRepository): SettingsRepository
}
