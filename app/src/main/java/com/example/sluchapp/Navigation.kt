package com.example.sluchapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseUser

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object EarTraining : Screen("ear_training")
    object Theory : Screen("theory")
    object MusicMap : Screen("music_map")
    object MusicCalendar : Screen("music_calendar")
    object Login : Screen("login")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    user: FirebaseUser?,
    loginViewModel: LoginViewModel,
    onLogout: () -> Unit
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            MainScreen(
                user = user,
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onLogoutClick = onLogout,
                navController = navController
            )
        }
        composable(Screen.EarTraining.route) {
            EarTrainingScreen()
        }
        composable(Screen.Theory.route) {
            TheoryScreen()
        }
        composable(Screen.MusicMap.route) {
            MusicMapScreen()
        }
        composable(Screen.MusicCalendar.route) {
            MusicCalendarScreen()
        }
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
