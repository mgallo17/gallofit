package com.gallofit.feature.dashboard
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gallofit.app.ui.theme.BlueProtein
import com.gallofit.app.ui.theme.OrangeCarbs
import com.gallofit.app.ui.theme.OrangeCalorias
import com.gallofit.app.ui.theme.YellowFat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    // TODO: ligar ao ViewModel com dados reais
    val today = LocalDate.now()
    val dayName = today.format(DateTimeFormatter.ofPattern("EEEE, d MMM", Locale("pt")))

    val caloriasMeta = 2100
    val caloriasConsumidas = 1685
    val protMeta = 190; val protConsumida = 145
    val carbsMeta = 160; val carbsConsumidos = 89
    val gordMeta = 65; val gordConsumida = 46

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dayName.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium) },
                actions = {
                    Text("90.0 kg", modifier = Modifier.padding(end = 16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: AddFood bottom sheet */ }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar refeição")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Círculo de calorias
            item {
                CaloriasCard(
                    consumidas = caloriasConsumidas,
                    meta = caloriasMeta
                )
            }

            // Barras de macros
            item {
                MacrosCard(
                    protConsumida = protConsumida, protMeta = protMeta,
                    carbsConsumidos = carbsConsumidos, carbsMeta = carbsMeta,
                    gordConsumida = gordConsumida, gordMeta = gordMeta
                )
            }

            // Treino do dia
            item {
                TreinoCard()
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun CaloriasCard(consumidas: Int, meta: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Calorias", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { consumidas.toFloat() / meta.toFloat() },
                    modifier = Modifier.size(140.dp),
                    strokeWidth = 12.dp,
                    color = OrangeCalorias,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$consumidas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("de $meta kcal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            val restantes = meta - consumidas
            Text(
                if (restantes > 0) "Faltam $restantes kcal" else "Meta atingida! 🎉",
                style = MaterialTheme.typography.bodyMedium,
                color = if (restantes > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MacrosCard(
    protConsumida: Int, protMeta: Int,
    carbsConsumidos: Int, carbsMeta: Int,
    gordConsumida: Int, gordMeta: Int
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Macros", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            MacroRow("🥩 Proteína", protConsumida, protMeta, "g", BlueProtein)
            MacroRow("🍞 Carbos", carbsConsumidos, carbsMeta, "g", OrangeCarbs)
            MacroRow("🧈 Gordura", gordConsumida, gordMeta, "g", YellowFat)
        }
    }
}

@Composable
fun MacroRow(label: String, consumed: Int, target: Int, unit: String, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("${consumed}${unit} / ${target}${unit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { consumed.toFloat() / target.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun TreinoCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f)) {
                Text("Treino de hoje", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Sem treino registado", style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = { /* TODO */ }) { Text("Registar") }
        }
    }
}
