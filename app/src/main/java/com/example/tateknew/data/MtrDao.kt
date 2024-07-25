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
               abonents.clientId AS abonent_clientId, 
               abonents.ctt AS abonent_ctt, 
               abonents.ct AS abonent_ct, 
               abonents.name AS abonent_name, 
               abonents.clientNo AS abonent_clientNo, 
               abonents.address AS abonent_address, 
               abonents.baseId AS abonent_baseId, 
               abonents.clientGr AS abonent_clientGr, 
               abonents.street AS abonent_street, 
               abonents.home AS abonent_home, 
               abonents.flat AS abonent_flat, 
               abonents.createdAt AS abonent_createdAt, 
               abonents.updatedAt AS abonent_updatedAt 
        FROM mtrs 
        JOIN abonents ON mtrs.abonentId = abonents.clientId 
        WHERE mtrs.nobjId = :objectId
    """)
    fun getAbonents(objectId: Int): List<MtrWithAbonent>

    @Query("SELECT * FROM mtrs WHERE abonentId = :abonentId")
    fun getMtrsByAbonentId(abonentId: Long): List<MtrEntity>
}
