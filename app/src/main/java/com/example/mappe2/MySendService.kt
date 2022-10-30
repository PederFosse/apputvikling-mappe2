package com.example.mappe2

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.telephony.SmsManager
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import java.time.ZoneId

val TAG = "MELDINGGUTTA"




class MySendService : LifecycleService() {

    private var myAppointments: List<Appointment>? = null
    private var myContacts: List<Contact>? = null
    private val CHANNEL_ID = "channel01"

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

        val todaysMessages = myAppointments?.filter { appointment ->
            DateUtils.isToday(appointment.time.atZone(ZoneId.of("Europe/Oslo")).toInstant().toEpochMilli())
        }

        sendMessages(todaysMessages, myContacts)
        notificationPopup(myAppointments)

        return super.onStartCommand(intent, flags, startId)
    }

    fun notificationPopup(listAppointments: List<Appointment>?) {
        if (listAppointments === null) { return }

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Du har ${listAppointments?.size} avtale(r) i dag")
            .setContentText("Gå inn i appen for å lese mer om avtalene dine")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(101, builder.build())
        }
    }



    fun sendMessages (listAppointments: List<Appointment>?, listContacts: List<Contact>?) {
        if (listAppointments === null || listContacts === null) {
            Toast.makeText(applicationContext, "No messages will be sent", Toast.LENGTH_SHORT).show()
            return }

        // TODO: Filter out appointments that are today only

        val sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val defaultMessage = sharedPref.getString("defaultMessage", null)

        var obj=SmsManager.getDefault()

        listAppointments.forEach{
            val contact = listContacts.find { c -> c.id == it.contactId }
            val customMessage = null  // TODO: Peder fix :) Takk
            if (contact != null) {
                var message = "Appointment with " + contact.contactName + " at " + it.place + ". "
                val phonenumber = contact.phoneNumber
                if (customMessage===null) {
                    message += defaultMessage
                } else  {
                    message += customMessage
                }

                // TODO: Erstatt default message med appointment sin melding dersom den har en melding

                try {
                    obj.sendTextMessage(phonenumber, null, message, null, null)
                    Toast.makeText(applicationContext, "Message Sent to " + it.name, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Error, message not sent: "+e.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}