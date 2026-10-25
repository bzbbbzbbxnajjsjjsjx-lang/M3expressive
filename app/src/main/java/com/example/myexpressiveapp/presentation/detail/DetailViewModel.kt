package com.example.myexpressiveapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpressiveapp.domain.model.HomeItem
import com.example.myexpressiveapp.domain.usecase.GetHomeItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

const val DETAIL_ITEM_ID_ARG = "itemId"

private const val STATE_STOP_TIMEOUT_MS = 5_000L

@HiltViewModel
class DetailViewModel @Inject constructor(
    getHomeItems: GetHomeItemsUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val itemId: String = checkNotNull(savedStateHandle[DETAIL_ITEM_ID_ARG])

    val uiState: StateFlow<DetailUiState> = getHomeItems()
        .map<List<HomeItem>, DetailUiState> { items ->
            items.firstOrNull { it.id == itemId }?.let { DetailUiState.Content(it) } ?: DetailUiState.NotFound
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_STOP_TIMEOUT_MS),
            initialValue = DetailUiState.Loading,
        )
}
