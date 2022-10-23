package com.example.mappe2.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "appointment")
data class Appointment (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val place: String,
    val time: LocalDateTime,
    val contactId: Int,
)
