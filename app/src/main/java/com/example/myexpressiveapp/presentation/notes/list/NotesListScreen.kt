@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.example.myexpressiveapp.presentation.notes.list

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.graphics.shapes.Morph
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.Note
import com.example.myexpressiveapp.presentation.components.MorphShape
import com.example.myexpressiveapp.presentation.theme.AppSpacing
import com.example.myexpressiveapp.presentation.theme.MyExpressiveTheme
import java.text.DateFormat
import java.util.Date

@Composable
fun NotesListScreen(
    startSearching: Boolean,
    onNoteClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    viewModel: NotesListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(startSearching) {
        if (startSearching) viewModel.onSearchOpen()
    }
    NotesListContent(
        uiState = uiState,
        onNoteClick = onNoteClick,
        onCreateClick = onCreateClick,
        onQueryChange = viewModel::onQueryChange,
        onSearchOpen = viewModel::onSearchOpen,
        onSearchClose = viewModel::onSearchClose,
        onCategorySelected = viewModel::onCategorySelected,
        onToggleSort = viewModel::onToggleSort,
    )
}

@Composable
private fun NotesListContent(
    uiState: NotesListUiState,
    onNoteClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchOpen: () -> Unit,
    onSearchClose: () -> Unit,
    onCategorySelected: (String?) -> Unit,
    onToggleSort: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(start = AppSpacing.small).size(AppSpacing.avatarSize),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = stringResource(R.string.profile_initial),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                },
                title = { Text(text = stringResource(R.string.home_title)) },
                actions = {
                    if (uiState is NotesListUiState.Success) {
                        FilledTonalButton(
                            onClick = onToggleSort,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.padding(end = AppSpacing.small),
                        ) {
                            Text(
                                text = stringResource(
                                    if (uiState.newestFirst) R.string.sort_newest else R.string.sort_oldest,
                                ),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { innerPadding ->
        when (uiState) {
            NotesListUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            NotesListUiState.Error -> Text(
                text = stringResource(R.string.error_generic),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(innerPadding).padding(AppSpacing.medium),
            )

            is NotesListUiState.Success -> Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchField(
                        query = uiState.query,
                        isSearching = uiState.isSearching,
                        onQueryChange = onQueryChange,
                        onSearchOpen = onSearchOpen,
                        onSearchClose = onSearchClose,
                    )
                    if (uiState.isSearching) {
                        CategoryChips(
                            categories = uiState.categories,
                            selectedCategory = uiState.selectedCategory,
                            onCategorySelected = onCategorySelected,
                        )
                    }
                    if (uiState.notes.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_results),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(AppSpacing.medium),
                        )
                    } else {
                        NotesGrid(
                            notes = uiState.notes,
                            onNoteClick = onNoteClick,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                CreateMenu(
                    onCreateClick = onCreateClick,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(AppSpacing.medium),
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchOpen: () -> Unit,
    onSearchClose: () -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(text = stringResource(R.string.search_hint)) },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        trailingIcon = if (isSearching) {
            { TextButton(onClick = onSearchClose) { Text(text = stringResource(R.string.search_close)) } }
        } else {
            null
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small)
            .onFocusChanged { if (it.isFocused) onSearchOpen() },
    )
}

@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = AppSpacing.medium),
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text(text = stringResource(R.string.filter_all)) },
            )
        }
        items(categories) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(text = category) },
            )
        }
    }
}

@Composable
private fun NotesGrid(
    notes: List<Note>,
    onNoteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(GRID_COLUMNS),
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = AppSpacing.medium,
            end = AppSpacing.medium,
            bottom = AppSpacing.toolbarClearance,
        ),
        verticalItemSpacing = AppSpacing.small,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
    ) {
        items(notes, key = Note::id) { note ->
            NoteCard(note = note, onClick = { onNoteClick(note.id) })
        }
    }
}

@Composable
private fun NoteCard(note: Note, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier.padding(AppSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.small),
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = TITLE_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = SUMMARY_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = note.category,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = formatDate(note.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CreateMenu(onCreateClick: () -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val morphProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = MotionScheme.expressive().defaultSpatialSpec(),
        label = "createMenuMorph",
    )
    val fabMorph = remember { Morph(MaterialShapes.Cookie4Sided, MaterialShapes.Flower) }
    val fabShape = remember(morphProgress) { MorphShape(fabMorph, morphProgress) }

    FloatingActionButtonMenu(
        expanded = expanded,
        modifier = modifier,
        button = {
            FloatingActionButton(onClick = { expanded = !expanded }, shape = fabShape) {
                Text(text = stringResource(R.string.action_add))
            }
        },
    ) {
        FloatingActionButtonMenuItem(
            onClick = {
                expanded = false
                onCreateClick()
            },
            text = { Text(text = stringResource(R.string.create_text_note)) },
            icon = { Text(text = stringResource(R.string.create_text_glyph)) },
        )
    }
}

private fun formatDate(timestamp: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(timestamp))

private const val GRID_COLUMNS = 2
private const val TITLE_MAX_LINES = 2
private const val SUMMARY_MAX_LINES = 4

@Preview(showBackground = true)
@Composable
private fun NotesListPreview() {
    MyExpressiveTheme {
        NotesListContent(
            uiState = NotesListUiState.Success(
                notes = listOf(Note("1", "Alışveriş", "Süt, yumurta, ekmek", 0L, "Kişisel", false)),
                query = "",
                isSearching = false,
                selectedCategory = null,
                categories = listOf("Kişisel"),
                newestFirst = true,
            ),
            onNoteClick = {},
            onCreateClick = {},
            onQueryChange = {},
            onSearchOpen = {},
            onSearchClose = {},
            onCategorySelected = {},
            onToggleSort = {},
        )
    }
}
