package com.example.tateknew.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ObjectDao {
    @Query("SELECT * FROM objects")
    fun getAllObjects(): LiveData<List<ObjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertObject(obj: ObjectEntity)

    @Query("SELECT * FROM mtrs WHERE nobjId = :objectId")
    fun getMtrsByObjectId(objectId: Int): List<MtrEntity>
}