package com.example.myexpressiveapp.di

import com.example.myexpressiveapp.domain.model.HomeItem
import com.example.myexpressiveapp.domain.usecase.GetHomeItemsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.flowOf

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    // Veri katmanı henüz yok; repository bağlanana kadar boş liste döner.
    @Provides
    fun provideGetHomeItemsUseCase(): GetHomeItemsUseCase =
        GetHomeItemsUseCase { flowOf(emptyList<HomeItem>()) }
}
