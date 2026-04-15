package com.gallofit.core.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gallofit.core.data.database.FoodEntryEntity
import com.gallofit.core.data.database.GalloFitDatabase
import com.gallofit.core.data.database.GymSetEntity
import com.gallofit.core.data.database.WorkoutEntryEntity
import com.gallofit.core.data.remote.FoodSearchResult
import com.gallofit.core.data.remote.FoodSearchService
import com.gallofit.core.data.repository.FoodRepository
import com.gallofit.core.domain.model.MealSlot
import com.gallofit.feature.gym.Exercise
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
    val caloriasGoal: Int = 2100,
    val proteinGoal: Int = 190,
    val carbsGoal: Int = 160,
    val fatGoal: Int = 65
)

data class WorkoutUiState(
    val recentWorkouts: List<WorkoutEntryEntity> = emptyList(),
    val todayWorkouts: List<WorkoutEntryEntity> = emptyList()
)

data class GymUiState(
    val todaySets: List<GymSetEntity> = emptyList()
)

data class FoodSearchState(
    val query: String = "",
    val results: List<FoodSearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class HealthConnectState(
    val stepsToday: Int = 0,
    val caloriesBurnedToday: Int = 0,
    val isConnected: Boolean = false
)

class FoodViewModel(app: Application) : AndroidViewModel(app) {

    private val db = GalloFitDatabase.getInstance(app)
    private val repo = FoodRepository(db.foodEntryDao())
    private val workoutDao = db.workoutEntryDao()
    private val gymDao = db.gymSetDao()
    private val today = LocalDate.now().toString()

    private val _dailyData = MutableStateFlow(DailyData())
    val dailyData: StateFlow<DailyData> = _dailyData.asStateFlow()

    private val _workoutState = MutableStateFlow(WorkoutUiState())
    val workoutState: StateFlow<WorkoutUiState> = _workoutState.asStateFlow()

    private val _gymState = MutableStateFlow(GymUiState())
    val gymState: StateFlow<GymUiState> = _gymState.asStateFlow()

    private val _searchState = MutableStateFlow(FoodSearchState())
    val searchState: StateFlow<FoodSearchState> = _searchState.asStateFlow()

    private val _profile = MutableStateFlow(ProfileStore.load(app))
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _healthConnect = MutableStateFlow(HealthConnectState())
    val healthConnect: StateFlow<HealthConnectState> = _healthConnect.asStateFlow()

    init {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) auth.signInAnonymously()

        val profile = ProfileStore.load(app)
        val dayOfWeek = LocalDate.now().dayOfWeek.value
        val goal = if (dayOfWeek == 2 || dayOfWeek == 4) profile.caloriesDouble else profile.caloriesNormal

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
                    caloriasGoal = goal,
                    proteinGoal = profile.proteinG,
                    carbsGoal = profile.carbsG,
                    fatGoal = profile.fatG
                )
            }.collect { _dailyData.value = it }
        }

        viewModelScope.launch {
            combine(workoutDao.getRecent(), workoutDao.getByDate(today)) { recent, todayList ->
                WorkoutUiState(recentWorkouts = recent, todayWorkouts = todayList)
            }.collect { _workoutState.value = it }
        }

        viewModelScope.launch {
            gymDao.getByDate(today).collect { sets ->
                _gymState.value = GymUiState(todaySets = sets)
            }
        }
    }

    fun saveProfile(profile: UserProfile) {
        ProfileStore.save(getApplication(), profile)
        _profile.value = profile
    }

    // ── Food ──────────────────────────────────────────────────────────────

    fun addEntry(slot: MealSlot, name: String, calories: Int, protein: Double, carbs: Double, fat: Double) {
        viewModelScope.launch {
            repo.addEntry(FoodEntryEntity(date = today, mealSlot = slot.name, foodName = name, calories = calories, protein = protein, carbs = carbs, fat = fat))
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch { repo.deleteEntry(id) }
    }

    // ── Workout ───────────────────────────────────────────────────────────

    fun addWorkout(type: String, durationMin: Int, caloriesBurned: Int, notes: String) {
        viewModelScope.launch {
            workoutDao.insert(WorkoutEntryEntity(date = today, type = type, durationMin = durationMin, caloriesBurned = caloriesBurned, notes = notes))
        }
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch { workoutDao.deleteById(id) }
    }

    // ── Gym ───────────────────────────────────────────────────────────────

    fun addGymSet(exercise: Exercise, setNumber: Int, weightKg: Double, reps: Int) {
        viewModelScope.launch {
            gymDao.insert(GymSetEntity(
                date = today, exerciseId = exercise.id, exerciseName = exercise.name,
                muscleGroup = exercise.muscleGroup, setNumber = setNumber, weightKg = weightKg, reps = reps
            ))
        }
    }

    fun deleteGymSet(id: Long) {
        viewModelScope.launch { gymDao.deleteById(id) }
    }

    // ── Search ────────────────────────────────────────────────────────────

    fun searchFood(query: String) {
        if (query.length < 2) { _searchState.value = FoodSearchState(query = query); return }
        _searchState.value = FoodSearchState(query = query, isLoading = true)
        viewModelScope.launch {
            val results = FoodSearchService.search(query)
            _searchState.value = FoodSearchState(query = query, results = results, isLoading = false,
                error = if (results.isEmpty()) "Nenhum resultado encontrado" else null)
        }
    }

    fun updateHealthConnect(state: HealthConnectState) { _healthConnect.value = state }

    fun clearSearch() { _searchState.value = FoodSearchState() }
}
