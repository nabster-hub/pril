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
               abonents.updatedAt AS abonent_updatedAt,
               CASE 
                   WHEN meter_readings.id IS NOT NULL THEN 1 
                   ELSE 0 
               END AS exist_meter 
        FROM mtrs 
        JOIN abonents ON mtrs.abonentId = abonents.clientId 
        LEFT JOIN meter_readings ON mtrs.id = meter_readings.mtrId
        WHERE mtrs.nobjId = :objectId GROUP BY mtrs.abonentId
        ORDER BY exist_meter ASC, abonents.street, 
                    CAST(abonents.home AS INTEGER), 
                    CAST(abonents.flat AS INTEGER)
    """)
    fun getAbonents(objectId: Int): List<MtrWithAbonent>

    @Query("SELECT * FROM mtrs WHERE abonentId = :abonentId and nobjId = :nobjId")//сделать сбор данных mtr только по текущей тп, без выбора других
    fun getMtrsByAbonentId(abonentId: Long, nobjId: Int): List<MtrEntity>

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
        WHERE mtrs.id = :mtrId GROUP BY mtrs.abonentId order by abonent_clientNo
    """)
    fun getMtr(mtrId: Long): List<MtrWithAbonent>
}
