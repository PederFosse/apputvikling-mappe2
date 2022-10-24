package com.example.mappe2

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.example.mappe2.Utilities.fromDateTimeToString
import com.example.mappe2.data.Appointment
import com.example.mappe2.data.ContactRoomDatabase
import kotlinx.coroutines.flow.collect
import java.time.LocalDate
import java.time.LocalDateTime
val TAG = "MELDINGGUTTA"
class MySendService : Service() {




    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(applicationContext, "Started mySendService", Toast.LENGTH_SHORT).show()

        sendMessages(getAllAppointments())

        return super.onStartCommand(intent, flags, startId)
    }

    fun getAllAppointments (): List<Appointment> {
        val list = mutableListOf<Appointment>()

        // TODO: Replace mock data with database lookup results
        val app1 = Appointment(
            id = 0, name = "Rob", place = "Berlin", time = LocalDateTime.now(), contactId = 1)
        val app2 = Appointment(
            id = 1, name = "Peder", place = "Frogner", time = LocalDateTime.now(), contactId = 2)
        val app3 = Appointment(
            id = 2, name = "Fredrik", place = "Tøyen", time = LocalDateTime.now(), contactId = 3)
        list += app1
        list += app2
        list += app3

        return list
    }

    fun sendMessages (list: List<Appointment>) {
        val sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val defaultMessage = sharedPref.getString("defaultMessage", null)

        var obj=SmsManager.getDefault()

        val db = ContactRoomDatabase.getDatabase(applicationContext)
        val appointments = db.appointmentDao().getAppointments().asLiveData().value

        list.forEach{
            val contact = db.contactDao().getContact(it.contactId).asLiveData().value
            var  message = "Appointment " + fromDateTimeToString(it.time) + " with " + contact?.contactName + " at " + it.place // TODO: Get message from appointment
            val phonenumber = it.contactId.toString() + "-0-" + it.contactId.toString() // TODO: Get phonenumber from contactId

            if (message == "" && defaultMessage != null) message = defaultMessage

            try {
                obj.sendTextMessage(phonenumber, null, message + defaultMessage, null, null)
                Toast.makeText(applicationContext, "Message Sent to " + it.name, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Error, message not sent: "+e.message.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}