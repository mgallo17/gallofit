package com.gallofit.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gallofit.core.data.FoodViewModel
import com.gallofit.core.data.UserProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(foodViewModel: FoodViewModel) {
    val profile by foodViewModel.profile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember(profile) { mutableStateOf(profile.name) }
    var weight by remember(profile) { mutableStateOf(profile.weightKg.toString()) }
    var height by remember(profile) { mutableStateOf(profile.heightCm.toString()) }
    var goalWeight by remember(profile) { mutableStateOf(profile.goalWeightKg.toString()) }
    var calNormal by remember(profile) { mutableStateOf(profile.caloriesNormal.toString()) }
    var calDouble by remember(profile) { mutableStateOf(profile.caloriesDouble.toString()) }
    var protein by remember(profile) { mutableStateOf(profile.proteinG.toString()) }
    var carbs by remember(profile) { mutableStateOf(profile.carbsG.toString()) }
    var fat by remember(profile) { mutableStateOf(profile.fatG.toString()) }

    fun save() {
        foodViewModel.saveProfile(
            UserProfile(
                name = name,
                weightKg = weight.toDoubleOrNull() ?: profile.weightKg,
                heightCm = height.toIntOrNull() ?: profile.heightCm,
                goalWeightKg = goalWeight.toDoubleOrNull() ?: profile.goalWeightKg,
                caloriesNormal = calNormal.toIntOrNull() ?: profile.caloriesNormal,
                caloriesDouble = calDouble.toIntOrNull() ?: profile.caloriesDouble,
                proteinG = protein.toIntOrNull() ?: profile.proteinG,
                carbsG = carbs.toIntOrNull() ?: profile.carbsG,
                fatG = fat.toIntOrNull() ?: profile.fatG
            )
        )
        scope.launch { snackbarHostState.showSnackbar("Perfil salvo! ✅") }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
                actions = {
                    IconButton(onClick = ::save) {
                        Icon(Icons.Default.Check, contentDescription = "Salvar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            item { Text("Dados pessoais", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Peso atual (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Altura (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
            }
            item {
                OutlinedTextField(value = goalWeight, onValueChange = { goalWeight = it }, label = { Text("Peso meta (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
            }

            item { HorizontalDivider() }
            item { Text("Metas calóricas", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = calNormal, onValueChange = { calNormal = it }, label = { Text("Dias normais (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = calDouble, onValueChange = { calDouble = it }, label = { Text("Dias duplos (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
            }
            item { Text("Dias duplos = Terça e Quinta (treino duplo)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }

            item { HorizontalDivider() }
            item { Text("Metas de macros (por dia)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Proteína (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbos (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("Gordura (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Button(onClick = ::save, modifier = Modifier.fillMaxWidth()) { Text("Salvar perfil") }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
