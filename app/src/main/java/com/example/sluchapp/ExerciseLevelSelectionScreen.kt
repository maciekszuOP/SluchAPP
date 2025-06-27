package com.example.sluchapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLevelSelectionScreen(
    type: EarTrainingType,
    onLevelSelected: (String) -> Unit,
    onBack: () -> Unit // ← dodaj ten parametr
) {
    val levelOptions = when (type) {
        EarTrainingType.INTERVALS -> mapOf("Podstawowe interwały" to "basic", "Wszystkie interwały" to "advanced")
        EarTrainingType.CHORDS -> mapOf("Akordy podstawowe" to "basic", "Akordy zaawansowane" to "advanced")
        EarTrainingType.SCALES -> mapOf("Skale podstawowe" to "basic", "Skale zaawansowane" to "advanced")
        EarTrainingType.TEMPO_RHYTHM -> mapOf("Tempo" to "tempo", "Rytm" to "rhythm")
        EarTrainingType.ABSOLUTE_PITCH -> mapOf("Rozpoznawanie wysokości dźwięku" to "default")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Wróć"
                        )
                    }
                },
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Wybierz poziom", modifier = Modifier.padding(end = 48.dp)) // padding end by nie nachodził na ikonę
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 🔵 Tło z obrazka
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Wybierz poziom trudności",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Grid jak w MainScreen
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(25.dp),
                        horizontalArrangement = Arrangement.spacedBy(25.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        levelOptions.forEach { (label, levelKey) ->
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clickable {
                                            onLevelSelected(levelKey)
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0xFFD0E8FF),  // bardzo jasny niebieski
                                                        Color(0xFFA6D4FF)   // mocniejszy niebieski w środku
                                                    ),
                                                    radius = 500f
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}
