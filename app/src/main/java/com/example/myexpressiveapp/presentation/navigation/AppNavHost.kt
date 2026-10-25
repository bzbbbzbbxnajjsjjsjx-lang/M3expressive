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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myexpressiveapp.presentation.detail.DETAIL_ITEM_ID_ARG
import com.example.myexpressiveapp.presentation.detail.HomeDetailScreen
import com.example.myexpressiveapp.presentation.home.HomeScreen

private const val HOME_ROUTE = "home"
private const val DETAIL_ROUTE = "detail/{$DETAIL_ITEM_ID_ARG}"

// Ekran geçişi yatay ekseninde kayar; kayma mesafesi ekran genişliğinin bu kadar bölümüdür.
private const val SHARED_AXIS_OFFSET_DIVISOR = 4

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val motion = remember { MotionScheme.expressive() }
    NavHost(navController = navController, startDestination = HOME_ROUTE) {
        composable(
            route = HOME_ROUTE,
            exitTransition = { sharedAxisExit(motion) },
            popEnterTransition = { sharedAxisEnter(motion) },
        ) {
            HomeScreen(onItemClick = { itemId -> navController.navigate("detail/$itemId") })
        }
        composable(
            route = DETAIL_ROUTE,
            arguments = listOf(navArgument(DETAIL_ITEM_ID_ARG) { type = NavType.StringType }),
            enterTransition = { sharedAxisEnter(motion) },
            popExitTransition = { sharedAxisExit(motion) },
        ) {
            HomeDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}

// Konum ve boyut için spatial spring (overshoot serbest), opaklık için effects spring kullanılır.
private fun sharedAxisEnter(motion: MotionScheme): EnterTransition =
    slideInHorizontally(animationSpec = motion.defaultSpatialSpec()) { fullWidth ->
        fullWidth / SHARED_AXIS_OFFSET_DIVISOR
    } + fadeIn(animationSpec = motion.defaultEffectsSpec())

private fun sharedAxisExit(motion: MotionScheme): ExitTransition =
    slideOutHorizontally(animationSpec = motion.defaultSpatialSpec()) { fullWidth ->
        -fullWidth / SHARED_AXIS_OFFSET_DIVISOR
    } + fadeOut(animationSpec = motion.defaultEffectsSpec())
