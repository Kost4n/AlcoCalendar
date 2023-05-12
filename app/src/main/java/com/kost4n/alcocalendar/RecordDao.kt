package com.kost4n.alcocalendar

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecordDao {
    @Insert
    fun insert(record: Record)

    @Query("SELECT * FROM record")
    fun getRecords(): List<Record>
}