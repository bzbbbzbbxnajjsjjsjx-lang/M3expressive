@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.example.myexpressiveapp.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.Task
import com.example.myexpressiveapp.presentation.theme.AppSpacing

// Bekleyen ve tamamlanan görevleri iki bölüm halinde çizer. Durumsuzdur; veri dışarıdan gelir.
@Composable
fun TaskList(
    pending: List<Task>,
    completed: List<Task>,
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        if (pending.isNotEmpty()) {
            item(key = "header_pending") {
                SectionHeader(text = stringResource(R.string.section_pending, pending.size))
            }
            itemsIndexed(pending, key = { _, task -> task.id }) { index, task ->
                TaskRow(task = task, index = index, count = pending.size, onTaskClick = onTaskClick)
            }
        }
        if (completed.isNotEmpty()) {
            item(key = "header_completed") {
                SectionHeader(text = stringResource(R.string.section_completed, completed.size))
            }
            itemsIndexed(completed, key = { _, task -> task.id }) { index, task ->
                TaskRow(task = task, index = index, count = completed.size, onTaskClick = onTaskClick)
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = AppSpacing.small, vertical = AppSpacing.small),
    )
}

@Composable
private fun TaskRow(task: Task, index: Int, count: Int, onTaskClick: (String) -> Unit) {
    SegmentedListItem(
        onClick = { onTaskClick(task.id) },
        shapes = ListItemDefaults.segmentedShapes(index = index, count = count),
        overlineContent = { Text(text = task.category, style = MaterialTheme.typography.labelMedium) },
        supportingContent = { Text(text = task.description) },
        content = {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                ),
            )
        },
    )
}
