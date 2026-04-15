package com.gallofit.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {
    @Query("SELECT * FROM food_entries WHERE date = :date ORDER BY timestamp ASC")
    fun getByDate(date: String): Flow<List<FoodEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FoodEntryEntity): Long

    @Query("DELETE FROM food_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT SUM(calories) FROM food_entries WHERE date = :date")
    fun getTotalCaloriesByDate(date: String): Flow<Int?>

    @Query("SELECT SUM(protein) FROM food_entries WHERE date = :date")
    fun getTotalProteinByDate(date: String): Flow<Double?>

    @Query("SELECT SUM(carbs) FROM food_entries WHERE date = :date")
    fun getTotalCarbsByDate(date: String): Flow<Double?>

    @Query("SELECT SUM(fat) FROM food_entries WHERE date = :date")
    fun getTotalFatByDate(date: String): Flow<Double?>
}
