@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.sluchapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// MODELE

data class TheoryTopic(
    val id: String = "",
    val title: String = "",
    val iconName: String = "menu_book"
)

@Composable
fun TheoryScreen(navController: NavController) {
    var topics by remember { mutableStateOf<List<TheoryTopic>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        topics = loadTheoryTopicsFromFirebase()
        isLoading = false
    }

    Scaffold(
        topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Teoria") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Wstecz")
                        }
                    },
                    actions = {
                        if (user != null) {
                            val initials = user.displayName
                                ?.split(" ")
                                ?.mapNotNull { it.firstOrNull()?.toString() }
                                ?.joinToString("")
                                ?.take(2) ?: "??"

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(initials, color = MaterialTheme.colorScheme.onError)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        } else {
                            Text(
                                "Nie zalogowano",
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                )

        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tło
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    items(topics) { topic ->
                        TheoryTopicCard(topic = topic) {
                            navController.navigate("theory/${topic.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TheoryTopicCard(topic: TheoryTopic, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD0E8FF),
                            Color(0xFFA6D4FF)
                        ),
                        center = Offset(0.5f, 0.5f),
                        radius = 5000f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val emoji = when (topic.iconName.lowercase()) {
                    "notes", "czytanie nut" -> "🎵"
                    "compare_arrows" -> "↔️"
                    "piano", "keyboard", "music_note" -> "🎹"
                    "timer" -> "⏱️"
                    "book", "menu_book" -> "📖"
                    else -> "❓"
                }

                Text(
                    text = emoji,
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    color = Color.Black
                )
            }
        }
    }
}

suspend fun loadTheoryTopicsFromFirebase(): List<TheoryTopic> {
    return try {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("theoryTopics")
            .get()
            .await()

        snapshot.documents.mapNotNull { doc ->
            doc.toObject(TheoryTopic::class.java)?.copy(id = doc.id)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
