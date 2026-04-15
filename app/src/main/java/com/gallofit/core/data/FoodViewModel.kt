package com.gallofit.core.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gallofit.core.data.database.FoodEntryEntity
import com.gallofit.core.data.database.GalloFitDatabase
import com.gallofit.core.data.repository.FoodRepository
import com.gallofit.core.domain.model.MealSlot
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DailyData(
    val entries: List<FoodEntryEntity> = emptyList(),
    val totalCalories: Int = 0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0,
    val caloriasGoal: Int = 2100
)

class FoodViewModel(app: Application) : AndroidViewModel(app) {

    private val db = GalloFitDatabase.getInstance(app)
    private val repo = FoodRepository(db.foodEntryDao())
    private val today = LocalDate.now().toString()

    private val _dailyData = MutableStateFlow(DailyData())
    val dailyData: StateFlow<DailyData> = _dailyData.asStateFlow()

    init {
        // Login anônimo se não tiver usuário
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }

        // Calcular meta do dia (Terça=2 ou Quinta=4 → 2200, resto → 2100)
        val dayOfWeek = LocalDate.now().dayOfWeek.value // 1=Seg, 2=Ter...
        val goal = if (dayOfWeek == 2 || dayOfWeek == 4) 2200 else 2100

        viewModelScope.launch {
            combine(
                repo.getEntriesForDate(today),
                repo.getTotalCalories(today),
                repo.getTotalProtein(today),
                repo.getTotalCarbs(today),
                repo.getTotalFat(today)
            ) { entries, kcal, prot, carbs, fat ->
                DailyData(
                    entries = entries,
                    totalCalories = kcal ?: 0,
                    totalProtein = prot ?: 0.0,
                    totalCarbs = carbs ?: 0.0,
                    totalFat = fat ?: 0.0,
                    caloriasGoal = goal
                )
            }.collect { _dailyData.value = it }
        }
    }

    fun addEntry(slot: MealSlot, name: String, calories: Int, protein: Double, carbs: Double, fat: Double) {
        viewModelScope.launch {
            repo.addEntry(
                FoodEntryEntity(
                    date = today,
                    mealSlot = slot.name,
                    foodName = name,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat
                )
            )
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch { repo.deleteEntry(id) }
    }
}
