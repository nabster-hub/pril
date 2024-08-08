package com.example.tateknew.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TpsDao {
    @Query("SELECT * FROM Tps")
    fun getAllObjects(): LiveData<List<TpsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertObject(obj: TpsEntity)

    @Query("SELECT * FROM mtrs WHERE nobjId = :objectId")
    fun getMtrsByObjectId(objectId: Int): List<MtrEntity>
}