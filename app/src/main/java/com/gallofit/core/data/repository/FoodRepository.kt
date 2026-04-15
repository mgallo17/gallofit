package com.gallofit.core.data.repository

import com.gallofit.core.data.database.FoodEntryDao
import com.gallofit.core.data.database.FoodEntryEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FoodRepository(
    private val dao: FoodEntryDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun getEntriesForDate(date: String): Flow<List<FoodEntryEntity>> =
        dao.getByDate(date)

    fun getTotalCalories(date: String): Flow<Int?> = dao.getTotalCaloriesByDate(date)
    fun getTotalProtein(date: String): Flow<Double?> = dao.getTotalProteinByDate(date)
    fun getTotalCarbs(date: String): Flow<Double?> = dao.getTotalCarbsByDate(date)
    fun getTotalFat(date: String): Flow<Double?> = dao.getTotalFatByDate(date)

    suspend fun addEntry(entry: FoodEntryEntity): Long {
        val id = dao.insert(entry)
        syncToFirestore(entry.copy(id = id))
        return id
    }

    suspend fun deleteEntry(id: Long) {
        dao.deleteById(id)
    }

    private fun syncToFirestore(entry: FoodEntryEntity) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("food_entries")
            .document(entry.id.toString())
            .set(mapOf(
                "date" to entry.date,
                "mealSlot" to entry.mealSlot,
                "foodName" to entry.foodName,
                "calories" to entry.calories,
                "protein" to entry.protein,
                "carbs" to entry.carbs,
                "fat" to entry.fat,
                "timestamp" to entry.timestamp
            ))
    }
}
