package com.example.tateknew.tasks

data class ObjectItem(
    val id: Int,
    val name: String,
    val baseId: Int,
    val fullName: String,
    val createdAt: String,
    val updatedAt: String
)

data class MtrItem(
    val id: Int,
    val abonentId: Int,
    val nobjId: Int,
    val baseId: Int,
    val name: String,
    val puName: String,
    val itemNo: String,
    val status: Int,
    val ecapId: Int,
    val sredrashod: Int,
    val vl: String,
    val ktt: Int,
    val createdAt: String,
    val updatedAt: String
)
