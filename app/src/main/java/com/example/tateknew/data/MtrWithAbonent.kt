package com.example.tateknew.data

import androidx.room.Embedded
import androidx.room.Relation

data class MtrWithAbonent(
    @Embedded val mtr: MtrEntity,
    @Embedded(prefix = "abonent_") val abonent: AbonentEntity
)
