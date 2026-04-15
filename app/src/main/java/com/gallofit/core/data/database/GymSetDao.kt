package com.gallofit.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GymSetDao {
    @Query("SELECT * FROM gym_sets WHERE date = :date ORDER BY timestamp ASC")
    fun getByDate(date: String): Flow<List<GymSetEntity>>

    @Query("SELECT * FROM gym_sets WHERE exerciseId = :exerciseId ORDER BY date DESC, setNumber ASC LIMIT 20")
    fun getLastSetsForExercise(exerciseId: String): Flow<List<GymSetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: GymSetEntity): Long

    @Query("DELETE FROM gym_sets WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT DISTINCT exerciseId, exerciseName, muscleGroup, MAX(date) as lastDate FROM gym_sets GROUP BY exerciseId ORDER BY lastDate DESC LIMIT 10")
    fun getRecentExercises(): Flow<List<RecentExercise>>
}

data class RecentExercise(
    val exerciseId: String,
    val exerciseName: String,
    val muscleGroup: String,
    val lastDate: String
)
