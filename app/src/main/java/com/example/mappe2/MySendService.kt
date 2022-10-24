package com.example.mappe2

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.widget.Toast
import com.example.mappe2.data.Appointment
import java.time.LocalDate
import java.time.LocalDateTime

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

        // mock data
        val app1 = Appointment(
            id = 0, name = "Rob", place = "Oslo", time = LocalDateTime.now(), contactId = 1)
        list += app1

        return list
    }

    fun sendMessages (list: List<Appointment>) {
        Toast.makeText(applicationContext, "Sending messages to ...", Toast.LENGTH_SHORT).show()

        var obj=SmsManager.getDefault()

        list.forEach {
            try {
                obj.sendTextMessage("90965665", null, "Appointment with " + it.name, null, null)
                Toast.makeText(applicationContext, "Message Sent to " + it.name, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Error, message not sent: "+e.message.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        }



    }

}