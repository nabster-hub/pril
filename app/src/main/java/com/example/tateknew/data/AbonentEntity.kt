package com.example.tateknew.data

import android.system.Int64Ref
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "abonents")
data class AbonentEntity(
    @PrimaryKey val clientId: Long,
    val ctt: String,
    val ct: Int,
    val name: String,
    val clientNo: Long,
    val address: String,
    val baseId: Int,
    val clientGr: String,
    val street: String,
    val home: String,
    val flat: String,
    val createdAt: String,
    val updatedAt: String
)
