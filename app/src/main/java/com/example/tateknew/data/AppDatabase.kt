package com.example.tateknew.data


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tateknew.data.converters.Converters
import com.example.tateknew.data.converters.DateConverter

@Database(entities = [TpsEntity::class, MtrEntity::class, AbonentEntity::class, MeterReading::class], version = 1)
@TypeConverters(DateConverter::class, Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun objectDao(): TpsDao
    abstract fun mtrDao(): MtrDao
    abstract fun abonentDao(): AbonentDao
    abstract fun meterReadingDao(): MeterReadingDao

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
