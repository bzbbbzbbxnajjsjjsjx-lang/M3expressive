@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.example.myexpressiveapp.presentation.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.Morph
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.HomeItem
import com.example.myexpressiveapp.presentation.components.MorphShape
import com.example.myexpressiveapp.presentation.theme.AppSpacing
import com.example.myexpressiveapp.presentation.theme.MyExpressiveTheme

@Composable
fun HomeScreen(
    onItemClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onItemClick = onItemClick,
        onSearchClick = viewModel::onSearchClick,
        onQueryChange = viewModel::onQueryChange,
        onFilterClick = viewModel::onFilterClick,
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onItemClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.home_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (uiState) {
                HomeUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is HomeUiState.Error -> Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(AppSpacing.medium),
                )

                is HomeUiState.Content -> Column(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isSearching) {
                        OutlinedTextField(
                            value = uiState.query,
                            onValueChange = onQueryChange,
                            label = { Text(text = stringResource(R.string.search_label)) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppSpacing.medium),
                        )
                    }
                    Text(
                        text = categoryLabel(uiState.selectedCategory),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small),
                    )
                    if (uiState.items.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_results),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(AppSpacing.medium),
                        )
                    } else {
                        HomeItemList(
                            items = uiState.items,
                            onItemClick = onItemClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            MorphingToolbar(
                onSearchClick = onSearchClick,
                onFilterClick = onFilterClick,
                modifier = Modifier.align(Alignment.BottomCenter).padding(AppSpacing.medium),
            )
        }
    }
}

@Composable
private fun categoryLabel(category: String?): String =
    if (category == null) {
        stringResource(R.string.filter_all)
    } else {
        stringResource(R.string.filter_category, category)
    }

@Composable
private fun HomeItemList(
    items: List<HomeItem>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = AppSpacing.medium,
            end = AppSpacing.medium,
            bottom = AppSpacing.toolbarClearance,
        ),
    ) {
        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
            SegmentedListItem(
                onClick = { onItemClick(item.id) },
                shapes = ListItemDefaults.segmentedShapes(index = index, count = items.size),
                overlineContent = { Text(text = item.category, style = MaterialTheme.typography.labelMedium) },
                supportingContent = { Text(text = item.subtitle) },
                content = { Text(text = item.title, style = MaterialTheme.typography.titleMedium) },
            )
        }
    }
}

@Composable
private fun MorphingToolbar(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var morphed by remember { mutableStateOf(false) }
    val morphProgress by animateFloatAsState(
        targetValue = if (morphed) 1f else 0f,
        animationSpec = MotionScheme.expressive().defaultSpatialSpec(),
        label = "fabMorph",
    )
    val fabMorph = remember { Morph(MaterialShapes.Cookie4Sided, MaterialShapes.Flower) }
    val fabShape = remember(morphProgress) { MorphShape(fabMorph, morphProgress) }

    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { morphed = !morphed },
                shape = fabShape,
            ) {
                Text(text = stringResource(R.string.action_add))
            }
        },
    ) {
        FilledTonalButton(onClick = onSearchClick) { Text(text = stringResource(R.string.action_search)) }
        FilledTonalButton(onClick = onFilterClick) { Text(text = stringResource(R.string.action_filter)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentLoadingPreview() {
    MyExpressiveTheme {
        HomeContent(
            uiState = HomeUiState.Loading,
            onSearchClick = {},
            onQueryChange = {},
            onFilterClick = {},
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentItemsPreview() {
    MyExpressiveTheme {
        HomeContent(
            uiState = HomeUiState.Content(
                items = listOf(
                    HomeItem(id = "1", title = "Sabah toplantısı", subtitle = "15 dakika", category = "Çalışma"),
                    HomeItem(id = "2", title = "Market alışverişi", subtitle = "Süt, yumurta", category = "Kişisel"),
                ),
                query = "",
                isSearching = false,
                selectedCategory = null,
                categories = listOf("Çalışma", "Kişisel"),
            ),
            onSearchClick = {},
            onQueryChange = {},
            onFilterClick = {},
            onItemClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentErrorPreview() {
    MyExpressiveTheme {
        HomeContent(
            uiState = HomeUiState.Error(message = "Bir hata oluştu"),
            onSearchClick = {},
            onQueryChange = {},
            onFilterClick = {},
            onItemClick = {},
        )
    }
}
