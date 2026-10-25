package com.example.myexpressiveapp.data.local

import android.content.Context
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.domain.model.HomeItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// Örnek ana ekran verisi; metinler strings.xml dizilerinden okunur.
class HomeLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun items(): List<HomeItem> {
        val resources = context.resources
        val titles = resources.getStringArray(R.array.home_item_titles)
        val subtitles = resources.getStringArray(R.array.home_item_subtitles)
        val categories = resources.getStringArray(R.array.home_item_categories)
        return titles.indices.map { index ->
            HomeItem(
                id = "home_$index",
                title = titles[index],
                subtitle = subtitles[index],
                category = categories[index],
            )
        }
    }
}
