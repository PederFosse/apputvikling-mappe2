package com.example.mappe2.data

import android.content.ClipData
import android.content.Context
import androidx.room.*
import java.util.*

// type converters for storing dates with Room
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

@Database(entities = [Contact::class, Appointment::class], version=4, exportSchema=false)
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