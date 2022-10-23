package com.example.mappe2.data

import android.content.ClipData
import android.content.Context
import androidx.room.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

// type converters for storing dates with Room
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): LocalDateTime {
        return Instant.ofEpochMilli(value)
            .atZone(ZoneId.of("Europe/Oslo")).toLocalDateTime()
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime): Long? {
        return date.atZone(ZoneId.of("Europe/Oslo")).toInstant().toEpochMilli()
    }
}

@Database(entities = [Contact::class, Appointment::class], version=5, exportSchema=false)
@TypeConverters(Converters::class)
abstract class ContactRoomDatabase: RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun appointmentDao(): AppointmentDao

    companion object{
        @Volatile
        private var INSTANCE: ContactRoomDatabase? = null
        fun getDatabase(context: Context): ContactRoomDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactRoomDatabase::class.java,
                    "contact_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }

}