package com.example.tateknew.data

import androidx.room.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MtrDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMtr(mtr: MtrEntity)

    @Query("SELECT * FROM mtrs WHERE nobjId = :objectId")
    fun getMtrsByObjectId(objectId: Int): List<MtrEntity>

    @Query("""
        SELECT mtrs.*, 
               abonents.clientId, 
               abonents.ctt, 
               abonents.ct, 
               abonents.name AS abonentName, 
               abonents.clientNo, 
               abonents.address, 
               abonents.baseId AS abonentBaseId, 
               abonents.clientGr, 
               abonents.street, 
               abonents.home, 
               abonents.flat, 
               abonents.createdAt AS abonentCreatedAt, 
               abonents.updatedAt AS abonentUpdatedAt 
        FROM mtrs 
        JOIN abonents ON mtrs.abonentId = abonents.clientId 
        WHERE mtrs.nobjId = :objectId
    """)
    fun getAbonents(objectId: Int): List<MtrWithAbonent>
}
