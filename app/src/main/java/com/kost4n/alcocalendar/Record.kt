package com.kost4n.alcocalendar

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Record(
    val day: Int,
    val month: Int,
    val drink: String,
    val degree: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}