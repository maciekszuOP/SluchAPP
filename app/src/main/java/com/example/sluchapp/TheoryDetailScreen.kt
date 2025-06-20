package com.example.sluchapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class TheoryTutorial(
    val title: String = "",
    val description: String = "",
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TheoryDetailScreen(topicId: String) {
    val scope = rememberCoroutineScope()
    var tutorial by remember { mutableStateOf<TheoryTutorial?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(topicId) {
        scope.launch {
            tutorial = loadTheoryTutorial(topicId)
            isLoading = false
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tutorial?.title ?: "Teoria") }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            tutorial?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(it.imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(201.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } ?: run {
                Text(
                    text = "Brak danych do wyświetlenia.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

suspend fun loadTheoryTutorial(topicId: String): TheoryTutorial? {
    return try {
        val doc = FirebaseFirestore.getInstance()
            .collection("theoryTutorials")
            .document(topicId)
            .get()
            .await()

        if (doc.exists()) {
            doc.toObject(TheoryTutorial::class.java)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
