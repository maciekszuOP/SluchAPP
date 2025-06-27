package com.example.sluchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.sluchapp.ui.theme.SluchAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {

    private val authViewModel by viewModels<AuthViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 Inicjalizacja Firebase (jeśli nie masz własnej klasy Application)
        FirebaseApp.initializeApp(this)

        setContent {
            SluchAppTheme {
                val user by authViewModel.user.collectAsState()
                val navController = rememberNavController()

                NavGraph(
                    navController = navController,
                    user = user,
                    loginViewModel = loginViewModel,
                    onLogout = { authViewModel.signOut() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    user: FirebaseUser?,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    navController: NavHostController
) {
    val sections = listOf(
        "Ćwiczenia słuchowe" to Screen.EarTraining.route,
        "Teoria muzyczna" to Screen.Theory.route,
        "Mapa muzyczna" to Screen.MusicMap.route,
        "Kalendarz muzyczny" to Screen.MusicCalendar.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("SłuchApp") },
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
                        TextButton(onClick = onLoginClick) {
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
                // Tło pod gridem
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Grid kafelków
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(25.dp),
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    items(sections) { (label, route) ->
                        val requiresLogin = route == Screen.MusicMap.route || route == Screen.MusicCalendar.route
                        val isEnabled = user != null || !requiresLogin

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .then(
                                    if (isEnabled) Modifier.clickable {
                                        navController.navigate(route)
                                    } else Modifier
                                ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = if (isEnabled) {
                                                listOf(Color(0xFFD0E8FF), Color(0xFFA6D4FF))
                                            } else {
                                                listOf(Color.LightGray, Color.Gray)
                                            },
                                            center = Offset(0.5f, 0.5f),
                                            radius = 5000f
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (isEnabled) Color.Black else Color.DarkGray
                                    )
                                    if (!isEnabled) {
                                        Text(
                                            text = "Zaloguj się",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.DarkGray
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

