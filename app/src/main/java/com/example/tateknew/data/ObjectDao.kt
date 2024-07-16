package com.example.tateknew.data

import androidx.room.*

@Dao
interface ObjectDao {
    @Query("SELECT * FROM objects")
    fun getAllObjects(): List<ObjectEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertObject(obj: ObjectEntity)

    @Query("SELECT * FROM mtrs WHERE nobjId = :objectId")
    fun getMtrsByObjectId(objectId: Int): List<MtrEntity>
}