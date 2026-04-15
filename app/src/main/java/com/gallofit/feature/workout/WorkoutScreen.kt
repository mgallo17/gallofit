package com.gallofit.feature.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gallofit.core.data.FoodViewModel
import com.gallofit.core.data.database.WorkoutEntryEntity

data class WorkoutType(val key: String, val displayName: String, val emoji: String)

val WORKOUT_TYPES = listOf(
    WorkoutType("GYM", "Ginásio", "🏋️"),
    WorkoutType("TENNIS", "Tênis", "🎾"),
    WorkoutType("CARDIO", "Cardio", "🏃"),
    WorkoutType("SWIM", "Natação", "🏊"),
    WorkoutType("BIKE", "Bicicleta", "🚴"),
    WorkoutType("OTHER", "Outro", "⚽")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(navController: NavController, foodViewModel: FoodViewModel = viewModel()) {
    val workoutState by foodViewModel.workoutState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Treinos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Registrar treino")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Botão para ir ao ginásio detalhado
            item {
                OutlinedButton(
                    onClick = { navController.navigate("gym") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FitnessCenter, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🏋️ Registrar ginásio (exercício a exercício)")
                }
            }

            if (workoutState.todayWorkouts.isNotEmpty()) {
                item { Text("Hoje", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
                items(workoutState.todayWorkouts) { workout ->
                    WorkoutCard(workout, onDelete = { foodViewModel.deleteWorkout(workout.id) })
                }
                item { HorizontalDivider() }
            }

            if (workoutState.recentWorkouts.filter { it.date != workoutState.todayWorkouts.firstOrNull()?.date }.isNotEmpty()) {
                item { Text("Histórico recente", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
                val history = workoutState.recentWorkouts.filter { w ->
                    workoutState.todayWorkouts.none { it.id == w.id }
                }
                items(history) { workout ->
                    WorkoutCard(workout, onDelete = { foodViewModel.deleteWorkout(workout.id) })
                }
            }

            if (workoutState.recentWorkouts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💪", style = MaterialTheme.typography.displayMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sem treinos registrados", style = MaterialTheme.typography.bodyLarge)
                        Text("Toca no + para adicionar", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showDialog) {
        AddWorkoutDialog(
            onDismiss = { showDialog = false },
            onConfirm = { type, duration, calories, notes ->
                foodViewModel.addWorkout(type, duration, calories, notes)
                showDialog = false
            }
        )
    }
}

@Composable
fun WorkoutCard(workout: WorkoutEntryEntity, onDelete: () -> Unit) {
    val type = WORKOUT_TYPES.find { it.key == workout.type } ?: WorkoutType(workout.type, workout.type, "💪")
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(type.emoji, style = MaterialTheme.typography.headlineSmall)
            Column(modifier = Modifier.weight(1f)) {
                Text(type.displayName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text("${workout.durationMin} min" + if (workout.caloriesBurned > 0) " · ${workout.caloriesBurned} kcal queimadas" else "",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (workout.notes.isNotBlank()) {
                    Text(workout.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkoutDialog(onDismiss: () -> Unit, onConfirm: (String, Int, Int, String) -> Unit) {
    var selectedType by remember { mutableStateOf(WORKOUT_TYPES[0]) }
    var typeExpanded by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf("60") }
    var calories by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar treino") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(
                        value = "${selectedType.emoji} ${selectedType.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        WORKOUT_TYPES.forEach { type ->
                            DropdownMenuItem(
                                text = { Text("${type.emoji} ${type.displayName}") },
                                onClick = { selectedType = type; typeExpanded = false }
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duração (min)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Kcal (opcional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas (opcional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedType.key, duration.toIntOrNull() ?: 60, calories.toIntOrNull() ?: 0, notes) },
                enabled = duration.isNotBlank()
            ) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
