package com.example.mappe2.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(appointment: Appointment)

    @Update
    suspend fun update(appointment: Appointment)

    @Delete
    suspend fun delete(appointment: Appointment)

    @Query("SELECT * FROM appointment WHERE id = :id")
    fun getAppointment(id: Int): Flow<Appointment>

    @Query("SELECT * FROM appointment ORDER BY name ASC")
    fun getAppointments(): Flow<List<Appointment>>
}