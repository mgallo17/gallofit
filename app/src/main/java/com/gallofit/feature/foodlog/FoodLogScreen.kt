package com.gallofit.feature.foodlog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gallofit.core.domain.model.MealSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Refeições de hoje") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: AddFood */ }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            MealSlot.entries.forEach { slot ->
                item {
                    MealSlotSection(slot = slot, entries = emptyList())
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun MealSlotSection(slot: MealSlot, entries: List<Any>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${slot.emoji} ${slot.displayName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("0 kcal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (entries.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sem registos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
