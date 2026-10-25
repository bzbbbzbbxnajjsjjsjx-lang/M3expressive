@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.example.myexpressiveapp.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
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
import com.example.myexpressiveapp.presentation.add.AddTaskScreen
import com.example.myexpressiveapp.presentation.detail.DETAIL_ITEM_ID_ARG
import com.example.myexpressiveapp.presentation.detail.HomeDetailScreen
import com.example.myexpressiveapp.presentation.home.HomeScreen
import com.example.myexpressiveapp.presentation.profile.ProfileScreen
import com.example.myexpressiveapp.presentation.search.SearchScreen

private const val HOME_ROUTE = "home"
private const val SEARCH_ROUTE = "search"
private const val PROFILE_ROUTE = "profile"
private const val ADD_ROUTE = "add"
private const val DETAIL_ROUTE = "detail/{$DETAIL_ITEM_ID_ARG}"

// Alt gezinme çubuğundaki sekmeler; yalnızca bu rotalarda çubuk görünür.
enum class TopLevelDestination(val route: String, val labelRes: Int, val glyph: String) {
    HOME(HOME_ROUTE, R.string.tab_home, "●"),
    SEARCH(SEARCH_ROUTE, R.string.tab_search, "◎"),
    PROFILE(PROFILE_ROUTE, R.string.tab_profile, "◯"),
}

// Ekran geçişi yatay ekseninde kayar; kayma mesafesi ekran genişliğinin bu kadar bölümüdür.
private const val SHARED_AXIS_OFFSET_DIVISOR = 4

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val motion = remember { MotionScheme.expressive() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = TopLevelDestination.entries.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination?.route == destination.route,
                            onClick = { navController.navigateToTopLevel(destination.route) },
                            icon = { Text(text = destination.glyph) },
                            label = { Text(text = stringResource(destination.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HOME_ROUTE,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(
                route = HOME_ROUTE,
                enterTransition = { tabEnter(motion) },
                exitTransition = { tabExit(motion) },
            ) {
                HomeScreen(
                    onItemClick = { itemId -> navController.navigate(detailRoute(itemId)) },
                    onAddClick = { navController.navigate(ADD_ROUTE) },
                )
            }
            composable(
                route = SEARCH_ROUTE,
                enterTransition = { tabEnter(motion) },
                exitTransition = { tabExit(motion) },
            ) {
                SearchScreen(onItemClick = { itemId -> navController.navigate(detailRoute(itemId)) })
            }
            composable(
                route = PROFILE_ROUTE,
                enterTransition = { tabEnter(motion) },
                exitTransition = { tabExit(motion) },
            ) {
                ProfileScreen()
            }
            composable(
                route = ADD_ROUTE,
                enterTransition = { sharedAxisEnter(motion) },
                exitTransition = { sharedAxisExit(motion) },
                popEnterTransition = { sharedAxisEnter(motion) },
                popExitTransition = { sharedAxisExit(motion) },
            ) {
                AddTaskScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack(HOME_ROUTE, inclusive = false) },
                )
            }
            composable(
                route = DETAIL_ROUTE,
                arguments = listOf(navArgument(DETAIL_ITEM_ID_ARG) { type = NavType.StringType }),
                enterTransition = { sharedAxisEnter(motion) },
                exitTransition = { sharedAxisExit(motion) },
                popEnterTransition = { sharedAxisEnter(motion) },
                popExitTransition = { sharedAxisExit(motion) },
            ) {
                HomeDetailScreen(onBack = { navController.popBackStack() })
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

private fun detailRoute(itemId: String): String = "detail/$itemId"

// Sekmeler arası geçiş için yalnızca opaklık; spring süresi effects spring'den gelir.
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
