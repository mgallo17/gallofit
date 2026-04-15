package com.gallofit.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gym_sets")
data class GymSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,           // YYYY-MM-DD
    val exerciseId: String,
    val exerciseName: String,
    val muscleGroup: String,
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val timestamp: Long = System.currentTimeMillis()
)
