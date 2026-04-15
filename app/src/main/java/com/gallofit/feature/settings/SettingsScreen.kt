package com.gallofit.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gallofit.core.data.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, foodViewModel: FoodViewModel) {
    val profile by foodViewModel.profile.collectAsState()
    val healthConnect by foodViewModel.healthConnect.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Definições") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Perfil resumo
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Perfil", style = MaterialTheme.typography.titleMedium)
                    Text(profile.name, style = MaterialTheme.typography.bodyLarge)
                    Text("${profile.weightKg}kg · ${profile.heightCm}cm · Meta: ${profile.goalWeightKg}kg",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Calorias: ${profile.caloriesNormal} kcal (dias normais) / ${profile.caloriesDouble} kcal (dias duplos)",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { navController.navigate("profile") }, modifier = Modifier.fillMaxWidth()) {
                        Text("Editar perfil")
                    }
                }
            }

            // Health Connect
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Health Connect", style = MaterialTheme.typography.titleMedium)
                    if (healthConnect.isConnected) {
                        Text("✅ Conectado", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        Text("Passos hoje: ${healthConnect.stepsToday}", style = MaterialTheme.typography.bodySmall)
                        Text("Calorias queimadas: ${healthConnect.caloriesBurnedToday} kcal", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("Sincroniza passos e calorias automaticamente", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navController.navigate("health_connect") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Conectar Health Connect")
                        }
                    }
                }
            }

            // Sobre
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GalloFit v3.2", style = MaterialTheme.typography.titleMedium)
                    Text("Tracking de fitness personalizado para Matheus Gallo 🏋️",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
