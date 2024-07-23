package com.example.tateknew.data

data class MtrWithAbonent(
    val id: Long,
    val abonentId: Long,
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
    val updatedAt: String,
    val clientId: Long,
    val ctt: String,
    val ct: Int,
    val abonentName: String, // renamed to avoid conflict
    val clientNo: Int,
    val address: String,
    val abonentBaseId: Int, // renamed to avoid conflict
    val clientGr: String,
    val street: String,
    val home: String,
    val flat: String,
    val abonentCreatedAt: String, // renamed to avoid conflict
    val abonentUpdatedAt: String // renamed to avoid conflict
)