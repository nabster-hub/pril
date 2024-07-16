package com.example.tateknew.data

import androidx.room.*

@Dao
interface MtrDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMtr(mtr: MtrEntity)
}
