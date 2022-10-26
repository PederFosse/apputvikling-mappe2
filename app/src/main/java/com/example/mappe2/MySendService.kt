package com.example.mappe2

import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.asLiveData
import com.example.mappe2.data.Appointment
import com.example.mappe2.data.Contact
import com.example.mappe2.data.ContactRoomDatabase

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

        sendMessages(myAppointments, myContacts)
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
        Log.d("channel01", "SMS runde...")
        if (listAppointments === null || listContacts === null) {
            Toast.makeText(applicationContext, "No messages will be sent", Toast.LENGTH_SHORT).show()
            return }

        // TODO: Filter out appointments that are today only

        val sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val defaultMessage = sharedPref.getString("defaultMessage", null)

        var smsManager=SmsManager.getDefault()

        listAppointments.forEach{
            val contact = listContacts.find { c -> c.id == it.contactId }
            val customMessage = it.message
            Log.d(TAG, customMessage)

            if (contact != null ) {
                var message = "Appointment with " + contact.contactName + " at " + it.place + ". "
                val phonenumber = contact.phoneNumber
                message += if (customMessage=="") {
                    defaultMessage
                } else  {
                    customMessage
                }

                try {
                    Log.d(TAG, phonenumber)
                    smsManager.sendTextMessage(phonenumber, null, message, null, null)
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Success!!")
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "Error, message not sent: "+e.message.toString(), Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Failure")
                }
            } else {
                Log.d(TAG, "Contact was null")
            }
        }
    }
}