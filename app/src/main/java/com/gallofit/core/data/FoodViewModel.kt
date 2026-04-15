package com.gallofit.core.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gallofit.core.data.database.FoodEntryEntity
import com.gallofit.core.data.database.GalloFitDatabase
import com.gallofit.core.data.database.WorkoutEntryEntity
import com.gallofit.core.data.remote.FoodSearchResult
import com.gallofit.core.data.remote.FoodSearchService
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

data class WorkoutUiState(
    val recentWorkouts: List<WorkoutEntryEntity> = emptyList(),
    val todayWorkouts: List<WorkoutEntryEntity> = emptyList()
)

data class FoodSearchState(
    val query: String = "",
    val results: List<FoodSearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class FoodViewModel(app: Application) : AndroidViewModel(app) {

    private val db = GalloFitDatabase.getInstance(app)
    private val repo = FoodRepository(db.foodEntryDao())
    private val workoutDao = db.workoutEntryDao()
    private val today = LocalDate.now().toString()

    private val _dailyData = MutableStateFlow(DailyData())
    val dailyData: StateFlow<DailyData> = _dailyData.asStateFlow()

    private val _workoutState = MutableStateFlow(WorkoutUiState())
    val workoutState: StateFlow<WorkoutUiState> = _workoutState.asStateFlow()

    private val _searchState = MutableStateFlow(FoodSearchState())
    val searchState: StateFlow<FoodSearchState> = _searchState.asStateFlow()

    init {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }

        val dayOfWeek = LocalDate.now().dayOfWeek.value
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

        viewModelScope.launch {
            combine(
                workoutDao.getRecent(),
                workoutDao.getByDate(today)
            ) { recent, todayList ->
                WorkoutUiState(recentWorkouts = recent, todayWorkouts = todayList)
            }.collect { _workoutState.value = it }
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

    fun addWorkout(type: String, durationMin: Int, caloriesBurned: Int, notes: String) {
        viewModelScope.launch {
            workoutDao.insert(
                WorkoutEntryEntity(
                    date = today,
                    type = type,
                    durationMin = durationMin,
                    caloriesBurned = caloriesBurned,
                    notes = notes
                )
            )
        }
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch { workoutDao.deleteById(id) }
    }

    fun searchFood(query: String) {
        if (query.length < 2) {
            _searchState.value = FoodSearchState(query = query)
            return
        }
        _searchState.value = FoodSearchState(query = query, isLoading = true)
        viewModelScope.launch {
            val results = FoodSearchService.search(query)
            _searchState.value = FoodSearchState(
                query = query,
                results = results,
                isLoading = false,
                error = if (results.isEmpty()) "Nenhum resultado encontrado" else null
            )
        }
    }

    fun clearSearch() {
        _searchState.value = FoodSearchState()
    }
}
