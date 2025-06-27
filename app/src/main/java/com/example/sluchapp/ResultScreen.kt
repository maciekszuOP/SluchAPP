package com.example.sluchapp

import android.Manifest
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sluchapp.model.EarTrainingType
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ResultScreen(
    navController: NavController,
    type: EarTrainingType,
    level: String,
    totalQuestions: Int,
    correctAnswers: Int,
    duration: Long // czas trwania quizu w ms
) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val durationSeconds = duration / 1000f
    val accuracy = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f

    var location by remember { mutableStateOf<Location?>(null) }
    var resultUploaded by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    var uploading by remember { mutableStateOf(false) }

    // Pobierz lokalizację przy wejściu na ekran (bez automatycznego uploadu)
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        } else {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                val loc = fusedLocationClient.lastLocation.await()
                if (loc != null) location = loc
                else uploadError = "Brak dostępnej lokalizacji"
            } catch (e: Exception) {
                uploadError = "Nie udało się pobrać lokalizacji"
            }
        }
    }

    fun uploadResult() {
        val user = auth.currentUser
        if (user == null) {
            uploadError = "Nie jesteś zalogowany – wynik nie został zapisany"
            return
        }

        uploading = true
        uploadError = null

        val resultData = mutableMapOf<String, Any>(
            "uid" to user.uid,
            "timestamp" to Timestamp.now(),
            "type" to type.name,
            "level" to level,
            "correct" to correctAnswers,
            "total" to totalQuestions,
            "accuracy" to accuracy,
            "durationSeconds" to durationSeconds,
        )

        // Dodaj lokalizację — prawdziwą lub losową z Poznania
        val locToUse = location ?: Location("").apply {
            val latMin = 52.359975
            val latMax = 52.435670
            val lonMin = 16.826761
            val lonMax = 16.992472
            latitude = latMin + Math.random() * (latMax - latMin)
            longitude = lonMin + Math.random() * (lonMax - lonMin)
        }

        resultData["location"] = mapOf(
            "lat" to locToUse.latitude,
            "lon" to locToUse.longitude
        )

        db.collection("quiz_results")
            .add(resultData)
            .addOnSuccessListener {
                uploading = false
                resultUploaded = true
            }
            .addOnFailureListener { e ->
                uploading = false
                uploadError = "Błąd przy zapisie wyniku: ${e.message}"
            }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Wyniki quizu", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Typ: ${type.name}")
        Text("Poziom: $level")
        Text("Poprawne odpowiedzi: $correctAnswers / $totalQuestions")
        Text("Skuteczność: ${(accuracy * 100).toInt()}%")
        Text("Czas trwania: %.1f s".format(durationSeconds))

        Spacer(modifier = Modifier.height(24.dp))

        when {
            uploading -> CircularProgressIndicator()
            resultUploaded -> Text("Wynik zapisany!", color = MaterialTheme.colorScheme.primary)
            uploadError != null -> Text(uploadError!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (!uploading && !resultUploaded) {
                    uploadResult()
                }
            },
            enabled = !uploading && !resultUploaded
        ) {
            Text("Zapisz dane z quizu")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            navController.navigate(Screen.Home.route) {
                popUpTo(0)
            }
        }) {
            Text("Powrót do ekranu głównego")
        }
    }
}
