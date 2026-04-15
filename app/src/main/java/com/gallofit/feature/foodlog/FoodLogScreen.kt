package com.gallofit.feature.foodlog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gallofit.core.data.FoodViewModel
import com.gallofit.core.data.database.FoodEntryEntity
import com.gallofit.core.domain.model.MealSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodLogScreen(navController: NavController, foodViewModel: FoodViewModel) {
    val daily by foodViewModel.dailyData.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Refeições de hoje") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_food/LUNCH") }) {
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
                val entries = daily.entries.filter { it.mealSlot == slot.name }
                val slotCalories = entries.sumOf { it.calories }
                item {
                    MealSlotSection(
                        slot = slot,
                        entries = entries,
                        totalCalories = slotCalories,
                        onAddClick = { navController.navigate("add_food/${slot.name}") },
                        onDeleteClick = { foodViewModel.deleteEntry(it) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun MealSlotSection(
    slot: MealSlot,
    entries: List<FoodEntryEntity>,
    totalCalories: Int,
    onAddClick: () -> Unit,
    onDeleteClick: (Long) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${slot.emoji} ${slot.displayName}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (totalCalories > 0) {
                        Text("$totalCalories kcal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onAddClick) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar a ${slot.displayName}", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            if (entries.isEmpty()) {
                Text("Sem registros", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                entries.forEach { entry ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(entry.foodName, style = MaterialTheme.typography.bodyMedium)
                            Text("${entry.calories} kcal · ${entry.protein.toInt()}g prot · ${entry.carbs.toInt()}g carbos",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { onDeleteClick(entry.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
