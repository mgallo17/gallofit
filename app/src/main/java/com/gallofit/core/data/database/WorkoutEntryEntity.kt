package com.gallofit.core.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_entries")
data class WorkoutEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,           // YYYY-MM-DD
    val type: String,           // GYM, TENNIS, CARDIO, OTHER
    val durationMin: Int,
    val caloriesBurned: Int = 0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
