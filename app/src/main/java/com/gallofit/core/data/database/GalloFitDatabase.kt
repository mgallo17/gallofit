package com.gallofit.core.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [FoodEntryEntity::class], version = 1, exportSchema = false)
abstract class GalloFitDatabase : RoomDatabase() {
    abstract fun foodEntryDao(): FoodEntryDao

    companion object {
        @Volatile private var INSTANCE: GalloFitDatabase? = null

        fun getInstance(context: Context): GalloFitDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    GalloFitDatabase::class.java,
                    "gallofit.db"
                ).build().also { INSTANCE = it }
            }
    }
}
