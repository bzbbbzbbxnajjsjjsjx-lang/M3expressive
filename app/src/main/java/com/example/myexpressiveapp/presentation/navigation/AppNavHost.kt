@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.example.myexpressiveapp.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myexpressiveapp.R
import com.example.myexpressiveapp.presentation.notes.detail.NoteDetailScreen
import com.example.myexpressiveapp.presentation.notes.edit.NoteEditScreen
import com.example.myexpressiveapp.presentation.notes.list.NotesListScreen
import com.example.myexpressiveapp.presentation.settings.SettingsScreen
import com.example.myexpressiveapp.presentation.theme.AppSpacing

private const val NOTES_LIST_ROUTE = "notes_list"
private const val NOTES_SEARCH_ROUTE = "notes_search"
private const val SETTINGS_ROUTE = "settings"
private const val NOTE_CREATE_ROUTE = "note_create"
private const val NOTE_DETAIL_ROUTE = "note_detail/{$NOTE_ID_ARG}"
private const val NOTE_EDIT_ROUTE = "note_edit/{$NOTE_ID_ARG}"

// Alt gezinme çubuğundaki sekmeler; çubuk yalnızca bu rotalarda görünür.
enum class TopLevelDestination(val route: String, val labelRes: Int, val glyph: String) {
    NOTES(NOTES_LIST_ROUTE, R.string.tab_notes, "●"),
    SEARCH(NOTES_SEARCH_ROUTE, R.string.tab_search, "◎"),
    SETTINGS(SETTINGS_ROUTE, R.string.tab_settings, "◯"),
}

// Ekran geçişi yatay ekseninde kayar; kayma mesafesi ekran genişliğinin bu kadar bölümüdür.
private const val SHARED_AXIS_OFFSET_DIVISOR = 4

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val motion = remember { MotionScheme.expressive() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = TopLevelDestination.entries.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.height(AppSpacing.navigationBarHeight),
                ) {
                    TopLevelDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = { navController.navigateToTopLevel(destination.route) },
                            icon = { Text(text = destination.glyph) },
                            label = { Text(text = stringResource(destination.labelRes)) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NOTES_LIST_ROUTE,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(
                route = NOTES_LIST_ROUTE,
                enterTransition = { tabEnter(motion) },
                exitTransition = { tabExit(motion) },
            ) {
                NotesListScreen(
                    startSearching = false,
                    onNoteClick = { id -> navController.navigate(noteDetailRoute(id)) },
                    onCreateClick = { navController.navigate(NOTE_CREATE_ROUTE) },
                )
            }
            composable(
                route = NOTES_SEARCH_ROUTE,
                enterTransition = { tabEnter(motion) },
                exitTransition = { tabExit(motion) },
            ) {
                NotesListScreen(
                    startSearching = true,
                    onNoteClick = { id -> navController.navigate(noteDetailRoute(id)) },
                    onCreateClick = { navController.navigate(NOTE_CREATE_ROUTE) },
                )
            }
            composable(
                route = SETTINGS_ROUTE,
                enterTransition = { tabEnter(motion) },
                exitTransition = { tabExit(motion) },
            ) {
                SettingsScreen()
            }
            composable(
                route = NOTE_DETAIL_ROUTE,
                arguments = listOf(navArgument(NOTE_ID_ARG) { type = NavType.StringType }),
                enterTransition = { sharedAxisEnter(motion) },
                exitTransition = { sharedAxisExit(motion) },
                popEnterTransition = { sharedAxisEnter(motion) },
                popExitTransition = { sharedAxisExit(motion) },
            ) {
                NoteDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(noteEditRoute(id)) },
                )
            }
            composable(
                route = NOTE_EDIT_ROUTE,
                arguments = listOf(navArgument(NOTE_ID_ARG) { type = NavType.StringType }),
                enterTransition = { sharedAxisEnter(motion) },
                exitTransition = { sharedAxisExit(motion) },
                popEnterTransition = { sharedAxisEnter(motion) },
                popExitTransition = { sharedAxisExit(motion) },
            ) {
                NoteEditScreen(
                    onClose = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
            composable(
                route = NOTE_CREATE_ROUTE,
                enterTransition = { sharedAxisEnter(motion) },
                exitTransition = { sharedAxisExit(motion) },
                popEnterTransition = { sharedAxisEnter(motion) },
                popExitTransition = { sharedAxisExit(motion) },
            ) {
                NoteEditScreen(
                    onClose = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun noteDetailRoute(id: String): String = "note_detail/$id"

private fun noteEditRoute(id: String): String = "note_edit/$id"

// Sekmeler arası geçiş için yalnızca opaklık; süre effects spring'den gelir.
private fun tabEnter(motion: MotionScheme): EnterTransition = fadeIn(animationSpec = motion.defaultEffectsSpec())

private fun tabExit(motion: MotionScheme): ExitTransition = fadeOut(animationSpec = motion.defaultEffectsSpec())

// Konum ve boyut için spatial spring (overshoot serbest), opaklık için effects spring kullanılır.
private fun sharedAxisEnter(motion: MotionScheme): EnterTransition =
    slideInHorizontally(animationSpec = motion.defaultSpatialSpec()) { fullWidth ->
        fullWidth / SHARED_AXIS_OFFSET_DIVISOR
    } + fadeIn(animationSpec = motion.defaultEffectsSpec())

private fun sharedAxisExit(motion: MotionScheme): ExitTransition =
    slideOutHorizontally(animationSpec = motion.defaultSpatialSpec()) { fullWidth ->
        -fullWidth / SHARED_AXIS_OFFSET_DIVISOR
    } + fadeOut(animationSpec = motion.defaultEffectsSpec())
