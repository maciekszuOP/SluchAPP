package com.example.sluchapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.sluchapp.ui.EarTrainingScreen
import com.google.firebase.auth.FirebaseUser
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object EarTraining : Screen("ear_training")
    object Theory : Screen("theory")
    object MusicMap : Screen("music_map")
    object MusicCalendar : Screen("music_calendar")
    object Login : Screen("login")

    object TheoryDetail : Screen("theory/{topicId}") {
        fun createRoute(topicId: String) = "theory/$topicId"
    }

    object ExerciseLevelSelection : Screen("exercise_level/{type}") {
        fun createRoute(type: EarTrainingType) = "exercise_level/${type.name}"
    }

    object ExerciseQuiz : Screen("exercise_quiz/{type}/{level}") {
        fun createRoute(type: EarTrainingType, level: String) =
            "exercise_quiz/${type.name}/$level"
    }

    object ResultScreen : Screen("results/{type}/{level}/{correctAnswers}/{totalQuestions}/{duration}") {
        fun createRoute(
            type: EarTrainingType,
            level: String,
            correctAnswers: Int,
            totalQuestions: Int,
            duration: Long
        ): String {
            val encodedLevel = URLEncoder.encode(level, StandardCharsets.UTF_8.toString())
            return "results/${type.name}/$encodedLevel/$correctAnswers/$totalQuestions/$duration"
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    user: FirebaseUser?,
    loginViewModel: LoginViewModel,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

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
            EarTrainingScreen(
                user = user,
                onLogoutClick = onLogout,
                navController = navController,
                onExerciseSelected = { type ->
                    navController.navigate(Screen.ExerciseLevelSelection.createRoute(type))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Theory.route) {
            TheoryScreen(navController = navController)
        }

        composable(Screen.MusicMap.route) {
            MusicMapScreen(navController = navController)
        }

        composable(Screen.MusicCalendar.route) {
            MusicCalendarScreen(navController = navController)
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

        composable(Screen.TheoryDetail.route) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: return@composable
            TheoryDetailScreen(topicId = topicId, navController = navController)
        }

        composable(Screen.ExerciseLevelSelection.route) { backStackEntry ->
            val typeString = backStackEntry.arguments?.getString("type") ?: return@composable
            val type = EarTrainingType.valueOf(typeString)
            ExerciseLevelSelectionScreen(
                type = type,
                onLevelSelected = { level ->
                    navController.navigate(Screen.ExerciseQuiz.createRoute(type, level))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ExerciseQuiz.route,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = EarTrainingType.valueOf(
                backStackEntry.arguments?.getString("type") ?: return@composable
            )
            val level = backStackEntry.arguments?.getString("level") ?: return@composable

            val allQuestions = com.example.sluchapp.data.QuestionRepository.getQuestions(context, type, level)
            val questions = List(5) { allQuestions.random() }

            ExerciseQuizScreen(
                type = type,
                level = level,
                navController = navController,
                questions = questions
            )
        }

        composable(
            route = Screen.ResultScreen.route,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("level") { type = NavType.StringType },
                navArgument("correctAnswers") { type = NavType.IntType },
                navArgument("totalQuestions") { type = NavType.IntType },
                navArgument("duration") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val type = EarTrainingType.valueOf(backStackEntry.arguments?.getString("type") ?: return@composable)
            val level = backStackEntry.arguments?.getString("level") ?: return@composable
            val correctAnswers = backStackEntry.arguments?.getInt("correctAnswers") ?: 0
            val totalQuestions = backStackEntry.arguments?.getInt("totalQuestions") ?: 0
            val duration = backStackEntry.arguments?.getLong("duration") ?: 0L

            ResultScreen(
                type = type,
                level = level,
                correctAnswers = correctAnswers,
                totalQuestions = totalQuestions,
                duration = duration,
                navController = navController
            )
        }
    }
}
