package com.example.mappe2

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object Utilities {
    fun fromDateTimeToString(time: LocalDateTime): String {
        Log.d("TIME", time.toString())
        val dateAndTime = time.toString().split("T")
        val date = dateAndTime[0].split("-")
        return "${date[2]}/${date[1]}/${date[0]} ${dateAndTime[1]}"
    }
}