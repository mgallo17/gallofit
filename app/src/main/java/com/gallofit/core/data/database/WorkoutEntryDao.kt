package com.gallofit.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutEntryDao {
    @Query("SELECT * FROM workout_entries ORDER BY timestamp DESC LIMIT 30")
    fun getRecent(): Flow<List<WorkoutEntryEntity>>

    @Query("SELECT * FROM workout_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getByDate(date: String): Flow<List<WorkoutEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WorkoutEntryEntity): Long

    @Query("DELETE FROM workout_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
