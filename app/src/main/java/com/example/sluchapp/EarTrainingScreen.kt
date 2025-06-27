package com.example.sluchapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.sluchapp.R
import com.example.sluchapp.EarTrainingType
import com.google.firebase.auth.FirebaseUser
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarTrainingScreen(
    user: FirebaseUser?,                  // stan użytkownika (zalogowany lub nie)
    onLogoutClick: () -> Unit,           // callback wylogowania
    navController: NavHostController,    // nawigacja
    onExerciseSelected: (EarTrainingType) -> Unit,
    onBack: () -> Unit          // <- TUTAJ dodaj ten parametr
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Wróć"
                        )
                    }
                },
                title = {
                    Text("Ćwiczenia słuchowe")
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                actions = {
                    if (user != null) {
                        val initials = user.displayName
                            ?.split(" ")
                            ?.mapNotNull { it.firstOrNull()?.toString() }
                            ?.joinToString("")
                            ?.take(2)
                            ?: "??"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(initials, color = MaterialTheme.colorScheme.onError)
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = onLogoutClick) {
                                Text("Wyloguj", color = MaterialTheme.colorScheme.onError)
                            }
                        }
                    } else {
                        TextButton(onClick = { navController.navigate("login") }) {
                            Text("Zaloguj", color = MaterialTheme.colorScheme.onError)
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Tło obrazkowe
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
                        text = "Wybierz rodzaj ćwiczenia słuchowego",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(EarTrainingType.values().toList()) { type ->   // tutaj poprawka
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .aspectRatio(5f)
                                    .clickable { onExerciseSelected(type) },
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
                                                radius = 500f
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type.label,  // tutaj musi istnieć label
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
    )
}
