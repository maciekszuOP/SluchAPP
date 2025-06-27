package com.example.sluchapp

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun MusicMapScreen(navController: NavController) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var markers by remember { mutableStateOf(listOf<LatLng>()) }
    var mapLoaded by remember { mutableStateOf(false) }

    // Pobieranie danych z Firestore po starcie ekranu
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }

        val user = auth.currentUser ?: return@LaunchedEffect
        try {
            val snapshot = db.collection("quiz_results")
                .whereEqualTo("uid", user.uid)
                .get()
                .await()

            val locations = snapshot.documents.mapNotNull { doc ->
                val location = doc.get("location") as? Map<*, *>
                val lat = location?.get("lat") as? Double
                val lon = location?.get("lon") as? Double
                if (lat != null && lon != null) LatLng(lat, lon) else null
            }
            markers = locations
        } catch (e: Exception) {
            Log.e("MusicMapScreen", "Błąd przy pobieraniu wyników: ${e.message}")
        }
    }

    // Domyślne centrum: Poznań
    val poznan = LatLng(52.4064, 16.9252)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(poznan, 12f)
    }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = { mapLoaded = true },
            properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted)
        ) {
            markers.forEach { latLng ->
                Marker(
                    state = MarkerState(position = latLng),
                    title = "Trening muzyczny"
                )
            }
        }

        // Przycisk powrotu do ekranu głównego w prawym dolnym rogu
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Button(onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(0)
                }
            }) {
                Text("Powrót do ekranu głównego")
            }
        }
    }
}
