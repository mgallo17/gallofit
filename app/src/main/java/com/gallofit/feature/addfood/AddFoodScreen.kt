package com.gallofit.feature.addfood

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gallofit.core.data.FoodViewModel
import com.gallofit.core.data.remote.FoodSearchResult
import com.gallofit.core.domain.model.DefaultTemplates
import com.gallofit.core.domain.model.MealSlot
import com.gallofit.core.domain.model.MealTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    navController: NavController,
    initialSlot: String = MealSlot.LUNCH.name,
    foodViewModel: FoodViewModel = viewModel()
) {
    var foodName by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var selectedSlot by remember { mutableStateOf(runCatching { MealSlot.valueOf(initialSlot.uppercase()) }.getOrDefault(MealSlot.LUNCH)) }
    var slotExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val searchState by foodViewModel.searchState.collectAsState()

    fun fillFromSearch(r: FoodSearchResult) {
        foodName = r.name
        calories = r.calories.toString()
        protein = String.format("%.1f", r.protein)
        carbs = String.format("%.1f", r.carbs)
        fat = String.format("%.1f", r.fat)
        foodViewModel.clearSearch()
        searchQuery = ""
    }

    fun fillFromTemplate(t: MealTemplate) {
        foodName = t.name
        calories = t.totalCalories.toString()
        protein = String.format("%.1f", t.totalProtein)
        carbs = String.format("%.1f", t.totalCarbs)
        fat = String.format("%.1f", t.totalFat)
    }

    fun save() {
        if (foodName.isBlank() || calories.isBlank()) return
        foodViewModel.addEntry(
            slot = selectedSlot,
            name = foodName,
            calories = calories.toIntOrNull() ?: 0,
            protein = protein.toDoubleOrNull() ?: 0.0,
            carbs = carbs.toDoubleOrNull() ?: 0.0,
            fat = fat.toDoubleOrNull() ?: 0.0
        )
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar alimento") },
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Busca API
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        foodViewModel.searchFood(it)
                    },
                    label = { Text("Buscar alimento") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = ""; foodViewModel.clearSearch() }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpar")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Resultados da busca
            if (searchState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
            if (searchState.results.isNotEmpty()) {
                items(searchState.results) { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { fillFromSearch(result) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(result.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, maxLines = 1)
                                Text("${result.calories} kcal · ${result.protein.toInt()}g prot · ${result.source}",
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            IconButton(onClick = { fillFromSearch(result) }) {
                                Icon(Icons.Default.Add, contentDescription = "Usar")
                            }
                        }
                    }
                }
                item { HorizontalDivider() }
            }
            if (searchState.error != null && searchQuery.length >= 2 && !searchState.isLoading) {
                item { Text(searchState.error!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
            }

            // Seletor de refeição
            item {
                ExposedDropdownMenuBox(expanded = slotExpanded, onExpandedChange = { slotExpanded = it }) {
                    OutlinedTextField(
                        value = "${selectedSlot.emoji} ${selectedSlot.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Refeição") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = slotExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = slotExpanded, onDismissRequest = { slotExpanded = false }) {
                        MealSlot.entries.forEach { slot ->
                            DropdownMenuItem(
                                text = { Text("${slot.emoji} ${slot.displayName}") },
                                onClick = { selectedSlot = slot; slotExpanded = false }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(value = foodName, onValueChange = { foodName = it }, label = { Text("Nome do alimento") }, modifier = Modifier.fillMaxWidth())
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Kcal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Prot (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = fat, onValueChange = { fat = it }, label = { Text("Gordura (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                }
            }
            item {
                Button(onClick = ::save, modifier = Modifier.fillMaxWidth(), enabled = foodName.isNotBlank() && calories.isNotBlank()) {
                    Text("Salvar")
                }
            }

            // Templates
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))
                Text("Templates rápidos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
            items(DefaultTemplates.all) { template ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { fillFromTemplate(template) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(template.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text("${template.totalCalories} kcal · ${template.totalProtein.toInt()}g prot",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = {
                            foodViewModel.addEntry(selectedSlot, template.name, template.totalCalories,
                                template.totalProtein, template.totalCarbs, template.totalFat)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Adicionar")
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
