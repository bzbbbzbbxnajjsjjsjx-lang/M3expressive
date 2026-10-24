package com.example.myexpressiveapp.domain.usecase

import com.example.myexpressiveapp.domain.model.HomeItem
import kotlinx.coroutines.flow.Flow

fun interface GetHomeItemsUseCase {
    operator fun invoke(): Flow<List<HomeItem>>
}
