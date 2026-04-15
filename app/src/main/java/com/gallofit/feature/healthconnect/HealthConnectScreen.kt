package com.gallofit.feature.healthconnect

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.health.connect.client.PermissionController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.navigation.NavController
import com.gallofit.core.data.FoodViewModel
import com.gallofit.core.data.HealthConnectState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConnectScreen(navController: NavController, foodViewModel: FoodViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val healthConnectState by foodViewModel.healthConnect.collectAsState()

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(permissions)) {
            scope.launch { syncHealthConnect(context, foodViewModel) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Connect") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(Icons.Default.Favorite, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)

            Text("Health Connect", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Sincronize seus passos e calorias queimadas automaticamente.",
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (healthConnectState.isConnected) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("✅ Conectado", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        Text("Passos hoje: ${healthConnectState.stepsToday}", style = MaterialTheme.typography.bodyLarge)
                        Text("Calorias queimadas: ${healthConnectState.caloriesBurnedToday} kcal", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                OutlinedButton(
                    onClick = { scope.launch { syncHealthConnect(context, foodViewModel) } },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Sincronizar agora") }
            } else {
                val available = HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
                if (available) {
                    Button(
                        onClick = { permissionLauncher.launch(permissions) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Conceder permissões") }
                } else {
                    Text("Health Connect não está instalado.", style = MaterialTheme.typography.bodyMedium)
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.healthdata"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Instalar Health Connect") }
                }
            }
        }
    }
}

suspend fun syncHealthConnect(context: android.content.Context, foodViewModel: FoodViewModel) {
    try {
        val client = HealthConnectClient.getOrCreate(context)
        val todayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        val todayEnd = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        val range = TimeRangeFilter.between(todayStart, todayEnd)

        val stepsResp = client.readRecords(ReadRecordsRequest(StepsRecord::class, range))
        val steps = stepsResp.records.sumOf { it.count }.toInt()

        val calResp = client.readRecords(ReadRecordsRequest(TotalCaloriesBurnedRecord::class, range))
        val calories = calResp.records.sumOf { it.energy.inKilocalories }.toInt()

        foodViewModel.updateHealthConnect(HealthConnectState(steps, calories, true))
    } catch (_: Exception) {
        foodViewModel.updateHealthConnect(HealthConnectState(isConnected = false))
    }
}
