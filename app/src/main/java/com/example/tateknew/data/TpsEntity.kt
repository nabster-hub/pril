package com.example.tateknew.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tps")
data class TpsEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val baseId: Int,
    val fullName: String,
    val createdAt: String,
    val updatedAt: String
)
