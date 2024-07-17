package com.example.tateknew.data

import androidx.room.*

@Dao
interface MtrDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMtr(mtr: MtrEntity)

    @Query("SELECT * FROM mtrs WHERE nobjId = :objectId")
    fun getMtrsByObjectId(objectId: Int): List<MtrEntity>

    @Query("SELECT mtrs.*, abonents.* FROM mtrs JOIN abonents ON mtrs.abonentId = abonents.clientId WHERE mtrs.nobjId = :objectId")
    fun getAbonents(objectId: Int): List<MtrEntity>
}
