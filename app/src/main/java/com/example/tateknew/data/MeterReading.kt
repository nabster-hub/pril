package com.example.tateknew.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "meter_readings")
data class MeterReading(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val mtrId: Long,
    val currentReading: BigDecimal,
    val photoPath: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Date
)
