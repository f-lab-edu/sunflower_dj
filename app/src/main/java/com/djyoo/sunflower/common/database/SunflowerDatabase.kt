package com.djyoo.sunflower.common.database

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.djyoo.sunflower.screen.plant.data.dao.PlantDao
import com.djyoo.sunflower.screen.plant.data.model.Plant

@Database(entities = [Plant::class], version = 1, exportSchema = false)
abstract class SunflowerDatabase : RoomDatabase() {

    abstract fun plantDao(): PlantDao

    companion object {
        private const val DATABASE_NAME = "sunflower_database"

        @Volatile
        private var instance: SunflowerDatabase? = null

        fun getInstance(context: Context): SunflowerDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SunflowerDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { instance = it }
            }
        }

        @VisibleForTesting
        fun setInstance(database: SunflowerDatabase) {
            instance = database
        }
    }
}
