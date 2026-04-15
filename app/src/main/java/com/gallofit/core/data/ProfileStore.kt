package com.gallofit.core.data

import android.content.Context
import android.content.SharedPreferences

data class UserProfile(
    val name: String = "Matheus Gallo",
    val weightKg: Double = 90.0,
    val heightCm: Int = 177,
    val goalWeightKg: Double = 85.5,
    val caloriesNormal: Int = 2100,
    val caloriesDouble: Int = 2200,
    val proteinG: Int = 190,
    val carbsG: Int = 160,
    val fatG: Int = 65
)

object ProfileStore {
    private const val PREFS = "gallofit_profile"

    fun load(ctx: Context): UserProfile {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return UserProfile(
            name = p.getString("name", "Matheus Gallo") ?: "Matheus Gallo",
            weightKg = p.getFloat("weight", 90f).toDouble(),
            heightCm = p.getInt("height", 177),
            goalWeightKg = p.getFloat("goal_weight", 85.5f).toDouble(),
            caloriesNormal = p.getInt("cal_normal", 2100),
            caloriesDouble = p.getInt("cal_double", 2200),
            proteinG = p.getInt("protein", 190),
            carbsG = p.getInt("carbs", 160),
            fatG = p.getInt("fat", 65)
        )
    }

    fun save(ctx: Context, profile: UserProfile) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().apply {
            putString("name", profile.name)
            putFloat("weight", profile.weightKg.toFloat())
            putInt("height", profile.heightCm)
            putFloat("goal_weight", profile.goalWeightKg.toFloat())
            putInt("cal_normal", profile.caloriesNormal)
            putInt("cal_double", profile.caloriesDouble)
            putInt("protein", profile.proteinG)
            putInt("carbs", profile.carbsG)
            putInt("fat", profile.fatG)
            apply()
        }
    }
}
