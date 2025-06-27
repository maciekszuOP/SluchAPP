package com.example.sluchapp

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.autoMirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.*
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MusicCalendarScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var activityDates by remember { mutableStateOf(setOf<LocalDate>()) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var swipeDirection by remember { mutableStateOf(AnimationDirection.FORWARD) }

    // Pobierz daty aktywności użytkownika z Firebase Firestore
    LaunchedEffect(user) {
        if (user != null) {
            val snapshot = db.collection("quiz_results")
                .whereEqualTo("uid", user.uid)
                .get()
                .await()

            val dates = snapshot.documents.mapNotNull { doc ->
                val timestamp = doc.getTimestamp("timestamp")?.toDate()?.toInstant()
                timestamp?.atZone(ZoneId.systemDefault())?.toLocalDate()
            }.toSet()

            activityDates = dates
        }
    }

    val initials = user?.displayName?.split(" ")
        ?.mapNotNull { it.firstOrNull()?.toString() }
        ?.take(2)
        ?.joinToString("") ?: "?"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Kalendarz",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wstecz", tint = Color.White)
                    }
                },
                actions = {
                    Text(
                        text = initials,
                        color = Color.White,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(Color.White.copy(alpha = 0.2f), shape = CircleShape)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFB71C1C) // mocna czerwień
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background), // Upewnij się, że masz background.jpg w drawable
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            if (dragAmount > 50) {
                                swipeDirection = AnimationDirection.BACKWARD
                                currentMonth = currentMonth.minusMonths(1)
                            } else if (dragAmount < -50) {
                                swipeDirection = AnimationDirection.FORWARD
                                currentMonth = currentMonth.plusMonths(1)
                            }
                        }
                    }
            ) {
                Text(
                    "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.White
                )

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    listOf("Pn", "Wt", "Śr", "Cz", "Pt", "Sb", "Nd").forEach { day ->
                        Text(day, Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
                    }
                }

                Spacer(Modifier.height(8.dp))

                AnimatedContent(
                    targetState = currentMonth,
                    transitionSpec = {
                        if (swipeDirection == AnimationDirection.FORWARD) {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> width } + fadeOut()
                        }.using(SizeTransform(clip = false))
                    }
                ) { monthToDisplay ->
                    CalendarGrid(monthToDisplay, activityDates)
                }
            }
        }
    }
}

@Composable
fun CalendarGrid(month: YearMonth, activityDates: Set<LocalDate>) {
    val daysInMonth = month.lengthOfMonth()
    val firstDayOfMonth = (month.atDay(1).dayOfWeek.value + 6) % 7 // przesunięcie, żeby poniedziałek=0

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.height(300.dp),
        userScrollEnabled = false
    ) {
        items(firstDayOfMonth) {
            Box(modifier = Modifier.size(40.dp))
        }

        items(daysInMonth) { i ->
            val date = month.atDay(i + 1)
            val isActive = date in activityDates

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(2.dp)
                    .background(
                        color = if (isActive) Color(0xFF81C784) else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = if (isActive) 2.dp else 0.dp,
                        color = if (isActive) Color(0xFF388E3C) else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable {
                        // TODO: opcjonalnie: pokaz szczegóły ćwiczeń z tego dnia
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "${i + 1}", color = Color.White)
            }
        }
    }
}

enum class AnimationDirection { FORWARD, BACKWARD }
