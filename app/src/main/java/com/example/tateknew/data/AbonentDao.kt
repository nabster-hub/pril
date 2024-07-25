package com.example.tateknew.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface AbonentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAbonent(abonent: AbonentEntity)

    @Query("SELECT * FROM abonents WHERE clientId = :abonentId")
    fun getAbonentById(abonentId: Long): AbonentEntity
}
