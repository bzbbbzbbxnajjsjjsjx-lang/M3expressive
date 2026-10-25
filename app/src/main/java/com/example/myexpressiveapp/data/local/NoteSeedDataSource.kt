package com.example.myexpressiveapp.data.local

import android.content.Context
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.Note
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// Başlangıç notları; metinler strings.xml dizilerinden okunur.
class NoteSeedDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun notes(now: Long): List<Note> {
        val resources = context.resources
        val titles = resources.getStringArray(R.array.note_seed_titles)
        val contents = resources.getStringArray(R.array.note_seed_contents)
        val categories = resources.getStringArray(R.array.note_seed_categories)
        return titles.indices.map { index ->
            Note(
                id = "seed_$index",
                title = titles[index],
                content = contents[index],
                timestamp = now - index * ONE_DAY_MILLIS,
                category = categories[index],
                isArchived = index == ARCHIVED_INDEX,
            )
        }
    }

    private companion object {
        const val ONE_DAY_MILLIS = 86_400_000L
        const val ARCHIVED_INDEX = 6
    }
}
