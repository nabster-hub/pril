package com.example.tateknew.data


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tateknew.data.converters.DateConverter

@Database(entities = [TpsEntity::class, MtrEntity::class, AbonentEntity::class, MeterReading::class], version = 3)
@TypeConverters(DateConverter::class)
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

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `meter_readings` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            "`mtrId` INTEGER NOT NULL," +
                            "`currentReading` REAL NOT NULL," +
                            "`photoPath` TEXT NOT NULL," +
                            "`latitude` REAL NOT NULL," +
                            "`longitude` REAL NOT NULL," +
                            "`createdAt` INTEGER NOT NULL)"
                )
            }
        }
    }
}
