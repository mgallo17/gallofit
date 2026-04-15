package com.gallofit.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Definições") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Perfil", style = MaterialTheme.typography.titleMedium)
                    Text("Matheus Gallo", style = MaterialTheme.typography.bodyMedium)
                    Text("90kg · 177cm · Meta: 85.5kg", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Metas diárias", style = MaterialTheme.typography.titleMedium)
                    Text("Dias normais: 2100 kcal · 190g prot · 160g carbos · 65g gord", style = MaterialTheme.typography.bodySmall)
                    Text("Dias duplos (Ter/Qui): 2200 kcal", style = MaterialTheme.typography.bodySmall)
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Health Connect", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* TODO: HC permissions */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("Ligar Health Connect")
                    }
                }
            }
        }
    }
}
