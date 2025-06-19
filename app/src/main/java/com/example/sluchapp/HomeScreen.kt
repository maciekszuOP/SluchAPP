package com.example.sluchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.sluchapp.ui.theme.SluchAppTheme
import com.google.firebase.auth.FirebaseUser
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel>()
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        topBar = {
            TopAppBar(
                title = { Text("SłuchApp") },
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
                            Text(initials)
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = onLogoutClick) {
                                Text("Wyloguj")
                            }
                        }
                    } else {
                        TextButton(onClick = onLoginClick) {
                            Text("Zaloguj")
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp),
                horizontalArrangement = Arrangement.spacedBy(25.dp)
            ) {
                items(sections) { (label, route) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable {
                                navController.navigate(route)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = label, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    )
}
