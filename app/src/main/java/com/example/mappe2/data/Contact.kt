package com.example.mappe2.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class Contact (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name="contact_name")
    val contactName: String,
    @ColumnInfo(name="phone_number")
    val phoneNumber: String,
)