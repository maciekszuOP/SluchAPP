package com.example.sluchapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sluchapp.model.EarTrainingType

@Composable
fun EarTrainingScreen(
    onExerciseSelected: (EarTrainingType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wybierz rodzaj ćwiczenia słuchowego",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        EarTrainingType.values().forEach { type ->
            Button(
                onClick = { onExerciseSelected(type) },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = type.label)
            }
        }
    }
}
