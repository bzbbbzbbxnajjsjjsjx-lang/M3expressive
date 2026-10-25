package com.example.myexpressiveapp.presentation.navigation

import androidx.compose.runtime.Composable
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

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HOME_ROUTE) {
        composable(HOME_ROUTE) {
            HomeScreen(onItemClick = { itemId -> navController.navigate("detail/$itemId") })
        }
        composable(
            route = DETAIL_ROUTE,
            arguments = listOf(navArgument(DETAIL_ITEM_ID_ARG) { type = NavType.StringType }),
        ) {
            HomeDetailScreen(onBack = { navController.popBackStack() })
        }
    }
}
