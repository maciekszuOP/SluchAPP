package com.example.sluchapp

import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExerciseQuizScreen(
    type: EarTrainingType,
    level: String,
    navController: NavController,
    questions: List<ExerciseQuestion>
) {
    val context = LocalContext.current

    val totalQuestions = questions.size
    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var correctAnswersCount by remember { mutableStateOf(0) }

    val currentQuestion = questions.getOrNull(currentIndex) ?: return

    val startTimeMillis = remember { System.currentTimeMillis() }

    val mediaPlayer = remember(currentQuestion.audioResId) {
        MediaPlayer.create(context, currentQuestion.audioResId)
    }

    DisposableEffect(currentQuestion.audioResId) {
        onDispose {
            mediaPlayer.release()
        }
    }

    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(800)
            showFeedback = false
            selectedAnswerIndex = null

            if (currentIndex < totalQuestions - 1) {
                currentIndex++
            } else {
                val endTimeMillis = System.currentTimeMillis()
                val durationMillis = endTimeMillis - startTimeMillis

                navController.navigate(
                    Screen.ResultScreen.createRoute(
                        type = type,
                        level = level,
                        correctAnswers = correctAnswersCount,
                        totalQuestions = totalQuestions,
                        duration = durationMillis
                    )
                )
            }
        }
    }

    // Kontrola animacji — brak na pierwszym pytaniu
    val previousQuestion = remember { mutableStateOf<ExerciseQuestion?>(null) }
    val shouldAnimate = remember(currentQuestion) {
        val animate = previousQuestion.value != null && previousQuestion.value != currentQuestion
        previousQuestion.value = currentQuestion
        animate
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AnimatedContent(
            targetState = currentQuestion,
            transitionSpec = {
                if (!shouldAnimate) {
                    EnterTransition.None with ExitTransition.None
                } else {
                    slideInHorizontally { fullWidth -> fullWidth } + fadeIn() with
                            slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
                }
            },
            label = "QuestionTransition"
        ) { question ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        when {
                            showFeedback && isCorrect -> Color(0xFFDFF0D8)
                            showFeedback && !isCorrect -> Color(0xFFF2DEDE)
                            else -> MaterialTheme.colorScheme.background
                        }
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = "Pytanie ${currentIndex + 1}/$totalQuestions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Button(
                    onClick = {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                            mediaPlayer.seekTo(0)
                        }
                        mediaPlayer.start()
                    },
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()
                ) {
                    Text("Odtwórz dźwięk")
                }

                question.answers.forEachIndexed { index, answer ->
                    Button(
                        onClick = {
                            if (!showFeedback) {
                                selectedAnswerIndex = index
                                isCorrect = index == question.correctAnswerIndex
                                if (isCorrect) correctAnswersCount++
                                showFeedback = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                showFeedback && selectedAnswerIndex == index && index == question.correctAnswerIndex -> Color(0xFF4CAF50)
                                showFeedback && selectedAnswerIndex == index && index != question.correctAnswerIndex -> Color(0xFFF44336)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(answer)
                    }
                }
            }
        }
    }
}

data class ExerciseQuestion(
    val questionText: String,
    val answers: List<String>,
    val correctAnswerIndex: Int,
    val audioResId: Int
)
