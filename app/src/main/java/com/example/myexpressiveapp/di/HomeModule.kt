package com.example.myexpressiveapp.di

import com.example.myexpressiveapp.data.local.HomeLocalDataSource
import com.example.myexpressiveapp.domain.usecase.GetHomeItemsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.flowOf

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    fun provideGetHomeItemsUseCase(source: HomeLocalDataSource): GetHomeItemsUseCase =
        GetHomeItemsUseCase { flowOf(source.items()) }
}
