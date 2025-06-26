package com.example.sluchapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sluchapp.model.EarTrainingType

@Composable
fun ExerciseLevelSelectionScreen(
    type: EarTrainingType,
    onLevelSelected: (String) -> Unit
) {
    // Mapowanie etykiet wyświetlanych użytkownikowi na poziomy systemowe
    val levelOptions = when (type) {
        EarTrainingType.INTERVALS -> mapOf("Podstawowe interwały" to "basic", "Wszystkie interwały" to "advanced")
        EarTrainingType.CHORDS -> mapOf("Akordy podstawowe" to "basic", "Akordy zaawansowane" to "advanced")
        EarTrainingType.SCALES -> mapOf("Skale podstawowe" to "basic", "Skale zaawansowane" to "advanced")
        EarTrainingType.TEMPO_RHYTHM -> mapOf("Tempo" to "tempo", "Rytm" to "rhythm")
        EarTrainingType.ABSOLUTE_PITCH -> mapOf("Rozpoznawanie wysokości dźwięku" to "default")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wybierz poziom trudności",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        levelOptions.forEach { (label, levelKey) ->
            Button(
                onClick = { onLevelSelected(levelKey) }, // przekazujemy klucz, np. "basic"
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = label)
            }
        }
    }
}
