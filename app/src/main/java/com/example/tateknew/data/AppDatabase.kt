package com.example.tateknew.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [ObjectEntity::class, MtrEntity::class, AbonentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun objectDao(): ObjectDao
    abstract fun mtrDao(): MtrDao
    abstract fun abonentDao(): AbonentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
