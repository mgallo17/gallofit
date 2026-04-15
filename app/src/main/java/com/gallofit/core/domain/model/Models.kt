package com.gallofit.core.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

// ─── Perfil ───────────────────────────────────────────────
data class UserProfile(
    val id: String = "local",
    val name: String = "Matheus",
    val heightCm: Int = 177,
    val startWeightKg: Double = 90.0,
    val goalWeightKg: Double = 85.5,
    val goalDays: Int = 30,
    val startDate: LocalDate = LocalDate.now(),
    val defaultMacros: MacroTargets = MacroTargets(),
    val doubleDayMacros: MacroTargets = MacroTargets(caloriesKcal = 2200),
    val doubleDays: Set<DayOfWeek> = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
)

data class MacroTargets(
    val caloriesKcal: Int = 2100,
    val proteinG: Int = 190,
    val carbsG: Int = 160,
    val fatG: Int = 65
)

enum class DayType { NORMAL, DOUBLE }

// ─── Alimentação ──────────────────────────────────────────
data class FoodEntry(
    val id: String,
    val date: LocalDate,
    val mealSlot: MealSlot,
    val name: String,
    val caloriesKcal: Int,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val quantity: Double = 1.0,
    val unit: String = "porção",
    val fromTemplateId: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val synced: Boolean = false
)

enum class MealSlot(val displayName: String, val emoji: String) {
    BREAKFAST("Pequeno-almoço", "🌅"),
    MORNING_SNACK("Lanche manhã", "🍎"),
    LUNCH("Almoço", "☀️"),
    AFTERNOON_SNACK("Lanche tarde", "🍪"),
    PRE_WORKOUT("Pré-treino", "⚡"),
    POST_WORKOUT("Pós-treino", "💪"),
    DINNER("Jantar", "🌙"),
    SUPPER("Ceia", "🌃")
}

data class MealTemplate(
    val id: String,
    val name: String,
    val items: List<TemplateItem>,
    val defaultSlot: MealSlot = MealSlot.LUNCH,
    val isFavorite: Boolean = false
) {
    val totalCalories: Int get() = items.sumOf { it.caloriesKcal }
    val totalProtein: Double get() = items.sumOf { it.proteinG }
    val totalCarbs: Double get() = items.sumOf { it.carbsG }
    val totalFat: Double get() = items.sumOf { it.fatG }
}

data class TemplateItem(
    val name: String,
    val caloriesKcal: Int,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val quantity: Double = 1.0,
    val unit: String = "porção"
)

// ─── Treino ───────────────────────────────────────────────
data class Workout(
    val id: String,
    val date: LocalDate,
    val type: WorkoutType,
    val durationMin: Int,
    val caloriesBurned: Int? = null,
    val notes: String? = null,
    val exercises: List<Exercise> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val synced: Boolean = false
)

enum class WorkoutType(val displayName: String, val emoji: String) {
    GYM("Ginásio", "🏋️"),
    TENNIS("Ténis", "🎾"),
    CARDIO("Cardio", "🏃"),
    OTHER("Outro", "⚽")
}

data class Exercise(
    val name: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Double? = null
)

// ─── Peso ─────────────────────────────────────────────────
data class WeightEntry(
    val id: String,
    val date: LocalDate,
    val weightKg: Double,
    val note: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val synced: Boolean = false
)

// ─── Resumo diário ────────────────────────────────────────
data class DailySummary(
    val date: LocalDate,
    val dayType: DayType,
    val targets: MacroTargets,
    val consumed: ConsumedMacros,
    val meals: Map<MealSlot, List<FoodEntry>>,
    val workouts: List<Workout>,
    val weight: WeightEntry?
) {
    val caloriesRemaining: Int get() = targets.caloriesKcal - consumed.caloriesKcal
    val proteinRemaining: Double get() = targets.proteinG - consumed.proteinG
}

data class ConsumedMacros(
    val caloriesKcal: Int = 0,
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0
)
