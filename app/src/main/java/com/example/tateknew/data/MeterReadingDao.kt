package com.example.tateknew.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MeterReadingDao {

    @Insert
    fun insertMeterReading(meterReading: MeterReading)

    @Query("SELECT * FROM meter_readings WHERE mtrId = :mtrId ORDER BY createdAt DESC")
    fun getMeterReadingsForMtr(mtrId: Long): LiveData<List<MeterReading>>

    @Query("SELECT * FROM meter_readings WHERE mtrId = :mtrId ORDER BY createdAt DESC LIMIT 1")
    fun getLastMeterReadingForMtr(mtrId: Long): MeterReading?
}
