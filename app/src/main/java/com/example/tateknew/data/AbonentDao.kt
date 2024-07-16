package com.example.tateknew.data

import androidx.room.*

@Dao
interface AbonentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAbonent(abonent: AbonentEntity)
}
