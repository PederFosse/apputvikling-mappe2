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
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.example.mappe2.Utilities.fromDateTimeToString
import com.example.mappe2.data.Appointment
import com.example.mappe2.data.Contact
import com.example.mappe2.data.ContactRoomDatabase
import kotlinx.coroutines.flow.collect
import java.time.LocalDate
import java.time.LocalDateTime
val TAG = "MELDINGGUTTA"




class MySendService : LifecycleService() {

    private var myAppointments: List<Appointment>? = null
    private var myContacts: List<Contact>? = null

    override fun onCreate() {
        super.onCreate()
        val db = ContactRoomDatabase.getDatabase(applicationContext)
        db.appointmentDao().getAppointments().asLiveData().observe(this) {
            appointments -> appointments.let {
            myAppointments = appointments
            }
        }

        db.contactDao().getContacts().asLiveData().observe(this) {
                contacts -> contacts.let {
            myContacts = contacts
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(applicationContext, "Started mySendService", Toast.LENGTH_SHORT).show()

        sendMessages(myAppointments, myContacts)

        return super.onStartCommand(intent, flags, startId)
    }



    fun sendMessages (listAppointments: List<Appointment>?, listContacts: List<Contact>?) {
        if (listAppointments === null || listContacts === null) {
            Toast.makeText(applicationContext, "No messages will be sent", Toast.LENGTH_SHORT).show()

            return }

        val sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val defaultMessage = sharedPref.getString("defaultMessage", null)

        var obj=SmsManager.getDefault()

        listAppointments.forEach{
            val contact = listContacts.find { c -> c.id == it.contactId }
            if (contact != null) {
                var message = "Appointment with " + contact.contactName + " at " + it.place
                val phonenumber = contact.phoneNumber

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
}