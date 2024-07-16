package com.example.tateknew.data

import android.system.Int64Ref
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mtrs")
data class MtrEntity(
    @PrimaryKey val id: Int,
    val abonentId: Int,
    val nobjId: Int,
    val baseId: Int,
    val name: String,
    val puName: String,
    val itemNo: String,
    val status: Int,
    val ecapId: Int,
    val sredrashod: Int?,
    val vl: String,
    val ktt: Int,
    val createdAt: String,
    val updatedAt: String
)
