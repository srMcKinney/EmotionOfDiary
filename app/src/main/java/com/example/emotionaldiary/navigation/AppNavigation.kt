package com.example.emotionaldiary.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.emotionaldiary.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "diary") {
        composable("diary") {
            DiaryScreen(navController = navController)
        }
        composable("calendar") {
//            CalendarScreen(navController = navController)
        }
        composable("about") {
            AboutScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable(
            route = "emotion_entry/{dateMillis}",
            arguments = listOf(
                navArgument("dateMillis") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val dateMillis = backStackEntry.arguments?.getLong("dateMillis") ?: System.currentTimeMillis()
//            EmotionEntryScreen(navController = navController, dateMillis = dateMillis)
        }
    }
}