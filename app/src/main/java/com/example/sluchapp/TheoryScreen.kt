// ---------------------- TheoryScreen.kt ----------------------
package com.example.sluchapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.navigation.NavController

// MODELE

data class TheoryTopic(
    val id: String = "",
    val title: String = "",
    val iconName: String = "menu_book"
)


// EKRAN LISTY TEMATÓW

@Composable
fun TheoryScreen(navController: NavController) {
    var topics by remember { mutableStateOf<List<TheoryTopic>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        topics = loadTheoryTopicsFromFirebase()
        isLoading = false
    }

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
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(topics) { topic ->
                TheoryTopicCard(topic = topic) {
                    navController.navigate("theory/${topic.id}")
                }
            }
        }
    }
}

@Composable
fun TheoryTopicCard(topic: TheoryTopic, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val emoji = when (topic.iconName.lowercase()) {
                "notes", "czytanie nut" -> "🎵"
                "compare_arrows" -> "↔️"
                "piano", "keyboard","music_note" -> "🎹"
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
                maxLines = 2
            )
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

