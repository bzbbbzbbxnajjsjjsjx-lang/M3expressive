package com.example.myexpressiveapp.data.local

import android.content.Context
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// Başlangıç görevleri; metinler strings.xml dizilerinden okunur.
class TaskSeedDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun tasks(): List<Task> {
        val resources = context.resources
        val titles = resources.getStringArray(R.array.home_item_titles)
        val descriptions = resources.getStringArray(R.array.home_item_subtitles)
        val categories = resources.getStringArray(R.array.home_item_categories)
        return titles.indices.map { index ->
            Task(
                id = "seed_$index",
                title = titles[index],
                description = descriptions[index],
                category = categories[index],
                isCompleted = index % COMPLETED_EVERY == 0,
            )
        }
    }

    private companion object {
        const val COMPLETED_EVERY = 4
    }
}
