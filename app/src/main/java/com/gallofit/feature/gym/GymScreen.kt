package com.gallofit.feature.gym

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.NavController
import com.gallofit.core.data.FoodViewModel
import com.gallofit.core.data.database.GymSetEntity
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymScreen(navController: NavController, foodViewModel: FoodViewModel) {
    val gymState by foodViewModel.gymState.collectAsState()
    var selectedMuscle by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    if (selectedExercise != null) {
        ExerciseLogScreen(
            exercise = selectedExercise!!,
            todaySets = gymState.todaySets.filter { it.exerciseId == selectedExercise!!.id },
            onAddSet = { weight, reps ->
                val setNum = gymState.todaySets.count { it.exerciseId == selectedExercise!!.id } + 1
                foodViewModel.addGymSet(selectedExercise!!, setNum, weight, reps)
            },
            onDeleteSet = { foodViewModel.deleteGymSet(it) },
            onBack = { selectedExercise = null }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ginásio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Resumo de hoje
            if (gymState.todaySets.isNotEmpty()) {
                val exercisesToday = gymState.todaySets.groupBy { it.exerciseId }
                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Hoje: ${exercisesToday.size} exercícios · ${gymState.todaySets.size} séries",
                                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            exercisesToday.forEach { (_, sets) ->
                                Text("• ${sets.first().exerciseName}: ${sets.size} séries",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Busca
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Buscar exercício") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Filtros por grupo muscular
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedMuscle == null,
                        onClick = { selectedMuscle = null },
                        label = { Text("Todos") }
                    )
                }
            }
            item {
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(ExerciseLibrary.muscleGroups) { muscle ->
                        FilterChip(
                            selected = selectedMuscle == muscle,
                            onClick = { selectedMuscle = if (selectedMuscle == muscle) null else muscle },
                            label = { Text(muscle) }
                        )
                    }
                }
            }

            // Lista de exercícios
            val filtered = ExerciseLibrary.all.filter { ex ->
                (selectedMuscle == null || ex.muscleGroup == selectedMuscle) &&
                (searchQuery.isBlank() || ex.name.contains(searchQuery, ignoreCase = true) || ex.equipment.contains(searchQuery, ignoreCase = true))
            }

            items(filtered) { exercise ->
                val setsToday = gymState.todaySets.count { it.exerciseId == exercise.id }
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedExercise = exercise },
                    colors = if (setsToday > 0)
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    else CardDefaults.cardColors()
                ) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(exercise.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Text("${exercise.muscleGroup} · ${exercise.equipment}",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (setsToday > 0) {
                            Badge { Text("$setsToday") }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLogScreen(
    exercise: Exercise,
    todaySets: List<GymSetEntity>,
    onAddSet: (Double, Int) -> Unit,
    onDeleteSet: (Long) -> Unit,
    onBack: () -> Unit
) {
    var weightInput by remember { mutableStateOf("") }
    var repsInput by remember { mutableStateOf("10") }

    // Pré-preencher com último peso usado
    val lastWeight = todaySets.lastOrNull()?.weightKg
    if (weightInput.isBlank() && lastWeight != null) {
        weightInput = if (lastWeight % 1 == 0.0) lastWeight.toInt().toString() else lastWeight.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                        Text("${exercise.muscleGroup} · ${exercise.equipment}",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Adicionar série
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Registrar série ${todaySets.size + 1}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { weightInput = it },
                                label = { Text("Peso (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = repsInput,
                                onValueChange = { repsInput = it },
                                label = { Text("Reps") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                val w = weightInput.toDoubleOrNull() ?: 0.0
                                val r = repsInput.toIntOrNull() ?: 0
                                if (r > 0) {
                                    onAddSet(w, r)
                                    repsInput = "10"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = repsInput.isNotBlank()
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Adicionar série")
                        }
                    }
                }
            }

            // Séries de hoje
            if (todaySets.isNotEmpty()) {
                item { Text("Séries hoje", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
                items(todaySets) { set ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("${set.setNumber}ª série", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, modifier = Modifier.width(70.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${if (set.weightKg > 0) "${set.weightKg}kg" else "Sem peso"} × ${set.reps} reps",
                                    style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { onDeleteSet(set.id) }) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            } else {
                item {
                    Text("Sem séries registradas hoje", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
